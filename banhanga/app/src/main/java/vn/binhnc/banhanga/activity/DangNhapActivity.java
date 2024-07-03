package vn.binhnc.banhanga.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import vn.binhnc.banhanga.R;
import vn.binhnc.banhanga.retrofit.ApiBanHang;
import vn.binhnc.banhanga.retrofit.RetrofitClient;
import vn.binhnc.banhanga.utils.NetWorkChangeListener;
import vn.binhnc.banhanga.utils.Utils;

public class DangNhapActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    TextView txtdangky, txtresetpass;
    EditText email, pass;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    AppCompatButton btndangnhap;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        initView();
        initControl();
    }

    private void initControl() {
        txtdangky.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), DangKyActivity.class);
            startActivity(intent);
        });
        txtresetpass.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ResetPassActivity.class);
            startActivity(intent);
        });
        btndangnhap.setOnClickListener(v -> {
            String str_email = email.getText().toString().trim();
            String str_pass = pass.getText().toString().trim();
            //
            String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
            //
            if (TextUtils.isEmpty(str_email)) {
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(str_pass)) {
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập Pass", Toast.LENGTH_LONG).show();
            } else if (!str_email.matches(emailPattern)) {
                Toast.makeText(getApplicationContext(), "Sai định dạng email", Toast.LENGTH_LONG).show();
            } else {
                // save user
                Paper.book().write("email", str_email);
                Paper.book().write("pass", str_pass);

                // firebase check
                if (user != null) {
                    // user đã có đăng nhập firebase, chưa sigout
                    dangNhap(str_email, str_pass);
                    Log.d("login", "Login with host exists user");
                } else {
                    // user đã sigout
                    // Có 2 nơi quản lý mật khẩu - firebase và hosting
                    firebaseAuth.signInWithEmailAndPassword(str_email, str_pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        dangNhap(str_email, str_pass);
                                        Log.d("login", "Login with firebase");
                                    } else {
                                        // Đăng nhập không thành công
                                        Log.d("login", "Login failed");
                                        // Hiển thị thông báo lỗi
                                        Toast.makeText(getApplicationContext(), "Sai email hoặc mật khẩu.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                // dangNhap(str_email, str_pass);
            }
        });
    }

    private void initView() {
        Paper.init(this);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        txtdangky = findViewById(R.id.txtdangky_dn);
        txtresetpass = findViewById(R.id.txtresetpass);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        btndangnhap = findViewById(R.id.btndangnhap);
        //
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        // read data paper user
        if (Paper.book().read("email") != null && Paper.book().read("pass") != null) {
            email.setText(Paper.book().read("email"));
            pass.setText(Paper.book().read("pass"));
            if (Paper.book().read("isLogin") != null) {
                boolean flag = Paper.book().read("isLogin");
                if (flag) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dangNhap(Paper.book().read("email"), Paper.book().read("pass"));
                        }
                    }, 1000);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Utils.user_current.getEmail() != null && Utils.user_current.getPass() != null) {
            email.setText(Utils.user_current.getEmail());
            pass.setText(Utils.user_current.getPass());
        }
    }

    private void dangNhap(String email, String pass) {
        compositeDisposable.add(apiBanHang.dangnhap(email, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                isLogin = true;
                                Paper.book().write("isLogin", isLogin);
                                Utils.user_current = userModel.getResult().get(0);
                                // Lưu lại thông tin người dùng
                                Paper.book().write("user", userModel.getResult().get(0));
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }, throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
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