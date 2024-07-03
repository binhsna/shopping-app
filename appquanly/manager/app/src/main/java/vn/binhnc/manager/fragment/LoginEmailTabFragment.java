package vn.binhnc.manager.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.internal.Util;
import vn.binhnc.manager.R;
import vn.binhnc.manager.activity.DangKyActivity;
import vn.binhnc.manager.activity.MainActivity;
import vn.binhnc.manager.activity.ResetPassActivity;
import vn.binhnc.manager.constant.GlobalFuntion;
import vn.binhnc.manager.retrofit.ApiBanHang;
import vn.binhnc.manager.retrofit.RetrofitClient;
import vn.binhnc.manager.utils.Utils;


public class LoginEmailTabFragment extends Fragment {
    TextView txtdangky, txtresetpass;
    EditText email, pass;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Button btndangnhap;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ImageView show_hide_pass;
    boolean isLogin = false;
    ProgressDialog mDialog;
    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_login_email_tab, container, false);
        //=>
        Paper.init(mView.getContext());
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        compositeDisposable = new CompositeDisposable();
        mDialog = new ProgressDialog(getActivity());
        // Inflate the layout for this fragment
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        checkAccount();
        initControl();
    }

    private void initControl() {
        txtdangky.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DangKyActivity.class);
            startActivity(intent);
        });
        txtresetpass.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ResetPassActivity.class);
            startActivity(intent);
        });
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra nếu EditText không rỗng
                if (!TextUtils.isEmpty(s)) {
                    // Hiển thị ImageView show_hide_pass
                    show_hide_pass.setVisibility(View.VISIBLE);
                } else {
                    // Ẩn ImageView show_hide_pass nếu EditText rỗng
                    show_hide_pass.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        show_hide_pass.setOnClickListener(view -> {
            // Kiểm tra type của EditText
            if (pass.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Hiển thị text nếu EditText là kiểu password
                pass.setInputType(InputType.TYPE_CLASS_TEXT);
                // Thay đổi src của show_hide_pass thành ic_hide_password
                show_hide_pass.setImageResource(R.drawable.ic_hide_password);
            } else {
                // Ẩn password nếu EditText không phải kiểu password
                pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                // Thay đổi src của show_hide_pass thành ic_show_password
                show_hide_pass.setImageResource(R.drawable.ic_show_password);
            }
            pass.setSelection(pass.length());
        });
        btndangnhap.setOnClickListener(v -> {
            String str_email = email.getText().toString().trim();
            String str_pass = pass.getText().toString().trim();
            //
            String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
            //
            if (TextUtils.isEmpty(str_email)) {
                Toast.makeText(getActivity(), "Bạn chưa nhập Email", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(str_pass)) {
                Toast.makeText(getActivity(), "Bạn chưa nhập Pass", Toast.LENGTH_LONG).show();
            } else if (!str_email.matches(emailPattern)) {
                Toast.makeText(getActivity(), "Sai định dạng email", Toast.LENGTH_LONG).show();
            } else {
                mDialog.setTitle("Thông báo");
                mDialog.setMessage("Đợi...");
                mDialog.show();
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
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    dangNhap(str_email, str_pass);
                                    Log.d("login", "Login with firebase");
                                } else {
                                    // Đăng nhập không thành công
                                    mDialog.dismiss();
                                    Log.d("login", "Login failed");
                                    // Hiển thị thông báo lỗi
                                    Toast.makeText(requireContext(),
                                            "Sai email hoặc mật khẩu!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                // dangNhap(str_email, str_pass);
            }
        });
    }

    private void checkAccount() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        // read data paper user
        if (Paper.book().read("email") != null && Paper.book().read("pass") != null) {
            email.setText(Paper.book().read("email"));
            pass.setText(Paper.book().read("pass"));
            String str_email = email.getText().toString().trim();
            String str_pass = pass.getText().toString().trim();
            if (!TextUtils.isEmpty(str_pass)) {
                // Hiển thị ImageView show_hide_pass
                show_hide_pass.setVisibility(View.VISIBLE);
            } else {
                // Ẩn ImageView show_hide_pass nếu EditText rỗng
                show_hide_pass.setVisibility(View.GONE);
            }
            email.requestFocus();
            email.setSelection(email.length());
            if (Paper.book().read("isLogin") != null) {
                boolean flag = Boolean.TRUE.equals(Paper.book().read("isLogin"));
                if (flag) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dangNhap(str_email, str_pass);
                        }
                    }, 1000);
                }
            }
        }
    }

    private void initView() {
        txtdangky = mView.findViewById(R.id.txtdangky_dn);
        txtresetpass = mView.findViewById(R.id.txtresetpass);
        email = mView.findViewById(R.id.email);
        pass = mView.findViewById(R.id.pass);
        btndangnhap = mView.findViewById(R.id.btndangnhap);
        show_hide_pass = mView.findViewById(R.id.show_hide_pass);
        //=>

    }

    @Override
    public void onResume() {
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
                            mDialog.dismiss();
                            if (userModel.isSuccess()) {
                                Utils.user_current = userModel.getResult().get(0);
                                if (Utils.user_current.getStatus() != 1) {
                                    isLogin = false;
                                    String title = "Thông báo";
                                    String message = "Bạn không phải là Admin" +
                                            "\nVui lòng đăng nhập lại!";
                                    GlobalFuntion.showAlertMessageDialog(
                                            requireContext(), title,
                                            message, 5000);
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
                                    Paper.book().write("user", Utils.user_current);
                                    Toast.makeText(requireContext(),
                                            "Đăng nhập thành công!"
                                            , Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(requireContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(getActivity(),
                                        userModel.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, throwable -> {
                            mDialog.dismiss();
                            Toast.makeText(getActivity(),
                                    throwable.getMessage(),
                                    Toast.LENGTH_LONG).show();
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