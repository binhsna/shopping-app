package vn.binhnc.banhanga.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import vn.binhnc.banhanga.R;
import vn.binhnc.banhanga.activity.DangKyActivity;
import vn.binhnc.banhanga.activity.EnterOtpActivity;
import vn.binhnc.banhanga.activity.MainActivity;
import vn.binhnc.banhanga.activity.ResetPassActivity;
import vn.binhnc.banhanga.retrofit.ApiBanHang;
import vn.binhnc.banhanga.retrofit.RetrofitClient;
import vn.binhnc.banhanga.utils.Utils;

public class LoginPhoneTabFragment extends Fragment {
    private EditText edtPhoneNumber;
    private Button btnVerifyPhoneNumber;
    private TextView tvRegister;
    private TextView tvResetPass;
    private FirebaseAuth mAuth;
    private static final String TAG = LoginPhoneTabFragment.class.getName();
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean isLogin = false;
    ProgressDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_phone_tab, container, false);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("vi");
        mDialog = new ProgressDialog(view.getContext());

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        initControl();
    }

    private void initUI(View view) {
        edtPhoneNumber = view.findViewById(R.id.edt_phone_number);
        btnVerifyPhoneNumber = view.findViewById(R.id.btnVerify_phone_number);
        tvRegister = view.findViewById(R.id.tv_register);
        tvResetPass = view.findViewById(R.id.tv_reset_pass);
    }

    private void initControl() {
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DangKyActivity.class);
            startActivity(intent);
        });
        tvResetPass.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ResetPassActivity.class);
            startActivity(intent);
        });
        btnVerifyPhoneNumber.setOnClickListener(v -> {
            String phonePattern = "^(0|\\+84)\\d{9}$";
            String strPhoneNumber = edtPhoneNumber.getText().toString().trim();
            if (TextUtils.isEmpty(strPhoneNumber)) {
                Toast.makeText(requireContext(), "Hãy nhập số điện thoại!", Toast.LENGTH_LONG).show();
            } else if (!strPhoneNumber.matches(phonePattern)) {
                Toast.makeText(requireContext(), "Sai định dạng số điện thoại", Toast.LENGTH_LONG).show();
            } else {
                if (strPhoneNumber.startsWith("0")) {
                    strPhoneNumber = "+84" + strPhoneNumber.substring(1);
                }
                mDialog.setTitle("Thông báo");
                mDialog.setMessage("Đợi...");
                mDialog.show();
                onClickVerifyPhoneNumber(strPhoneNumber);
            }
        });
    }

    private void onClickVerifyPhoneNumber(String strPhoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(strPhoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(requireActivity())                 // (optional) Activity for
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        mDialog.dismiss();
                        signInWithPhoneAuthCredential(phoneAuthCredential, strPhoneNumber);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        mDialog.dismiss();
                        Toast.makeText(requireContext(), "Xác minh phone number >> Lỗi!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        mDialog.dismiss();
                        goToEnterOtpActivity(strPhoneNumber, verificationId);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, String mMobile) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e(TAG, "signInWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();
                        // Update UI
                        dangNhap(mMobile);
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            Toast.makeText(requireContext(), "The verification code entered was invalid", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void goToEnterOtpActivity(String strPhoneNumber, String verificationId) {
        Intent intent = new Intent(requireContext(), EnterOtpActivity.class);
        intent.putExtra("phone_number", strPhoneNumber);
        intent.putExtra("verification_id", verificationId);
        startActivity(intent);
    }

    private void dangNhap(String mobile) {
        compositeDisposable.add(apiBanHang.dangnhap_otp(mobile)
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
                                Intent intent = new Intent(requireContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(requireContext(), userModel.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }, throwable -> {
                            Toast.makeText(requireContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
        super.onDestroy();
    }
}