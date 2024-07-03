package vn.binhnc.manager.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.binhnc.manager.R;
import vn.binhnc.manager.activity.MainActivity;
import vn.binhnc.manager.adapter.RecyclerViewFavoriteAdapter;
import vn.binhnc.manager.database.LocalDatabase;
import vn.binhnc.manager.databinding.FragmentFavoriteBinding;
import vn.binhnc.manager.model.SanPham;
import vn.binhnc.manager.model.YeuThich;
import vn.binhnc.manager.retrofit.ApiBanHang;
import vn.binhnc.manager.retrofit.RetrofitClient;
import vn.binhnc.manager.utils.Utils;

public class FavoriteFragment extends BaseFragment {
    private FragmentFavoriteBinding mFragmentFavoriteBinding;
    List<SanPham> mList;
    boolean isLoading = false;
    private int mPage = 1;
    int user_id = 1;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable;
    GridLayoutManager gridLayoutManager;
    RecyclerViewFavoriteAdapter adapter;
    Handler mhandler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Chỉnh toàn màn hình false
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), true);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        compositeDisposable = new CompositeDisposable();
        mFragmentFavoriteBinding = FragmentFavoriteBinding.inflate(inflater, container, false);
        //=>
        user_id = Utils.user_current.getId();
        InitUI();
        initToolbar();
        initActionBar();
        getData(mPage);
        addEventLoading();

        return mFragmentFavoriteBinding.getRoot();
    }

    public void InitUI() {
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentFavoriteBinding.recyclerViewFavorite.setLayoutManager(gridLayoutManager);
        mFragmentFavoriteBinding.recyclerViewFavorite.setClipToPadding(false);
        mFragmentFavoriteBinding.recyclerViewFavorite.setHasFixedSize(true);
        mFragmentFavoriteBinding.progressBarFavorite.setVisibility(View.VISIBLE);
    }

    // Xử lý sự kiện loading sản phẩm
    private void addEventLoading() {
        mFragmentFavoriteBinding.recyclerViewFavorite.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isLoading = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int currentItem = gridLayoutManager.getChildCount();
                int totalItems = gridLayoutManager.getItemCount();
                int scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();
                if (!isLoading && (currentItem + scrollOutItems == totalItems)) {
                    if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == mList.size() - 1) {
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });
    }

    private void loadMore() {
        mhandler.post(new Runnable() {
            @Override
            public void run() {
                // add null
                mList.add(null);
                adapter.notifyItemInserted(mList.size() - 1);
            }
        });
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // remove null
                mList.remove(mList.size() - 1);
                adapter.notifyItemRemoved(mList.size());
                mPage += 1;
                getData(mPage);
                adapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 1000);
    }

    private void getData(int page) {
        compositeDisposable.add(apiBanHang.getSanPhamFavoriteOnPage(page, user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamModel -> {
                            if (sanPhamModel.isSuccess()) {
                                mFragmentFavoriteBinding.progressBarFavorite.setVisibility(View.GONE);
                                if (adapter == null) {
                                    mList = sanPhamModel.getResult();
                                    // Khởi tạo adapter
                                    adapter = new RecyclerViewFavoriteAdapter(mList);
                                    adapter.UpdateFavorite(this::UpdateFavorite);
                                    mFragmentFavoriteBinding.recyclerViewFavorite.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    int vitri = mList.size() - 1;
                                    int soluongadd = sanPhamModel.getResult().size();
                                    for (int i = 0; i < soluongadd; i++) {
                                        mList.add(sanPhamModel.getResult().get(i));
                                    }
                                    adapter.notifyItemRangeInserted(vitri, soluongadd);
                                }
                            } else {
                              /*  Toast.makeText(requireContext(),
                                        "Load lỗi user_id = " + user_id + " page=" + page,
                                        Toast.LENGTH_LONG).show();*/
                                isLoading = true;
                            }
                        }, throwable -> {
                            Toast.makeText(getActivity(), "Không kết nối được với server" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void UpdateFavorite(SanPham sanPham, String action) {
        compositeDisposable.add(apiBanHang.updateFavorite(sanPham.getId(), Utils.user_current.getId(), action)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()) {
                                YeuThich yeuThich = new YeuThich(sanPham.getId(), Utils.user_current.getId());
                                if (action == "remove") {
                                    adapter.DeleteItemFavorite(sanPham);
                                    LocalDatabase.getInstance(getActivity()).YeuThichDAO().deleteSanPhamYeuThich(yeuThich);
                                }
                                Toast.makeText(getActivity(), messageModel.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), messageModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }, throwable -> {
                            Toast.makeText(getActivity(), "Lỗi: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void initActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(mFragmentFavoriteBinding.toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Danh sách yêu thích");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), true);
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(false, false, getString(R.string.nav_favorite));
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
