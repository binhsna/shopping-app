package vn.binhnc.manager.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import vn.binhnc.manager.R;
import vn.binhnc.manager.adapter.UserAdapter;
import vn.binhnc.manager.model.User;
import vn.binhnc.manager.utils.NetWorkChangeListener;

public class UserActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        initView();
        initToolBar();
        getUserFromFire();
    }

    private void getUserFromFire() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<User> userList = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            User user = new User();
                            user.setId(Objects.requireNonNull(documentSnapshot.getLong("id")).intValue());
                            user.setUsername(documentSnapshot.getString("username"));
                            userList.add(user);
                        }
                        if (userList.size() > 0) {
                            userAdapter = new UserAdapter(getApplicationContext(), userList);
                            recyclerView.setAdapter(userAdapter);
                        }
                    }
                });
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Khách hàng");
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_user);
        recyclerView = findViewById(R.id.recyclerview_user);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

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