package vn.binhnc.manager.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.binhnc.manager.R;
import vn.binhnc.manager.adapter.MoreImageAdapter;
import vn.binhnc.manager.constant.Constant;
import vn.binhnc.manager.constant.GlobalFuntion;
import vn.binhnc.manager.database.LocalDatabase;
import vn.binhnc.manager.databinding.ActivityChitietSanPhamBinding;
import vn.binhnc.manager.event.ReloadListCartEvent;
import vn.binhnc.manager.model.GioHang;
import vn.binhnc.manager.model.HinhAnh;
import vn.binhnc.manager.model.SanPham;
import vn.binhnc.manager.model.YeuThich;
import vn.binhnc.manager.retrofit.ApiBanHang;
import vn.binhnc.manager.retrofit.RetrofitClient;
import vn.binhnc.manager.utils.GlideUtils;
import vn.binhnc.manager.utils.NetWorkChangeListener;
import vn.binhnc.manager.utils.Utils;

public class ChitietSanPhamActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private static final int MAX_TITLE_LENGTH = 10;
    private ActivityChitietSanPhamBinding mActivityFoodDetailBinding;
    private SanPham sanPham;
    private GioHang mGioHang;
    private List<HinhAnh> mListHinhAnhSanPham = new ArrayList<>();
    private ApiBanHang apiBanHang;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Chỉnh toàn màn hình
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        mActivityFoodDetailBinding = ActivityChitietSanPhamBinding.inflate(getLayoutInflater());
        setContentView(mActivityFoodDetailBinding.getRoot());
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        compositeDisposable = new CompositeDisposable();
        //=>
        mGioHang = new GioHang();
        //=>
        getDataIntent();
        setupActionBar();
        setDataFoodDetail();
        initListener();
    }

    private void setupActionBar() {
        setSupportActionBar(mActivityFoodDetailBinding.toolbar);
        mActivityFoodDetailBinding.collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.white));
        mActivityFoodDetailBinding.collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));
        mActivityFoodDetailBinding.collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.white));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    void setupColorActionBarIcon(MenuItem favoriteItem) {
        Drawable favoriteItemColor = favoriteItem.getIcon();
        mActivityFoodDetailBinding.appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if ((mActivityFoodDetailBinding.collapsingToolbar.getHeight() + verticalOffset) < (2 * ViewCompat.getMinimumHeight(mActivityFoodDetailBinding.collapsingToolbar))) {
                System.out.println("collapsing height min = " + ViewCompat.getMinimumHeight(mActivityFoodDetailBinding.collapsingToolbar));
                System.out.println("vertical offset = " + verticalOffset);
                System.out.println("collapsing height = " + mActivityFoodDetailBinding.collapsingToolbar.getHeight());
                if (sanPham.getGiamgia() > 0) {
                    mActivityFoodDetailBinding.tvSaleOff.setVisibility(View.GONE);
                }
                if (mActivityFoodDetailBinding.toolbar.getNavigationIcon() != null) {
                    mActivityFoodDetailBinding.toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.red_crimson), PorterDuff.Mode.SRC_ATOP);
                }
                favoriteItemColor.mutate().setColorFilter(ContextCompat.getColor(this, R.color.red_crimson), PorterDuff.Mode.SRC_ATOP);
            } else {
                if (sanPham.getGiamgia() > 0) {
                    mActivityFoodDetailBinding.tvSaleOff.setVisibility(View.VISIBLE);
                }
                if (mActivityFoodDetailBinding.toolbar.getNavigationIcon() != null) {
                    mActivityFoodDetailBinding.toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.red_crimson), PorterDuff.Mode.SRC_ATOP);
                }
                favoriteItemColor.mutate().setColorFilter(getResources().getColor(R.color.red_crimson), PorterDuff.Mode.SRC_ATOP);
                System.out.println("collapsing height min = " + ViewCompat.getMinimumHeight(mActivityFoodDetailBinding.collapsingToolbar));
                System.out.println("vertical offset = " + verticalOffset);
                System.out.println("collapsing height = " + mActivityFoodDetailBinding.collapsingToolbar.getHeight());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        MenuItem favoriteItem = menu.findItem(R.id.favorite);
        YeuThich check_favorite = LocalDatabase.getInstance(this).YeuThichDAO().checkSanPhamYeuThich(sanPham.getId(), Utils.user_current.getId());
        if (check_favorite != null) {
            favoriteItem.setChecked(true);
        } else {
            favoriteItem.setChecked(false);
        }
        if (favoriteItem.isChecked()) {
            favoriteItem.setIcon(R.drawable.ic_baseline_red_favorite);
        } else {
            favoriteItem.setIcon(R.drawable.ic_favorite_border);
        }
        setupColorActionBarIcon(favoriteItem);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.favorite:
                if (item.isChecked()) {
                    item.setIcon(R.drawable.ic_favorite_border);
                    UpdateFavorite(sanPham.getId(), "remove");
                } else {
                    item.setIcon(R.drawable.ic_baseline_red_favorite);
                    UpdateFavorite(sanPham.getId(), "add");
                }
                setupColorActionBarIcon(item);
                // Đảo ngược trạng thái checked của menu item
                item.setChecked(!item.isChecked());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                                    LocalDatabase.getInstance(this).YeuThichDAO().insertYeuThich(yeuThich);
                                } else if (action == "remove") {
                                    LocalDatabase.getInstance(this).YeuThichDAO().deleteSanPhamYeuThich(yeuThich);
                                }
                                Toast.makeText(this, messageModel.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, messageModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }, throwable -> {
                            Toast.makeText(this, "Lỗi: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sanPham = (SanPham) bundle.get(Constant.KEY_INTENT_SANPHAM_OBJECT);
        }
    }

    private void setDataFoodDetail() {
        if (sanPham == null) {
            return;
        }
        GlideUtils.loadUrlBanner(sanPham.getHinhanh(), mActivityFoodDetailBinding.mealThumb);
        String title = sanPham.getTensp();
        if (title.length() > MAX_TITLE_LENGTH) {
            title = title.substring(0, MAX_TITLE_LENGTH) + "...";
        }
        mActivityFoodDetailBinding.collapsingToolbar.setTitle(title);
        //
        setupActionBar();
        //=>
        if (sanPham.getGiamgia() <= 0) {
            mActivityFoodDetailBinding.tvSaleOff.setVisibility(View.GONE);
            mActivityFoodDetailBinding.tvPrice.setVisibility(View.GONE);

            String strPrice = sanPham.getGiasp() + Constant.CURRENCY;
            mActivityFoodDetailBinding.tvPriceSale.setText(strPrice);
        } else {
            mActivityFoodDetailBinding.tvSaleOff.setVisibility(View.VISIBLE);
            mActivityFoodDetailBinding.tvPrice.setVisibility(View.VISIBLE);

            String strSale = "Giảm " + sanPham.getGiamgia() + "%";
            mActivityFoodDetailBinding.tvSaleOff.setText(strSale);

            String strPriceOld = sanPham.getGiasp() + Constant.CURRENCY;
            mActivityFoodDetailBinding.tvPrice.setText(strPriceOld);
            mActivityFoodDetailBinding.tvPrice.setPaintFlags(mActivityFoodDetailBinding.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String strRealPrice = sanPham.getGiaThat() + Constant.CURRENCY;
            mActivityFoodDetailBinding.tvPriceSale.setText(strRealPrice);
        }
        mActivityFoodDetailBinding.tvFoodDescription.setText(Html.fromHtml(sanPham.getMota()));

        displayListMoreImages();

        setStatusButtonAddToCart();
    }

    private void displayListMoreImages() {
        mActivityFoodDetailBinding.tvMoreImageLabel.setVisibility(View.VISIBLE);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mActivityFoodDetailBinding.rcvImages.setLayoutManager(gridLayoutManager);
        compositeDisposable.add(apiBanHang.getHinhAnhSanPham(sanPham.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(hinhAnhSanPhamModel -> {
                            if (hinhAnhSanPhamModel.isSuccess()) {
                                mListHinhAnhSanPham = hinhAnhSanPhamModel.getResult();
                                MoreImageAdapter moreImageAdapter = new MoreImageAdapter(mListHinhAnhSanPham);
                                mActivityFoodDetailBinding.rcvImages.setAdapter(moreImageAdapter);
                            }
                        }, throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                ));
        if (sanPham == null || mListHinhAnhSanPham.isEmpty()) {
            mActivityFoodDetailBinding.tvMoreImageLabel.setVisibility(View.GONE);
            return;
        }
    }

    private void setStatusButtonAddToCart() {
        if (isFoodInCart()) {
            mActivityFoodDetailBinding.tvAddToCart.setBackgroundResource(R.drawable.bg_gray_shape_corner_6);
            mActivityFoodDetailBinding.tvAddToCart.setText(getString(R.string.added_to_cart));
            mActivityFoodDetailBinding.tvAddToCart.setTextColor(ContextCompat.getColor(this, R.color.textColorPrimary));
        } else {
            mActivityFoodDetailBinding.tvAddToCart.setBackgroundResource(R.drawable.bg_green_shape_corner_6);
            mActivityFoodDetailBinding.tvAddToCart.setText(getString(R.string.add_to_cart));
            mActivityFoodDetailBinding.tvAddToCart.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }

    private boolean isFoodInCart() {
        List<GioHang> mCartList = LocalDatabase.getInstance(this).GioHangDAO().checkItemInCart(sanPham.getId());
        return mCartList != null && !mCartList.isEmpty();
    }

    private void initListener() {
        mActivityFoodDetailBinding.tvAddToCart.setOnClickListener(v -> onClickAddToCart());
        mActivityFoodDetailBinding.youtube.setOnClickListener(v -> {
            if (sanPham != null) {
                Intent youtube = new Intent(getApplicationContext(), YouTubeActivity.class);
                youtube.putExtra("linkvideo", sanPham.getLinkvideo());
                startActivity(youtube);
            }
        });
        mActivityFoodDetailBinding.source.setOnClickListener(v -> {
            if (!sanPham.getLinksource().isEmpty()) {
                Intent intentSource = new Intent(Intent.ACTION_VIEW);
                intentSource.setData(Uri.parse(sanPham.getLinksource()));
                startActivity(intentSource);
            } else {
                String title = "Thông báo";
                String message = "Vui lòng thử lại sau!";
                GlobalFuntion.showAlertMessageDialog(this, title, message, 3000);
            }
        });
    }

    public void onClickAddToCart() {
        if (isFoodInCart()) {
            return;
        }

        View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_cart, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(viewDialog);

        ImageView imgFoodCart = viewDialog.findViewById(R.id.img_food_cart);
        TextView tvFoodNameCart = viewDialog.findViewById(R.id.tv_food_name_cart);
        TextView tvFoodPriceCart = viewDialog.findViewById(R.id.tv_food_price_cart);
        TextView tvSubtractCount = viewDialog.findViewById(R.id.tv_subtract);
        TextView tvCount = viewDialog.findViewById(R.id.tv_count);
        TextView tvAddCount = viewDialog.findViewById(R.id.tv_add);
        TextView tvCancel = viewDialog.findViewById(R.id.tv_cancel);
        TextView tvAddCart = viewDialog.findViewById(R.id.tv_add_cart);

        GlideUtils.loadUrl(sanPham.getHinhanh(), imgFoodCart);
        tvFoodNameCart.setText(sanPham.getTensp());

        int totalPrice = sanPham.getGiaThat();
        String strTotalPrice = totalPrice + Constant.CURRENCY;
        tvFoodPriceCart.setText(strTotalPrice);

        sanPham.setUser_id(Utils.user_current.getId());
        sanPham.setCount(1);
        sanPham.setTotalPrice(totalPrice);
        //=>
        mGioHang.setIdsp(sanPham.getId());
        mGioHang.setTensp(sanPham.getTensp());
        mGioHang.setHinhsp(sanPham.getHinhanh());
        mGioHang.setSoluong(1);
        mGioHang.setSltonkho(sanPham.getSltonkho());
        mGioHang.setGiamgia(sanPham.getGiamgia());
        mGioHang.setGiasp(Integer.parseInt(sanPham.getGiasp()));
        mGioHang.setTotalPrice(totalPrice);

        //<=

        tvSubtractCount.setOnClickListener(v -> {
            int count = Integer.parseInt(tvCount.getText().toString());
            if (count <= 1) {
                return;
            }
            int newCount = Integer.parseInt(tvCount.getText().toString()) - 1;
            tvCount.setText(String.valueOf(newCount));
            int totalPrice1 = sanPham.getGiaThat() * newCount;
            String strTotalPrice1 = totalPrice1 + Constant.CURRENCY;
            tvFoodPriceCart.setText(strTotalPrice1);

            sanPham.setCount(newCount);
            sanPham.setTotalPrice(totalPrice1);
            //=>
            mGioHang.setSoluong(newCount);
            mGioHang.setTotalPrice(totalPrice1);
        });

        tvAddCount.setOnClickListener(v -> {
            int newCount = Integer.parseInt(tvCount.getText().toString()) + 1;
            if (newCount <= sanPham.getSltonkho()) {
                tvCount.setText(String.valueOf(newCount));
                int totalPrice2 = sanPham.getGiaThat() * newCount;
                String strTotalPrice2 = totalPrice2 + Constant.CURRENCY;
                tvFoodPriceCart.setText(strTotalPrice2);

                sanPham.setCount(newCount);
                sanPham.setTotalPrice(totalPrice2);
                //=>
                mGioHang.setSoluong(newCount);
                mGioHang.setTotalPrice(totalPrice2);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Thông báo")
                        .setMessage("Hết hàng.");

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                // Đóng thông báo sau 2 giây
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                    }
                }, 2000);
                // Đặt sự kiện khi người dùng nhấn bất kỳ nơi nào bên ngoài để đóng thông báo
                alertDialog.setCanceledOnTouchOutside(true);
            }
        });

        tvCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        tvAddCart.setOnClickListener(v -> {
            /*LocalDatabase.getInstance(ChitietSanPhamActivity.this).SanPhamDAO().insertSanPham(sanPham);*/
            mGioHang.setAddToCart(true);
            mGioHang.setChecked(false);
            LocalDatabase.getInstance(ChitietSanPhamActivity.this).GioHangDAO().insertItem(mGioHang);
            bottomSheetDialog.dismiss();
            setStatusButtonAddToCart();
            EventBus.getDefault().post(new ReloadListCartEvent());
            updateCountCart();
        });

        bottomSheetDialog.show();
    }

    void updateCountCart() {
        Utils.COUNT_CART = LocalDatabase.getInstance(this).GioHangDAO().getCountCart();
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