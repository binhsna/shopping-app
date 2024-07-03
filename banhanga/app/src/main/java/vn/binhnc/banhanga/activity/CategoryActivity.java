package vn.binhnc.banhanga.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import vn.binhnc.banhanga.adapter.ViewPagerCategoryAdapter;
import vn.binhnc.banhanga.constant.Constant;
import vn.binhnc.banhanga.databinding.ActivityCategoryBinding;
import vn.binhnc.banhanga.model.TheLoai;
import vn.binhnc.banhanga.utils.NetWorkChangeListener;

public class CategoryActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    ActivityCategoryBinding mActivityCategoryBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCategoryBinding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(mActivityCategoryBinding.getRoot());

        initActionBar();
        initIntent();

    }

    private void initIntent() {
        Intent intent = getIntent();
        List<TheLoai> categories = (List<TheLoai>) intent.getSerializableExtra(Constant.EXTRA_CATEGORY);
        int position = intent.getIntExtra(Constant.EXTRA_POSITION, 0);

        ViewPagerCategoryAdapter adapter = new ViewPagerCategoryAdapter(this, categories);
        mActivityCategoryBinding.viewPager.setAdapter(adapter);
        //tabLayout.setupWithViewPager(viewPager);
        mActivityCategoryBinding.viewPager.setCurrentItem(position, true);
        //=>
        new TabLayoutMediator(mActivityCategoryBinding.tabLayout, mActivityCategoryBinding.viewPager,
                (tab, i) -> {
                    assert categories != null;
                    String title = categories.get(i).getTentheloai();
                    tab.setText(title);

                }).attach();
        //==>
        mActivityCategoryBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                assert categories != null;
                String title = categories.get(tab.getPosition()).getTentheloai();
                SpannableString spannableString = new SpannableString(title);
                spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableString.length(), 0);
                tab.setText(spannableString);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                assert categories != null;
                String title = categories.get(tab.getPosition()).getTentheloai();
                SpannableString spannableString = new SpannableString(title);
                spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), 0, spannableString.length(), 0);
                tab.setText(spannableString);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Xử lý khi tab được chọn lại (nếu cần)
            }
        });
        //<=
        adapter.notifyDataSetChanged();
    }

    private void initActionBar() {
        setSupportActionBar(mActivityCategoryBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Thể loại");
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
