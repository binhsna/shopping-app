package vn.binhnc.banhanga.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import io.paperdb.Paper;
import vn.binhnc.banhanga.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Paper.init(this);
        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                } finally {
                    if (Paper.book().read("user") == null) {
                        Intent intent = new Intent(getApplicationContext(), DangNhapFullActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent home = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(home);
                        finish();
                    }
                }
            }
        };
        thread.start();
    }
}