package vn.binhnc.banhanga.activity;

import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import vn.binhnc.banhanga.R;
import vn.binhnc.banhanga.utils.NetWorkChangeListener;

public class YouTubeActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private Toolbar toolbar;
    private YouTubePlayerView playerView;
    private String video_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube);
        video_id = getIntent().getStringExtra("linkvideo");
        initView();
        initToolBar();
        initListener();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_youtube);
        playerView = findViewById(R.id.youtube);
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Hiển thị nút quay lại
            actionBar.setDisplayHomeAsUpEnabled(true);
            // Set icon cho nút quay lại
            Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24);
            if (upArrow != null) {
                DrawableCompat.setTint(upArrow, Color.RED);
                actionBar.setHomeAsUpIndicator(upArrow);
            }
            actionBar.setTitle(R.string.youtube);
        }
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    private void initListener() {
        playerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(video_id, 0);
            }
        });
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