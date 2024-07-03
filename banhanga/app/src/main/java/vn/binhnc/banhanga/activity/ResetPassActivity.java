package vn.binhnc.banhanga.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import vn.binhnc.banhanga.R;
import vn.binhnc.banhanga.retrofit.ApiBanHang;
import vn.binhnc.banhanga.retrofit.RetrofitClient;
import vn.binhnc.banhanga.utils.NetWorkChangeListener;
import vn.binhnc.banhanga.utils.Utils;

public class ResetPassActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    EditText email;
    AppCompatButton btnreset;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ProgressBar progressBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        initView();
        initActionBar();
        initControl();

    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Quên mật khẩu");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void initControl() {
        email.requestFocus();
        String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        btnreset.setOnClickListener(v -> {
            String str_email = email.getText().toString().trim();
            if (TextUtils.isEmpty(str_email)) {
                Toast.makeText(getApplicationContext(), "Bạn chưa nhập Email", Toast.LENGTH_SHORT).show();
            } else if (!str_email.matches(emailPattern)) {
                Toast.makeText(getApplicationContext(), "Sai định dạng email", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                // Reset Pass trên firebase trước
                String str_check_email = "Kiểm tra email của bạn";
                FirebaseAuth.getInstance().sendPasswordResetEmail(str_email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), str_check_email, Toast.LENGTH_SHORT).show();
                            }
                            finish();
                        });
            }
        });
    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        email = findViewById(R.id.edtresetpass);
        btnreset = findViewById(R.id.btnresetpass);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressbar);

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