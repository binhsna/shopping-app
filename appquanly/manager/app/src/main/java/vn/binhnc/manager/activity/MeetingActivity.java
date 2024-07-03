package vn.binhnc.manager.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import live.videosdk.rtc.android.Meeting;
import live.videosdk.rtc.android.VideoSDK;
import live.videosdk.rtc.android.listeners.MeetingEventListener;
import vn.binhnc.manager.R;
import vn.binhnc.manager.fragment.SpeakerFragment;
import vn.binhnc.manager.fragment.ViewerFragment;
import vn.binhnc.manager.retrofit.ApiBanHang;
import vn.binhnc.manager.retrofit.RetrofitClient;
import vn.binhnc.manager.utils.NetWorkChangeListener;
import vn.binhnc.manager.utils.Utils;

public class MeetingActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private Meeting meeting;
    private CompositeDisposable compositeDisposable;
    private ApiBanHang apiBanHang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        //=>
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        compositeDisposable = new CompositeDisposable();
        //<=
        final String meetingId = getIntent().getStringExtra("meetingId");
        String token = getIntent().getStringExtra("token");
        String mode = getIntent().getStringExtra("mode");
        Toast.makeText(getApplicationContext(), meetingId, Toast.LENGTH_SHORT).show();
        String localParticipantName = "BinhNC";
        assert mode != null;
        boolean streamEnable = mode.equals("CONFERENCE");
        //=> Đẩy dữ liệu lên database
        postDataToMeeting(meetingId, token);
        // initialize VideoSDK
        VideoSDK.initialize(getApplicationContext());

        // Configuration VideoSDK with Token
        VideoSDK.config(token);

        // Initialize VideoSDK Meeting
        meeting = VideoSDK.initMeeting(
                MeetingActivity.this, meetingId, localParticipantName,
                streamEnable, streamEnable, null, mode, false, null);

        // join Meeting
        assert meeting != null;
        meeting.join();

        // if mode is CONFERENCE than replace mainLayout with SpeakerFragment otherwise with ViewerFragment
        meeting.addEventListener(new MeetingEventListener() {
            @Override
            public void onMeetingJoined() {
                if (meeting != null) {
                    if (mode.equals("CONFERENCE")) {
                        //pin the local partcipant
                        meeting.getLocalParticipant().pin("SHARE_AND_CAM");
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mainLayout, new SpeakerFragment(), "MainFragment")
                                .commit();
                    } else if (mode.equals("VIEWER")) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mainLayout, new ViewerFragment(), "viewerFragment")
                                .commit();
                    }
                }
            }
        });
    }

    private void postDataToMeeting(String meetingId, String token) {
        compositeDisposable.add(apiBanHang.postMeeting(meetingId, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()) {
                                Log.d("logg_meeting", "post meeting ok");
                            } else {
                                Log.d("logg_meeting", messageModel.getMessage());
                            }
                        }, throwable -> Log.d("logg_meeting",
                                Objects.requireNonNull(throwable.getMessage()))
                ));
    }

    public Meeting getMeeting() {
        return meeting;
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
        super.onDestroy();
        compositeDisposable.clear();
    }
}
