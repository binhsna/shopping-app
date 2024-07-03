package vn.binhnc.manager.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import vn.binhnc.manager.R;
import vn.binhnc.manager.utils.NetWorkChangeListener;

public class JoinActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private Toolbar toolbar;
    private Button btnCreate, btnJoinHost, btnJoinViewer;
    private EditText etMeetingId;
    // Replace with the token you generated from the VideoSDK Dashboard
    private final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiI3NGQwMDUwYy0xNTFiLTQxYTMtOWVjOS1jNjIwMTRkMzViNjEiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTY5NDc3MzQ5NCwiZXhwIjoxNzI2MzA5NDk0fQ.NqR2FmgNgaaLRltlQfuKk4aEImfgEc8h5a3vHjvue3w";
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        //
        checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID);
        checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID);
        //
        initView();
        initToolBar();
        // create meeting and join as Host
        btnCreate.setOnClickListener(v -> createMeeting(token));

        // Join as Host
        btnJoinHost.setOnClickListener(v -> {
            Intent intent = new Intent(JoinActivity.this, MeetingActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("meetingId", etMeetingId.getText().toString().trim());
            intent.putExtra("mode", "CONFERENCE");
            startActivity(intent);
        });

        // Join as Viewer
        btnJoinViewer.setOnClickListener(v -> {
            Intent intent = new Intent(JoinActivity.this, MeetingActivity.class);
            intent.putExtra("token", token);
            intent.putExtra("meetingId", etMeetingId.getText().toString().trim());
            intent.putExtra("mode", "VIEWER");
            startActivity(intent);
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_meeting);
        btnCreate = findViewById(R.id.btnCreateMeeting);
        btnJoinHost = findViewById(R.id.btnJoinHostMeeting);
        btnJoinViewer = findViewById(R.id.btnJoinViewerMeeting);
        etMeetingId = findViewById(R.id.etMeetingId);
        etMeetingId.requestFocus();
    }

    private void createMeeting(String token) {
        // we will make an API call to VideoSDK Server to get a roomId
        AndroidNetworking.post("https://api.videosdk.live/v2/rooms")
                .addHeaders("Authorization", token) //we will pass the token in the Headers
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // response will contain `roomId`
                            final String meetingId = response.getString("roomId");

                            // starting the MeetingActivity with received roomId and our sampleToken
                            Intent intent = new Intent(JoinActivity.this, MeetingActivity.class);
                            intent.putExtra("token", token);
                            intent.putExtra("meetingId", meetingId);
                            intent.putExtra("mode", "CONFERENCE");
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(JoinActivity.this, anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
        }
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Cuộc họp");
        }
        toolbar.setNavigationOnClickListener((View.OnClickListener) v -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
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
}