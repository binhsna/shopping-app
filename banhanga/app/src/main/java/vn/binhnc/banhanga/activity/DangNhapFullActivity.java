package vn.binhnc.banhanga.activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.google.android.material.tabs.TabLayout;

import vn.binhnc.banhanga.R;
import vn.binhnc.banhanga.adapter.ViewPagerLoginAdapter;
import vn.binhnc.banhanga.utils.NetWorkChangeListener;

public class DangNhapFullActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Chỉnh toàn màn hình
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap_full);

        tabLayout = findViewById(R.id.tab_layout_login);
        viewPager2 = findViewById(R.id.view_pager_login);

        tabLayout.addTab(tabLayout.newTab().setText("Email"));
        tabLayout.addTab(tabLayout.newTab().setText("Di động"));

        FragmentManager fragmentManager = getSupportFragmentManager();
        ViewPagerLoginAdapter adapter = new ViewPagerLoginAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
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