package vn.binhnc.banhanga.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowCompat;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.Objects;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.binhnc.banhanga.R;
import vn.binhnc.banhanga.adapter.MyViewPager2Adapter;
import vn.binhnc.banhanga.database.LocalDatabase;
import vn.binhnc.banhanga.databinding.ActivityMainBinding;
import vn.binhnc.banhanga.model.User;
import vn.binhnc.banhanga.model.YeuThich;
import vn.binhnc.banhanga.retrofit.ApiBanHang;
import vn.binhnc.banhanga.retrofit.RetrofitClient;
import vn.binhnc.banhanga.utils.NetWorkChangeListener;
import vn.binhnc.banhanga.utils.Utils;
import vn.zalopay.sdk.ZaloPaySDK;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private static final int FRAGMENT_HOME = 0;
    private static final int FRAGMENT_CART = 1;
    private static final int FRAGMENT_FAVORITE = 2;
    private static final int FRAGMENT_ORDER = 3;
    private static final int FRAGMENT_MY_PROFILE = 4;
    private int mCurrentFragment = FRAGMENT_HOME;
    private BadgeDrawable badgeDrawable;
    private ActivityMainBinding mActivityMainBinding;
    //=>
    // Đếm sản phẩm trong giỏ hàng
    private int mCountProduct = 0;
    //<=
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    //=>
    TextView tv_email_user, tv_username;
    ImageView img_avatar_user;
    private MenuItem nav_content_main, nav_home, nav_favorite, nav_order;
    private MenuItem nav_content_account, nav_my_profile;
    private MenuItem bottom_home, bottom_cart, bottom_favorite, bottom_order, bottom_user;

    //=>
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Chỉnh toàn màn hình
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mActivityMainBinding.getRoot());
        // mActivityMainBinding.toolbar.imgSearch.setVisibility(View.VISIBLE);
        badgeDrawable = mActivityMainBinding.bottomNavigation.getOrCreateBadge(R.id.bottom_cart);
        //=>
        Utils.COUNT_CART = LocalDatabase.getInstance(this).GioHangDAO().getCountCart();
        if (Utils.COUNT_CART > 0) {
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(Utils.COUNT_CART);
        } else {
            badgeDrawable.setVisible(false);
        }
        //=>
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        Paper.init(this);
        if (Paper.book().read("user") != null) {
            User user = Paper.book().read("user");
            Utils.user_current = user;
        }
        View headerView = mActivityMainBinding.navigationView.getHeaderView(0);
        tv_username = headerView.findViewById(R.id.tv_username);
        tv_email_user = headerView.findViewById(R.id.tv_email_user);
        img_avatar_user = headerView.findViewById(R.id.img_avatar_user);
        showInformationUser();
        // Lấy về danh sách sản phẩm yêu thích nếu chưa có (Do cài lại app hay...)
        getFavoriteList(Utils.user_current.getId());
        // Get về token
        getToken();
        //<=
        ActionBar();
        //=> Ngăn chặn thao tác vuốt của viewPager
        mActivityMainBinding.viewPager2.setUserInputEnabled(false);
        MyViewPager2Adapter mMyViewPager2Adapter = new MyViewPager2Adapter(this);
        mActivityMainBinding.viewPager2.setAdapter(mMyViewPager2Adapter);

        mActivityMainBinding.navigationView.setNavigationItemSelectedListener(this);
        mActivityMainBinding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.bottom_home) {
                openHomeFragment();
            } else if (id == R.id.bottom_cart) {
                openCartFragment();
            } else if (id == R.id.bottom_favorite) {
                openFavoriteFragment();
            } else if (id == R.id.bottom_order) {
                openOrderFragment();
            } else if (id == R.id.bottom_user) {
                openMyProfileFragment();
            }
            return true;
        });
        // Ánh xạ navigation drawer
        nav_content_main = mActivityMainBinding.navigationView.getMenu().findItem(R.id.nav_content_main);
        nav_home = mActivityMainBinding.navigationView.getMenu().findItem(R.id.nav_home);
        nav_favorite = mActivityMainBinding.navigationView.getMenu().findItem(R.id.nav_favorite);
        nav_order = mActivityMainBinding.navigationView.getMenu().findItem(R.id.nav_order);
        //=>
        nav_content_account = mActivityMainBinding.navigationView.getMenu().findItem(R.id.nav_content_account);
        nav_my_profile = mActivityMainBinding.navigationView.getMenu().findItem(R.id.nav_my_profile);
        // Ánh xạ bottom navigation
        bottom_home = mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.bottom_home);
        bottom_cart = mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.bottom_cart);
        bottom_favorite = mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.bottom_favorite);
        bottom_order = mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.bottom_order);
        bottom_user = mActivityMainBinding.bottomNavigation.getMenu().findItem(R.id.bottom_user);
        mActivityMainBinding.viewPager2.registerOnPageChangeCallback(new OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mCurrentFragment = FRAGMENT_HOME;
                        nav_content_main.setChecked(true);
                        updateCheckedColorMenuItem(nav_home);
                        //=>
                        bottom_home.setChecked(true);
                        break;
                    case 1:
                        mCurrentFragment = FRAGMENT_CART;
                        bottom_cart.setChecked(true);
                        break;
                    case 2:
                        mCurrentFragment = FRAGMENT_FAVORITE;
                        nav_content_main.setChecked(true);
                        updateCheckedColorMenuItem(nav_favorite);
                        //=>
                        bottom_favorite.setChecked(true);
                        break;
                    case 3:
                        mCurrentFragment = FRAGMENT_ORDER;
                        nav_content_main.setChecked(true);
                        updateCheckedColorMenuItem(nav_order);
                        //=>
                        bottom_order.setChecked(true);
                        break;
                    case 4:
                        mCurrentFragment = FRAGMENT_MY_PROFILE;
                        nav_content_account.setChecked(true);
                        updateCheckedColorMenuItem(nav_my_profile);
                        //=>
                        bottom_user.setChecked(true);
                        break;

                }
            }
        });
        //
    }

    private void updateCheckedColorMenuItem(MenuItem item) {
        nav_home.setChecked(false);
        nav_favorite.setChecked(false);
        nav_order.setChecked(false);
        nav_my_profile.setChecked(false);
        //=>
        item.setChecked(true);
    }

    public void showInformationUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();

        if (name == null) {
            tv_username.setVisibility(View.GONE);
        } else {
            tv_username.setVisibility(View.VISIBLE);
            tv_username.setText(name);
        }
        tv_email_user.setText(email);
        Glide.with(this).load(photoUrl).error(R.drawable.ic_avatar_default).into(img_avatar_user);
    }

    private void getFavoriteList(int user_id) {
        compositeDisposable.add(apiBanHang.favoriteList(user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        yeuThichModel -> {
                            if (yeuThichModel.isSuccess()) {
                                List<YeuThich> favoriteList = yeuThichModel.getResult();
                                for (YeuThich favorite : favoriteList) {
                                    YeuThich temp = LocalDatabase.getInstance(this).YeuThichDAO().checkSanPhamYeuThich(favorite.getSp_id(), user_id);
                                    if (temp == null) {
                                        LocalDatabase.getInstance(this).YeuThichDAO().insertYeuThich(favorite);
                                    }
                                }
                            }
                        }, throwable -> {
                            Log.d("error", Objects.requireNonNull(throwable.getMessage()));
                        }
                ));
    }

    // Get về token
    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnSuccessListener(s -> {
                    if (!TextUtils.isEmpty(s)) {
                        compositeDisposable.add(apiBanHang.updateToken(Utils.user_current.getId(), s)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        messageModel -> {

                                        }, throwable -> {
                                            Log.d("error", Objects.requireNonNull(throwable.getMessage()));
                                        }
                                ));
                    }
                });
        // Lấy ra id của người nhận (admin)
        compositeDisposable.add(apiBanHang.gettoken(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                Utils.ID_RECEIVED = String.valueOf(userModel.getResult().get(0).getId());
                            }
                        }, throwable -> {
                            Log.d("error", Objects.requireNonNull(throwable.getMessage()));
                        }
                ));
    }

    private void ActionBar() {
        mActivityMainBinding.toolbar.imgOpenDrawer.setOnClickListener(v -> mActivityMainBinding.drawerLayout.openDrawer(GravityCompat.START));
        mActivityMainBinding.toolbar.imgSearch.setOnClickListener(v -> {
            Intent intentSearch = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intentSearch);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            if (mCurrentFragment != FRAGMENT_HOME) {
                openHomeFragment();
            }
        } else if (id == R.id.nav_favorite) {
            if (mCurrentFragment != FRAGMENT_FAVORITE) {
                openFavoriteFragment();
            }
        } else if (id == R.id.nav_order) {
            if (mCurrentFragment != FRAGMENT_ORDER) {
                openOrderFragment();
            }
        } else if (id == R.id.nav_contact) {
            Intent contact_intent = new Intent(this, ContactActivity.class);
            startActivity(contact_intent);
        } else if (id == R.id.nav_message) {
            Intent chat = new Intent(this, ChatActivity.class);
            startActivity(chat);
        } else if (id == R.id.nav_meeting) {
            Intent meeting = new Intent(getApplicationContext(), MeetingActivity.class);
            startActivity(meeting);
        } else if (id == R.id.nav_my_profile) {
            if (mCurrentFragment != FRAGMENT_MY_PROFILE) {
                openMyProfileFragment();
            }
        } else if (id == R.id.nav_change_password) {
            openChangePasswordDialog(Gravity.CENTER);
        } else if (id == R.id.nav_logout) {
            // Xóa key user
            Paper.book().delete("user");
            Paper.book().write("isLogin", false);
            // SignOut firebase
            FirebaseAuth.getInstance().signOut();
            Intent dangnhap = new Intent(getApplicationContext(), DangNhapFullActivity.class);
            startActivity(dangnhap);
            finish();
        }
        // -> Đóng draw layout lại
        mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openChangePasswordDialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_change_password);
        //=>
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        //=>
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //=>
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        // Ngăn chặn tắt dialog khi click ra ngoài
        if (Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        EditText edtChangePassword = dialog.findViewById(R.id.edt_change_password);
        TextView tvError = dialog.findViewById(R.id.tv_error_change_password);
        AppCompatButton btnCloseDialog = dialog.findViewById(R.id.btn_close_dialog);
        AppCompatButton btnChangePassword = dialog.findViewById(R.id.btn_change_password);
        edtChangePassword.requestFocus();

        btnCloseDialog.setOnClickListener(v -> dialog.dismiss());
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Cập nhật...");
                String strNewPassword = edtChangePassword.getText().toString().trim();
                if (strNewPassword.isEmpty()) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Mật khẩu?");
                    edtChangePassword.requestFocus();
                } else if (strNewPassword.length() < 6) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Mật khẩu phải từ 6 ký tự trở lên.");
                    edtChangePassword.requestFocus();
                    edtChangePassword.setSelection(edtChangePassword.length());
                } else {
                    tvError.setVisibility(View.GONE);
                    progressDialog.show();
                    onClickChangePassword(strNewPassword, dialog);
                    progressDialog.dismiss();
                }
            }
        });
        // Hiển thị dialog
        dialog.show();
    }

    private void onClickChangePassword(String strNewPassword, Dialog dialog) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.updatePassword(strNewPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Utils.user_current.setPass(strNewPassword);
                            Paper.book().write("pass", strNewPassword);
                            Paper.book().write("user", Utils.user_current);
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            openReAuthenticateDialog(Gravity.CENTER);
                        }
                    }
                });
    }

    private void openReAuthenticateDialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_re_authenticate);
        //=>
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        //=>
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //=>
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        // Ngăn chặn tắt dialog khi click ra ngoài
        if (Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        EditText edtEmail = dialog.findViewById(R.id.edt_email_auth);
        EditText edtPassword = dialog.findViewById(R.id.edt_password_auth);
        TextView tvError = dialog.findViewById(R.id.tv_error_re_auth);
        AppCompatButton btnCloseDialog = dialog.findViewById(R.id.btn_close_dialog);
        AppCompatButton btnAgreeAuth = dialog.findViewById(R.id.btn_agree_auth);
        edtEmail.requestFocus();
        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        btnCloseDialog.setOnClickListener(v -> dialog.dismiss());
        btnAgreeAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Xác thực...");
                String strEmail = edtEmail.getText().toString().trim();
                String strPassword = edtPassword.getText().toString().trim();
                if (strEmail.isEmpty()) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Email?");
                    edtEmail.requestFocus();
                } else if (strPassword.isEmpty()) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Mật khẩu?");
                    edtPassword.requestFocus();
                } else if (!strEmail.matches(emailPattern)) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Sai định dạng Email.");
                    edtEmail.requestFocus();
                    edtEmail.selectAll();
                } else if (strPassword.length() < 6) {
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText("Mật khẩu phải từ 6 ký tự trở lên.");
                    edtPassword.requestFocus();
                    edtPassword.selectAll();
                } else {
                    tvError.setVisibility(View.GONE);
                    progressDialog.show();
                    reAuthenticate(strEmail, strPassword, dialog);
                    progressDialog.dismiss();
                }
            }
        });
        // Hiển thị dialog
        dialog.show();
    }

    private void reAuthenticate(String email, String password, Dialog dialog) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        assert user != null;
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            openChangePasswordDialog(Gravity.CENTER);
                            Toast.makeText(MainActivity.this,
                                    "Xác thực thành công!",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Vui lòng nhập lại Email và mật khẩu!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void openHomeFragment() {
        mActivityMainBinding.viewPager2.setCurrentItem(0);
        mCurrentFragment = FRAGMENT_HOME;
    }

    private void openCartFragment() {
        mActivityMainBinding.viewPager2.setCurrentItem(1);
        mCurrentFragment = FRAGMENT_FAVORITE;
    }

    private void openFavoriteFragment() {
        mActivityMainBinding.viewPager2.setCurrentItem(2);
        mCurrentFragment = FRAGMENT_FAVORITE;
    }

    private void openOrderFragment() {
        mActivityMainBinding.viewPager2.setCurrentItem(3);
        mCurrentFragment = FRAGMENT_ORDER;
    }

    private void openMyProfileFragment() {
        mActivityMainBinding.viewPager2.setCurrentItem(4);
        mCurrentFragment = FRAGMENT_MY_PROFILE;
    }

    @Override
    public void onBackPressed() {
        if (mActivityMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            showConfirmExitApp();
        }
    }

    private void showConfirmExitApp() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive((dialog, which) -> finish())
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show();
    }

    public BadgeDrawable getBadgeDrawable() {
        return badgeDrawable;
    }

    public void updateCountCart(int count) {
        mCountProduct = count;
        badgeDrawable.setNumber(mCountProduct);
    }

    public int getmCountProduct() {
        return mCountProduct;
    }

    public BottomNavigationView getmBottomNavigationView() {
        return mActivityMainBinding.bottomNavigation;
    }

    public void setToolBar(boolean isHome, boolean isToolBar, String title) {
        if (isToolBar) {
            if (isHome) {
                mActivityMainBinding.toolbar.layoutToolbar.setVisibility(View.VISIBLE);
                mActivityMainBinding.toolbar.imgSearch.setVisibility(View.VISIBLE);
                mActivityMainBinding.toolbar.tvTitle.setVisibility(View.GONE);
            } else {
                mActivityMainBinding.toolbar.layoutToolbar.setVisibility(View.VISIBLE);
                mActivityMainBinding.toolbar.tvTitle.setVisibility(View.VISIBLE);
                mActivityMainBinding.toolbar.tvTitle.setText(title);
            }
        } else {
            mActivityMainBinding.toolbar.layoutToolbar.setVisibility(View.GONE);
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
    protected void onResume() {
        super.onResume();
        Utils.COUNT_CART = LocalDatabase.getInstance(this).GioHangDAO().getCountCart();
        if (Utils.COUNT_CART > 0) {
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(Utils.COUNT_CART);
        } else {
            badgeDrawable.setVisible(false);
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}