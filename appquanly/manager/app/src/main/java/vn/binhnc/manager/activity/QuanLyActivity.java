package vn.binhnc.manager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.binhnc.manager.R;
import vn.binhnc.manager.adapter.QuanLyAdapter;
import vn.binhnc.manager.constant.Constant;
import vn.binhnc.manager.constant.GlobalFuntion;
import vn.binhnc.manager.model.EventBus.SuaXoaEvent;
import vn.binhnc.manager.model.SanPham;
import vn.binhnc.manager.retrofit.ApiBanHang;
import vn.binhnc.manager.retrofit.RetrofitClient;
import vn.binhnc.manager.utils.NetWorkChangeListener;
import vn.binhnc.manager.utils.Utils;

public class QuanLyActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private Toolbar toolbar;
    private RecyclerView rcvManage;
    private LinearLayoutManager linearLayoutManager;
    private ImageView imgClearText;
    private ImageView imgAdd;
    private EditText edtSearch;
    private QuanLyAdapter adapter;
    private List<SanPham> mList;
    private ApiBanHang apiBanHang;
    private SanPham sanPhamSuaXoa;
    private CompositeDisposable compositeDisposable;
    private int mPage = 1;
    private Handler mHandler;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_ly);
        mList = new ArrayList<>();
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        compositeDisposable = new CompositeDisposable();
        mHandler = new Handler();
        //=>
        initView();
        initToolBar();
        initControl();
        getData(mPage);
        addEventLoading();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_manage);
        imgAdd = findViewById(R.id.img_them);
        edtSearch = findViewById(R.id.edt_search_manage);
        imgClearText = findViewById(R.id.img_clear_text_manage);
        rcvManage = findViewById(R.id.rcv_search_manage);
        linearLayoutManager = new GridLayoutManager(this, 2);
        rcvManage.setHasFixedSize(true);
        rcvManage.setLayoutManager(linearLayoutManager);
        //=>
        edtSearch.requestFocus();
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Hiển thị nút quay lại
            actionBar.setDisplayHomeAsUpEnabled(true);
            // Set icon cho nút quay lại
            Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24);
            if (upArrow != null) {
                DrawableCompat.setTint(upArrow, Color.RED);
                actionBar.setHomeAsUpIndicator(upArrow);
            }
        }
        toolbar.setTitle("Quản lý");
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void initControl() {
        imgAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, ThemSPActivity.class);
            startActivity(intent);
        });
        imgClearText.setOnClickListener(v -> edtSearch.setText(""));
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imgClearText.setVisibility(View.GONE);
                } else {
                    imgClearText.setVisibility(View.VISIBLE);
                }
                mPage = 1;
                getData(mPage);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getData(int page) {
        String strSearch = edtSearch.getText().toString().trim();
        compositeDisposable.add(apiBanHang.searchManage(page, strSearch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamModel -> {
                            if (sanPhamModel.isSuccess()) {
                                if (!mList.isEmpty()) {
                                    mList.clear();
                                }
                                mList = sanPhamModel.getResult();
                                // Khởi tạo adapter
                                adapter = new QuanLyAdapter(mList, this::goToSanPhamDetail);
                                adapter.notifyDataSetChanged();
                                rcvManage.setAdapter(adapter);
                            }
                        }, throwable -> Toast.makeText(getApplicationContext(),
                                "Không kết nối được với server" + throwable.getMessage(),
                                Toast.LENGTH_LONG).show()
                ));
    }

    private void addEventLoading() {
        rcvManage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == mList.size() - 1) {
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });
    }

    private void loadMore() {
        mHandler.post(() -> {
            // add null
            mList.add(null);
            adapter.notifyItemInserted(mList.size() - 1);
        });
        mHandler.postDelayed(() -> {
            // remove null
            mList.remove(mList.size() - 1);
            adapter.notifyItemRemoved(mList.size());
            mPage += 1;
            getData(mPage);
            adapter.notifyDataSetChanged();
            isLoading = false;
        }, 1000);
    }

    private void goToSanPhamDetail(@NonNull SanPham sanPham) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_SANPHAM_OBJECT, sanPham);
        GlobalFuntion.startActivity(this, ChitietSanPhamActivity.class, bundle);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (Objects.equals(item.getTitle(), "Sửa")) {
            suaSanPham();
        } else if (Objects.equals(item.getTitle(), "Xóa")) {
            xoaSanPham();
        }
        return super.onContextItemSelected(item);
    }

    private void xoaSanPham() {
        compositeDisposable.add(apiBanHang.xoaSanPham(sanPhamSuaXoa.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()) {
                                Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                                getData(mPage);
                            } else {
                                Toast.makeText(getApplicationContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }, throwable -> Log.d("log_xoa", Objects.requireNonNull(throwable.getMessage()))
                ));
    }

    private void suaSanPham() {
        Intent intent = new Intent(getApplicationContext(), ThemSPActivity.class);
        intent.putExtra("sua", sanPhamSuaXoa);
        startActivity(intent);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventSuaXoa(SuaXoaEvent event) {
        if (event != null) {
            sanPhamSuaXoa = event.getSanPham();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkChangeListener, filter);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(netWorkChangeListener);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}