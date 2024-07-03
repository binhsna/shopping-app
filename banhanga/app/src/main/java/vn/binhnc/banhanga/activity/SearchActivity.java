package vn.binhnc.banhanga.activity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.binhnc.banhanga.AnimationUtil.AnimationUtil;
import vn.binhnc.banhanga.R;
import vn.binhnc.banhanga.adapter.TimKiemAdapter;
import vn.binhnc.banhanga.constant.Constant;
import vn.binhnc.banhanga.constant.GlobalFuntion;
import vn.binhnc.banhanga.database.LocalDatabase;
import vn.binhnc.banhanga.databinding.ActivitySearchBinding;
import vn.binhnc.banhanga.event.ReloadListCartEvent;
import vn.binhnc.banhanga.model.GioHang;
import vn.binhnc.banhanga.model.SanPham;
import vn.binhnc.banhanga.retrofit.ApiBanHang;
import vn.binhnc.banhanga.retrofit.RetrofitClient;
import vn.binhnc.banhanga.utils.NetWorkChangeListener;
import vn.binhnc.banhanga.utils.Utils;

public class SearchActivity extends BaseActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private ActivitySearchBinding mSearchBinding;
    private List<SanPham> mListSanPham = new ArrayList<>();
    private TimKiemAdapter mTimKiemAdapter;
    private ApiBanHang apiBanHang;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    //=>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Chỉnh toàn màn hình
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        mSearchBinding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(mSearchBinding.getRoot());
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
        mSearchBinding.rcvSearch.setHasFixedSize(true);
        mSearchBinding.rcvSearch.setLayoutManager(layoutManager);
        //
        mSearchBinding.imgBack.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        // Lấy dữ liệu load luôn khi vào activity
        getDataSearch("");
        mSearchBinding.imgBack.setOnClickListener(v -> {
            finish();
        });
        mSearchBinding.imgMic.setOnClickListener(e -> {
            speak();
        });
        mSearchBinding.imgClearText.setOnClickListener(v -> {
            mSearchBinding.edtSearchName.setText("");
        });
        mSearchBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mSearchBinding.imgClearText.setVisibility(View.GONE);
                    mListSanPham.clear();
                    mTimKiemAdapter = new TimKiemAdapter(mListSanPham,
                            SearchActivity.this::goToSanPhamDetail,
                            SearchActivity.this::animationAddToCart);
                    mSearchBinding.rcvSearch.setAdapter(mTimKiemAdapter);
                } else {
                    mSearchBinding.imgClearText.setVisibility(View.VISIBLE);
                    getDataSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Nói gì đó");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataSearch(String s) {
        mListSanPham.clear();
        compositeDisposable.add(apiBanHang.search(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sanPhamModel -> {
                            if (sanPhamModel.isSuccess()) {
                                mListSanPham = sanPhamModel.getResult();
                                mTimKiemAdapter = new TimKiemAdapter(mListSanPham,
                                        SearchActivity.this::goToSanPhamDetail,
                                        SearchActivity.this::animationAddToCart);
                                mSearchBinding.rcvSearch.setAdapter(mTimKiemAdapter);
                            } else {
                                mListSanPham.clear();
                                mTimKiemAdapter.notifyDataSetChanged();
                            }
                        }, throwable -> {
                            /*Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();*/
                        }
                ));
    }

    private void goToSanPhamDetail(@NonNull SanPham sanPham) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_SANPHAM_OBJECT, sanPham);
        GlobalFuntion.startActivity(getApplicationContext(), ChitietSanPhamActivity.class, bundle);
    }

    private void animationAddToCart(final ImageView imgAddToCart, @NonNull SanPham sanPham) {
        mSearchBinding.imgBoxCartAnimation.setVisibility(View.VISIBLE);
        mSearchBinding.imgBoxCartAnimation.setElevation(10);
        AnimationUtil.translateAnimation(mSearchBinding.viewAnimation, imgAddToCart,
                mSearchBinding.viewEndAnimation, new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        sanPham.setAddToCart(true);
                        //=>
                        GioHang mGioHang = new GioHang();
                        mGioHang.setIdsp(sanPham.getId());
                        mGioHang.setTensp(sanPham.getTensp());
                        mGioHang.setHinhsp(sanPham.getHinhanh());
                        mGioHang.setSoluong(1);
                        mGioHang.setSltonkho(sanPham.getSltonkho());
                        mGioHang.setGiamgia(sanPham.getGiamgia());
                        mGioHang.setGiasp(Integer.parseInt(sanPham.getGiasp()));
                        mGioHang.setTotalPrice(sanPham.getGiaThat());
                        mGioHang.setAddToCart(true);
                        mGioHang.setChecked(false);
                        LocalDatabase.getInstance(SearchActivity.this).GioHangDAO().insertItem(mGioHang);
                        //<=
                        mSearchBinding.imgBoxCartAnimation.setVisibility(View.INVISIBLE);
                        imgAddToCart.setBackgroundResource(R.drawable.bg_gray_corner_6);
                        EventBus.getDefault().post(new ReloadListCartEvent());
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                assert result != null;
                mSearchBinding.edtSearchName.setText(result.get(0));
            }
        }
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkChangeListener, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(netWorkChangeListener);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}