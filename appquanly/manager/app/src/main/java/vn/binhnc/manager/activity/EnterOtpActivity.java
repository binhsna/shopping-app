package vn.binhnc.manager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.binhnc.manager.R;
import vn.binhnc.manager.constant.GlobalFuntion;
import vn.binhnc.manager.retrofit.ApiBanHang;
import vn.binhnc.manager.retrofit.RetrofitClient;
import vn.binhnc.manager.utils.NetWorkChangeListener;
import vn.binhnc.manager.utils.Utils;


public class EnterOtpActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private EditText edtOtp;
    private Button btnVerifyOtp;
    private TextView tvSendOtpAgain;
    private ImageView img_comeback;
    private String mPhoneNumber;
    private String mVerificationId;
    private static final String TAG = EnterOtpActivity.class.getName();
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        //=>
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("vi");

        getDataIntent();
        setTitleToolbar();
        initUI();
        initControl();
    }

    private void setTitleToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Xác thực mã OTP");
        }
    }

    private void initUI() {
        edtOtp = findViewById(R.id.edt_otp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        tvSendOtpAgain = findViewById(R.id.tv_send_opt_again);
        img_comeback = findViewById(R.id.img_come_back);
    }

    private void initControl() {
        btnVerifyOtp.setOnClickListener(v -> {
            String strOtp = edtOtp.getText().toString().trim();
            onClickSendOptCode(strOtp);
        });
        tvSendOtpAgain.setOnClickListener(v -> {
            onClickSendOptCodeAgain();
        });
        img_comeback.setOnClickListener(v -> finish());
    }

    private void getDataIntent() {
        mPhoneNumber = getIntent().getStringExtra("phone_number");
        mVerificationId = getIntent().getStringExtra("verification_id");
    }

    private void onClickSendOptCode(String strOtp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, strOtp);
        signInWithPhoneAuthCredential(credential);
    }

    private void onClickSendOptCodeAgain() {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(mPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setForceResendingToken(mForceResendingToken)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // Save the verification id somewhere
                        super.onCodeSent(verificationId, forceResendingToken);
                        mVerificationId = verificationId;
                        mForceResendingToken = forceResendingToken;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // Sign in with the credential
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(EnterOtpActivity.this, "Xác minh lỗi!", Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithCredential:success");

                        FirebaseUser user = task.getResult().getUser();
                        // Update UI
                        dangNhap(mPhoneNumber);
                        /*
                        Toast.makeText(EnterOtpActivity.this, "Xác minh phone number thành công!", Toast.LENGTH_LONG).show();
                         */
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(EnterOtpActivity.this, "The verification code entered was invalid", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void dangNhap(String mobile) {
        compositeDisposable.add(apiBanHang.dangnhap_otp(mobile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                Utils.user_current = userModel.getResult().get(0);
                                if (Utils.user_current.getStatus() != 1) {
                                    isLogin = false;
                                    String title = "Thông báo";
                                    String message = "Bạn không phải là Admin" +
                                            "\nVui lòng đăng nhập lại!";
                                    GlobalFuntion.showAlertMessageDialog(this, title, message, 5000);
                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    if (currentUser != null) {
                                        // Xóa key user
                                        Paper.book().delete("user");
                                        Paper.book().write("isLogin", false);
                                        // SignOut firebase
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                }
                                // Lưu lại thông tin người dùng
                                else {
                                    isLogin = true;
                                    Paper.book().write("isLogin", isLogin);
                                    Paper.book().write("user", userModel.getResult().get(0));
                                    Toast.makeText(EnterOtpActivity.this,
                                            "Đăng nhập thành công!"
                                            , Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, MainActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(this, userModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }, throwable -> {
                            Toast.makeText(EnterOtpActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
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