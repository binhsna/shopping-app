package vn.binhnc.manager.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.binhnc.manager.adapter.RecyclerViewMealByCategory;
import vn.binhnc.manager.database.LocalDatabase;
import vn.binhnc.manager.databinding.FragmentCategoryBinding;
import vn.binhnc.manager.model.SanPham;
import vn.binhnc.manager.model.YeuThich;
import vn.binhnc.manager.retrofit.ApiBanHang;
import vn.binhnc.manager.retrofit.RetrofitClient;
import vn.binhnc.manager.utils.GlideUtils;
import vn.binhnc.manager.utils.Utils;

public class CategoryFragment extends Fragment {
    private FragmentCategoryBinding mFragmentCategoryBinding;

    AlertDialog.Builder descDialog;
    List<SanPham> mList;
    boolean isLoading = false;
    int mPage = 1;
    int loai = 1;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable;
    GridLayoutManager gridLayoutManager;
    RecyclerViewMealByCategory adapter;
    Handler mhandler = new Handler();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentCategoryBinding = FragmentCategoryBinding.inflate(inflater, container, false);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        compositeDisposable = new CompositeDisposable();

        return mFragmentCategoryBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            mFragmentCategoryBinding.textCategory.setText(getArguments().getString("EXTRA_DATA_DESC"));
            GlideUtils.loadUrl(getArguments().getString("EXTRA_DATA_IMAGE"), mFragmentCategoryBinding.imageCategory);
            GlideUtils.loadUrl(getArguments().getString("EXTRA_DATA_IMAGE"), mFragmentCategoryBinding.imageCategoryBg);
            descDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(getArguments().getString("EXTRA_DATA_NAME"))
                    .setMessage(getArguments().getString("EXTRA_DATA_DESC"));
            loai = getArguments().getInt("EXTRA_DATA_ID_CATEGORY");
            try {
                InitUI();
                getData(mPage);
                addEventLoading();
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            mFragmentCategoryBinding.cardCategory.setOnClickListener(v -> {
                descDialog.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());
                descDialog.show();
            });
        }
    }

    public void InitUI() {
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentCategoryBinding.recyclerView.setLayoutManager(gridLayoutManager);
        mFragmentCategoryBinding.recyclerView.setClipToPadding(false);
        mFragmentCategoryBinding.recyclerView.setHasFixedSize(true);
        mFragmentCategoryBinding.progressBar.setVisibility(View.VISIBLE);
    }

    // Xử lý sự kiện loading sản phẩm
    private void addEventLoading() {
        mFragmentCategoryBinding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        compositeDisposable.add(apiBanHang.getSanPhamOnPage(page, loai)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamModel -> {
                            if (sanPhamModel.isSuccess()) {
                                mFragmentCategoryBinding.progressBar.setVisibility(View.GONE);
                                if (adapter == null) {
                                    mList = sanPhamModel.getResult();
                                    // Khởi tạo adapter
                                    adapter = new RecyclerViewMealByCategory(mList);
                                    adapter.UpdateFavorite(this::UpdateFavorite);
                                    mFragmentCategoryBinding.recyclerView.setAdapter(adapter);
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
                                /*    Toast.makeText(getActivity(), "Hết dữ liệu", Toast.LENGTH_LONG).show();*/
                                isLoading = true;
                            }
                        }, throwable -> {
                            Toast.makeText(getActivity(), "Không kết nối được với server" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void UpdateFavorite(int sp_id, String action) {
        compositeDisposable.add(apiBanHang.updateFavorite(sp_id, Utils.user_current.getId(), action)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()) {
                                YeuThich yeuThich = new YeuThich(sp_id, Utils.user_current.getId());
                                if (action == "add") {
                                    LocalDatabase.getInstance(getActivity()).YeuThichDAO().insertYeuThich(yeuThich);
                                } else if (action == "remove") {
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

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
