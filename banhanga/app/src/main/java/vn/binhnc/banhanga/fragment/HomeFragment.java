package vn.binhnc.banhanga.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;


import java.io.Serializable;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.binhnc.banhanga.R;
import vn.binhnc.banhanga.activity.CategoryActivity;
import vn.binhnc.banhanga.activity.ChitietSanPhamActivity;
import vn.binhnc.banhanga.activity.MainActivity;
import vn.binhnc.banhanga.adapter.SanPhamAdapter;
import vn.binhnc.banhanga.adapter.BannerAdapter;
import vn.binhnc.banhanga.adapter.TheLoaiAdapter;
import vn.binhnc.banhanga.constant.Constant;
import vn.binhnc.banhanga.constant.GlobalFuntion;
import vn.binhnc.banhanga.databinding.FragmentHomeBinding;
import vn.binhnc.banhanga.model.SanPham;
import vn.binhnc.banhanga.model.TheLoai;
import vn.binhnc.banhanga.retrofit.ApiBanHang;
import vn.binhnc.banhanga.retrofit.RetrofitClient;
import vn.binhnc.banhanga.utils.Utils;

public class HomeFragment extends BaseFragment {
    private MainActivity mMainActivity;
    FragmentHomeBinding mFragmentHomeBinding;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable;
    //
    int mPageSanPhamMoi = 1;
    boolean isLoading = false;
    Handler mHandlerLoadPage = new Handler();
    GridLayoutManager gridLayoutManager;
    Boolean iCompleteData = false;
    //
    private List<TheLoai> mListTheLoai;
    private List<SanPham> mListSanPham;
    private List<SanPham> mListBanner;
    private BannerAdapter mBannerAdapter;
    private SanPhamAdapter mSanPhamAdapter;
    private TheLoaiAdapter mTheLoaiAdapter;
    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (mListBanner == null || mListBanner.isEmpty()) {
                return;
            }
            if (mFragmentHomeBinding.viewpager2.getCurrentItem() == mListBanner.size() - 1) {
                mFragmentHomeBinding.viewpager2.setCurrentItem(0);
                return;
            }
            mFragmentHomeBinding.viewpager2.setCurrentItem(mFragmentHomeBinding.viewpager2.getCurrentItem() + 1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Chỉnh toàn màn hình
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), false);
        mMainActivity = (MainActivity) getActivity();
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        compositeDisposable = new CompositeDisposable();
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        initUI();
        initBanner();
        initTheLoai();
        getSanPhamMoi(mPageSanPhamMoi);
        addEventLoading();

        return mFragmentHomeBinding.getRoot();
    }

    private void displayBanner() {
        mBannerAdapter = new BannerAdapter(mListBanner, this::goToSanPhamDetail);
        mFragmentHomeBinding.viewpager2.setAdapter(mBannerAdapter);
        mFragmentHomeBinding.indicator3.setViewPager(mFragmentHomeBinding.viewpager2);

        mFragmentHomeBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }

    private void initBanner() {
        // Lấy về Banner
        compositeDisposable.add(apiBanHang.getBanner()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamModel -> {
                            if (sanPhamModel.isSuccess()) {
                                mListBanner = sanPhamModel.getResult();
                                displayBanner();
                            }
                        }, throwable -> {
                            Toast.makeText(requireContext(),
                                            "Không kết nối được với server" + throwable.getMessage(),
                                            Toast.LENGTH_LONG).
                                    show();

                        }
                ));
    }

    private void initTheLoai() {
        // Lấy về thể loại sản phẩm
        compositeDisposable.add(apiBanHang.getTheLoai()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        theLoaiModel -> {
                            if (theLoaiModel.isSuccess()) {
                                mListTheLoai = theLoaiModel.getResult();
                                //Log.d("check", new Gson().toJson(mListTheLoai));
                                mTheLoaiAdapter = new TheLoaiAdapter(mListTheLoai, this::goToSanPhamOnTheLoai);
                                mFragmentHomeBinding.rcvTheloai.setAdapter(mTheLoaiAdapter);
                            }
                        }, throwable -> {
                            Toast.makeText(requireContext(),
                                    "Không kết nối được với server" + throwable.getMessage(),
                                    Toast.LENGTH_LONG).show();

                        }
                ));
    }

    // Xử lý sự kiện loading sản phẩm
    private void addEventLoading() {
        mFragmentHomeBinding.rcvSanpham.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == mListSanPham.size() - 1) {
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });
    }

    private void loadMore() {
        mHandlerLoadPage.post(new Runnable() {
            @Override
            public void run() {
                // add null
                mListSanPham.add(null);
                mSanPhamAdapter.notifyItemInserted(mListSanPham.size() - 1);
            }
        });
        mHandlerLoadPage.postDelayed(new Runnable() {
            @Override
            public void run() {
                // remove null
                mListSanPham.remove(mListSanPham.size() - 1);
                mSanPhamAdapter.notifyItemRemoved(mListSanPham.size());
                mPageSanPhamMoi += 1;
                getSanPhamMoi(mPageSanPhamMoi);
                mSanPhamAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 1000);
    }

    private void getSanPhamMoi(int page) {
        // Lấy về sản phầm
        compositeDisposable.add(apiBanHang.getSanPhamMoi(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamModel -> {
                            if (sanPhamModel.isSuccess()) {
                                if (mSanPhamAdapter == null) {
                                    mListSanPham = sanPhamModel.getResult();
                                    mSanPhamAdapter = new SanPhamAdapter(mListSanPham, this::goToSanPhamDetail);
                                    mFragmentHomeBinding.rcvSanpham.setAdapter(mSanPhamAdapter);
                                } else {
                                    int vitri = mListSanPham.size() - 1;
                                    int soluongadd = sanPhamModel.getResult().size();
                                    for (int i = 0; i < soluongadd; i++) {
                                        mListSanPham.add(sanPhamModel.getResult().get(i));
                                    }
                                    mSanPhamAdapter.notifyItemRangeInserted(vitri, soluongadd);
                                }
                            } else {
                                /*if (!isLoading) {
                                    Toast.makeText(requireContext(), "Hết dữ liệu", Toast.LENGTH_SHORT).show();
                                }*/
                                iCompleteData = true;
                                isLoading = true;
                            }
                        }, throwable -> {
                            Toast.makeText(requireContext(),
                                    "Không kết nối được với  server" + throwable.getMessage(),
                                    Toast.LENGTH_LONG).show();

                        }
                ));
    }

    private void initUI() {
        // Set Layout cho Thể loại
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mFragmentHomeBinding.rcvTheloai.setLayoutManager(layoutManager);
        // Set Layout cho Sản phẩm mới
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentHomeBinding.rcvSanpham.setLayoutManager(gridLayoutManager);
        mFragmentHomeBinding.rcvSanpham.setHasFixedSize(true);

    }


    private void goToSanPhamDetail(@NonNull SanPham sanPham) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_SANPHAM_OBJECT, sanPham);
        GlobalFuntion.startActivity(getActivity(), ChitietSanPhamActivity.class, bundle);
    }

    private void goToSanPhamOnTheLoai(@NonNull List<TheLoai> list, int position) {
        try {
            Intent intent = new Intent(getActivity(), CategoryActivity.class);
            intent.putExtra(Constant.EXTRA_CATEGORY, (Serializable) list);
            intent.putExtra(Constant.EXTRA_POSITION, position);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mMainActivity, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(true, true, getString(R.string.nav_home));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandlerBanner.removeCallbacks(mRunnableBanner);
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), false);
        mHandlerBanner.postDelayed(mRunnableBanner, 3000);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
