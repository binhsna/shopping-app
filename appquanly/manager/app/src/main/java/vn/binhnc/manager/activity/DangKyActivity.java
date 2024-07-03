package vn.binhnc.manager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.binhnc.manager.R;
import vn.binhnc.manager.retrofit.ApiBanHang;
import vn.binhnc.manager.retrofit.RetrofitClient;
import vn.binhnc.manager.utils.NetWorkChangeListener;
import vn.binhnc.manager.utils.Utils;

public class DangKyActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    EditText email, pass, repass, mobile, username;
    AppCompatButton button;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    FirebaseAuth firebaseAuth;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ky);
        initView();
        initActionBar();
        initControl();
    }

    private void initControl() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangky();
            }
        });
    }

    private void dangky() {
        String str_email = email.getText().toString().trim();
        String str_pass = pass.getText().toString().trim();
        String str_repass = repass.getText().toString().trim();
        String str_mobile = mobile.getText().toString().trim();
        String str_user = username.getText().toString().trim();
        //
        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        // "^0\\d{9}$";
        String phonePattern = "^(0|\\+84)\\d{9}$";
        //
        if (TextUtils.isEmpty(str_user)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Username", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(str_email)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(str_pass)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Pass", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(str_repass)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Repass", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(str_mobile)) {
            Toast.makeText(getApplicationContext(), "Bạn chưa nhập Mobile", Toast.LENGTH_LONG).show();
        } else if (!str_email.matches(emailPattern)) {
            Toast.makeText(getApplicationContext(), "Sai định dạng email", Toast.LENGTH_LONG).show();
        } else if (!str_mobile.matches(phonePattern)) {
            Toast.makeText(getApplicationContext(), "Sai định dạng Mobile", Toast.LENGTH_LONG).show();
        } else {
            if (str_pass.equals(str_repass)) {
                if (str_mobile.startsWith("0")) {
                    str_mobile = "+84" + str_mobile.substring(1);
                }
                // post data
                firebaseAuth = FirebaseAuth.getInstance();
                String finalStr_mobile = str_mobile;
                firebaseAuth.createUserWithEmailAndPassword(str_email, str_pass)
                        .addOnCompleteListener(DangKyActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    Log.d("user_firebase", String.valueOf(user));
                                    if (user != null) {
                                        postData(str_email, str_pass, str_user, finalStr_mobile, user.getUid());
                                    }
                                } else {
                                    Log.e("create_user_error", Objects.requireNonNull(task.getException().getMessage()));
                                    Toast.makeText(getApplicationContext(), "Email đã tồn tại hoặc không thành công", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Pass chưa khớp", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void postData(String str_email, String str_pass, String str_user, String str_mobile, String uid) {
        compositeDisposable.add(apiBanHang.dangky(str_email, "onfirebase", str_user, str_mobile, uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                Utils.user_current.setEmail(str_email);
                                Utils.user_current.setPass(str_pass);
                                Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }, throwable -> {
                            Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        //=>
        toolbar = findViewById(R.id.toolbar);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        repass = findViewById(R.id.repass);
        mobile = findViewById(R.id.mobile);
        username = findViewById(R.id.username);
        button = findViewById(R.id.btndangky);

    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Đăng ký tài khoản");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
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