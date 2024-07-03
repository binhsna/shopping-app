package vn.binhnc.manager.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowCompat;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


import java.io.File;
import java.util.Objects;

import io.paperdb.Paper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.binhnc.manager.R;
import vn.binhnc.manager.activity.MainActivity;
import vn.binhnc.manager.model.MessageModel;
import vn.binhnc.manager.retrofit.ApiBanHang;
import vn.binhnc.manager.retrofit.RetrofitClient;
import vn.binhnc.manager.utils.Utils;

public class MyProfileFragment extends BaseFragment {
    private View mView;
    private ImageView imgAvatar;
    private ImageView imgEnableEdtUserName, imgEnableEdtEmail, imgEnableEdtMobile;
    private EditText edtUserName, edtEmail, edtPhone;
    private FloatingActionButton btnCollectImage;
    private AppCompatButton btnUpdateProfile;
    private Uri mUri;
    private String imageName;
    private MainActivity mMainActivity;
    private ProgressDialog mProgressDialog;
    private ApiBanHang apiBanHang;
    private final ActivityResultLauncher<Intent> startForMediaPickerResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                if (data != null && result.getResultCode() == Activity.RESULT_OK) {
                    Uri resultUri = data.getData();
                    mUri = resultUri;
                    // Đặt tên cho ảnh
                    imageName = "avatar_" + Utils.user_current.getId();
                    imgAvatar.setImageURI(resultUri);
                    if (!btnUpdateProfile.isEnabled()) {
                        btnUpdateProfile.setEnabled(true);
                    }
                } else {
                    Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_LONG).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Chỉnh toàn màn hình
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), false);
        mView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        initUI();
        Paper.init(requireActivity());
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        mMainActivity = (MainActivity) getActivity();
        mProgressDialog = new ProgressDialog(requireActivity());
        setUserInformation();
        initListener();
        return mView;
    }

    private void initUI() {
        imgAvatar = mView.findViewById(R.id.img_avatar);
        imgEnableEdtUserName = mView.findViewById(R.id.img_enable_edt_user_name);
        imgEnableEdtEmail = mView.findViewById(R.id.img_enable_edt_email);
        imgEnableEdtMobile = mView.findViewById(R.id.img_enable_edt_mobile);
        //=>
        edtUserName = mView.findViewById(R.id.edt_username);
        edtEmail = mView.findViewById(R.id.edt_email);
        edtPhone = mView.findViewById(R.id.edt_phone);
        btnCollectImage = mView.findViewById(R.id.btnCollectImage);
        btnUpdateProfile = mView.findViewById(R.id.btn_update_profile);

    }

    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        edtUserName.setText(user.getDisplayName());
        edtEmail.setText(user.getEmail());
        edtPhone.setText(Utils.user_current.getMobile());
        Glide.with(requireActivity()).load(user.getPhotoUrl()).error(R.drawable.ic_avatar_default).into(imgAvatar);
    }

    private void onClickSetImage() {
        String[] mimeTypes = {"image/png", "image/jpg", "image/jpeg"};
        ImagePicker.Companion.with(requireActivity())
                .saveDir(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
                .galleryOnly()
                .galleryMimeTypes(mimeTypes)
                .crop()
                .compress(1024)
                .maxResultSize(1000, 1000)
                .createIntent(intent -> {
                    startForMediaPickerResult.launch(intent);
                    return null;
                });
    }

    private void onClickUpdateEmail() {
        String strNewEmail = edtEmail.getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        if (!strNewEmail.equals(user.getEmail())) {
            user.updateEmail(strNewEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Paper.book().write("email", strNewEmail);
                                Utils.user_current.setEmail(strNewEmail);
                            } else {
                                // Show lên 1 dialog có 2 trường email và password
                                openReAuthenticateDialog(Gravity.CENTER);
                            }
                        }
                    });
        }
    }

    private void onClickUpdateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        mProgressDialog.setTitle("Thông báo");
        mProgressDialog.setMessage("Đợi...");
        mProgressDialog.show();
        //=>
        String strNewUserName = edtUserName.getText().toString().trim();
        String strNewEmail = edtEmail.getText().toString().trim();
        //=> Cập nhật lại email (Nếu có email mới)
        if (!strNewEmail.equals(user.getEmail())) {
            user.updateEmail(strNewEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Paper.book().write("email", strNewEmail);
                                Utils.user_current.setEmail(strNewEmail);
                            } else {
                                mProgressDialog.dismiss();
                                // Show lên 1 dialog có 2 trường email và password
                                openReAuthenticateDialog(Gravity.CENTER);
                            }
                        }
                    });
        }
        //=> Cập nhật thông tin user (Trừ email và password)
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(strNewUserName)
                .setPhotoUri(mUri)
                .build();
        //=>
        user.updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Lưu file lên Server
                            uploadData();
                            mMainActivity.showInformationUser();
                            Toast.makeText(requireActivity(), "Cập nhật thành công!", Toast.LENGTH_LONG).show();
                        } else {
                            // Show lên 1 dialog có 2 trường email và password
                            openReAuthenticateDialog(Gravity.CENTER);
                        }
                    }
                });
    }

    // Lấy đường dẫn chuẩn cho file ảnh
    private String getPath(Uri uri) {
        String result;
        Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            //=>
            cursor.close();
        }
        return result;
    }

    private void uploadData() {
        try {
            File file = new File(getPath(mUri));
            RequestBody requestBody1 = RequestBody.create(MediaType.parse("*/*"), file);
            MultipartBody.Part fileToUpload1 = MultipartBody.Part.createFormData("file", file.getName(), requestBody1);

            String str_mobile = edtPhone.getText().toString().trim();
            if (str_mobile.startsWith("0")) {
                str_mobile = "+84" + str_mobile.substring(1);
            }

            String strNewUserName = edtUserName.getText().toString().trim();
            String strNewEmail = edtEmail.getText().toString().trim();
            String strNewMobile = str_mobile;
            RequestBody idRequestBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Utils.user_current.getId()));
            RequestBody emailRequestBody = RequestBody.create(MediaType.parse("text/plain"), strNewEmail);
            RequestBody usernameRequestBody = RequestBody.create(MediaType.parse("text/plain"), strNewUserName);
            RequestBody fileNameRequestBody = RequestBody.create(MediaType.parse("text/plain"), imageName);
            RequestBody mobileRequestBody = RequestBody.create(MediaType.parse("text/plain"), strNewMobile);

            Call<MessageModel> call = apiBanHang.uploadFile(fileToUpload1, idRequestBody,
                    emailRequestBody, usernameRequestBody, fileNameRequestBody, mobileRequestBody);
            call.enqueue(new Callback<MessageModel>() {
                @Override
                public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                    MessageModel serverResponse = response.body();
                    if (serverResponse != null) {
                        if (serverResponse.isSuccess()) {
                            if (!Utils.user_current.getMobile().equals(strNewMobile)) {
                                Utils.user_current.setMobile(strNewMobile);
                            }
                            if (!Utils.user_current.getUsername().equals(strNewUserName)) {
                                Utils.user_current.setUsername(strNewUserName);
                            }
                            if (!Utils.user_current.getEmail().equals(strNewEmail)) {
                                Utils.user_current.setEmail(strNewEmail);
                                Paper.book().write("email", strNewEmail);
                            }
                            Paper.book().write("user", Utils.user_current);
                        } else {
                            Toast.makeText(requireActivity(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.v("Response", serverResponse.toString());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                    Log.d("log", Objects.requireNonNull(t.getMessage()));
                }
            });
        } catch (Exception e) {
            Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initListener() {
        btnCollectImage.setOnClickListener(v -> {
            onClickSetImage();
        });
        imgEnableEdtUserName.setOnClickListener(v -> {
            edtUserName.setEnabled(true);
            if (!btnUpdateProfile.isEnabled()) {
                btnUpdateProfile.setEnabled(true);
            }
        });
        imgEnableEdtEmail.setOnClickListener(v -> {
            edtEmail.setEnabled(true);
            if (!btnUpdateProfile.isEnabled()) {
                btnUpdateProfile.setEnabled(true);
            }
        });
        imgEnableEdtMobile.setOnClickListener(v -> {
            edtPhone.setEnabled(true);
            if (!btnUpdateProfile.isEnabled()) {
                btnUpdateProfile.setEnabled(true);
            }
        });
        btnUpdateProfile.setOnClickListener(v -> {
            String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
            String phonePattern = "^(0|\\+84)\\d{9}$";
            String str_username = edtUserName.getText().toString().trim();
            String str_email = edtEmail.getText().toString().trim();
            String str_mobile = edtPhone.getText().toString().trim();
            if (TextUtils.isEmpty(str_username)) {
                Toast.makeText(requireActivity(), "Username?", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(str_email)) {
                Toast.makeText(requireActivity(), "Email?", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(str_mobile)) {
                Toast.makeText(requireActivity(), "Số điện thoại?", Toast.LENGTH_LONG).show();
            } else if (!str_email.matches(emailPattern)) {
                Toast.makeText(requireActivity(), "Nhập đúng Email?", Toast.LENGTH_LONG).show();
            } else if (!str_mobile.matches(phonePattern)) {
                Toast.makeText(requireActivity(), "Nhập đúng số điện thoại?", Toast.LENGTH_LONG).show();
            } else {
                onClickUpdateProfile();
            }
        });
    }

    private void openReAuthenticateDialog(int gravity) {
        final Dialog dialog = new Dialog(requireActivity());
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
                ProgressDialog progressDialog = new ProgressDialog(requireActivity());
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

    // Trường hợp lâu không đăng nhập sẽ phải xác nhận lại tài khoản
    private void reAuthenticate(String email, String password, Dialog dialog) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        assert user != null;
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireActivity(),
                                    "Xác thực thành công!",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(requireActivity(),
                                    "Vui lòng nhập lại Email và mật khẩu!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(false, true, getString(R.string.nav_account));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), false);
    }
}
