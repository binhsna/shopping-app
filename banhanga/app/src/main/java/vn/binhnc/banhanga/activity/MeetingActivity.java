package vn.binhnc.banhanga.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import live.videosdk.rtc.android.Meeting;
import live.videosdk.rtc.android.VideoSDK;
import live.videosdk.rtc.android.listeners.MeetingEventListener;
import vn.binhnc.banhanga.R;
import vn.binhnc.banhanga.fragment.ViewerFragment;
import vn.binhnc.banhanga.retrofit.ApiBanHang;
import vn.binhnc.banhanga.retrofit.RetrofitClient;
import vn.binhnc.banhanga.utils.NetWorkChangeListener;
import vn.binhnc.banhanga.utils.Utils;

public class MeetingActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private CompositeDisposable compositeDisposable;
    private ApiBanHang apiBanHang;
    private Meeting meeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);
        //=>
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        compositeDisposable = new CompositeDisposable();
        //<=
        getMeetingIdFromServer();

    }

    private void getMeetingIdFromServer() {
        compositeDisposable.add(apiBanHang.getMeeting()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        meetingModel -> {
                            if (meetingModel.isSuccess()) {
                                String meetingId = meetingModel.getResult().get(0).getMeetingId();
                                String token = meetingModel.getResult().get(0).getToken();
                                String mode = "VIEWER";
                                boolean streamEnable = false;
                                // Khởi tạo VideoSDK
                                VideoSDK.initialize(getApplicationContext());

                                // Configuration VideoSDK with Token
                                VideoSDK.config(token);

                                // Initialize VideoSDK Meeting
                                meeting = VideoSDK.initMeeting(
                                        MeetingActivity.this, meetingId, "App bán hàng",
                                        streamEnable, streamEnable, null, mode, false, null);

                                // join Meeting
                                assert meeting != null;
                                meeting.join();
                                //=>
                                meeting.addEventListener(new MeetingEventListener() {
                                    @Override
                                    public void onMeetingJoined() {
                                        if (meeting != null) {
                                            getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .replace(R.id.mainLayout, new ViewerFragment(), "viewerFragment")
                                                    .commit();
                                        }
                                    }
                                });
                            }
                        }, throwable -> Log.d("log_meeting", Objects.requireNonNull(throwable.getMessage()))
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