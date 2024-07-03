package vn.binhnc.manager.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import vn.binhnc.manager.R;
import vn.binhnc.manager.adapter.ContactAdapter;
import vn.binhnc.manager.constant.GlobalFuntion;
import vn.binhnc.manager.databinding.ActivityContactBinding;
import vn.binhnc.manager.model.Contact;
import vn.binhnc.manager.utils.NetWorkChangeListener;

public class ContactActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private ContactAdapter mContactAdapter;
    private ActivityContactBinding mActivityContactBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Chỉnh toàn màn hình
        // WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        mActivityContactBinding = ActivityContactBinding.inflate(getLayoutInflater());
        setContentView(mActivityContactBinding.getRoot());
        initActionBar();
        initView();
    }

    private void initActionBar() {
        setSupportActionBar(mActivityContactBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Liên hệ");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initView() {
        mContactAdapter = new ContactAdapter(this, getListContact(), () -> {
            GlobalFuntion.callPhoneNumber(this);
        });
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mActivityContactBinding.rcvData.setNestedScrollingEnabled(false);
        mActivityContactBinding.rcvData.setFocusable(false);
        mActivityContactBinding.rcvData.setLayoutManager(layoutManager);
        mActivityContactBinding.rcvData.setAdapter(mContactAdapter);
    }

    public List<Contact> getListContact() {
        List<Contact> contactArrayList = new ArrayList<>();
        contactArrayList.add(new Contact(Contact.FACEBOOK, R.drawable.ic_facebook));
        contactArrayList.add(new Contact(Contact.HOTLINE, R.drawable.ic_hotline));
        contactArrayList.add(new Contact(Contact.GMAIL, R.drawable.ic_gmail));
        contactArrayList.add(new Contact(Contact.SKYPE, R.drawable.ic_skype));
        contactArrayList.add(new Contact(Contact.YOUTUBE, R.drawable.ic_youtube));
        contactArrayList.add(new Contact(Contact.ZALO, R.drawable.ic_zalo));

        return contactArrayList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContactAdapter.release();
    }
}