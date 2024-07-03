package vn.binhnc.manager.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import vn.binhnc.manager.R;
import vn.binhnc.manager.adapter.ChatAdapter;
import vn.binhnc.manager.model.ChatMessage;
import vn.binhnc.manager.utils.NetWorkChangeListener;
import vn.binhnc.manager.utils.Utils;

public class ChatActivity extends AppCompatActivity {
    // Kiểm tra kết nối Internet
    NetWorkChangeListener netWorkChangeListener = new NetWorkChangeListener();
    private String iduser_str;
    private String username = "user";
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ImageView clearMess;
    private ImageView imgSend;
    private EditText edtMess;
    private FirebaseFirestore db;
    private ChatAdapter adapter;
    private List<ChatMessage> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //=>
        // id người nhận
        int iduser = getIntent().getIntExtra("id", 0);
        iduser_str = String.valueOf(iduser);
        username = getIntent().getStringExtra("username");
        //<=
        initView();
        initToolBar();
        initControl();
        listenMess();
    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle(username);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initControl() {
        clearMess.setOnClickListener(v -> edtMess.setText(""));
        edtMess.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    clearMess.setVisibility(View.GONE);
                } else {
                    clearMess.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        imgSend.setOnClickListener(view -> sendMessToFire());
    }

    private void sendMessToFire() {
        String str_mess = edtMess.getText().toString().trim();
        if (!TextUtils.isEmpty(str_mess)) {
            HashMap<String, Object> message = new HashMap<>();
            //=> (key, value)
            message.put(Utils.SENDID, String.valueOf(Utils.user_current.getId()));
            message.put(Utils.RECEIVEDID, iduser_str);
            message.put(Utils.MESS, str_mess);
            message.put(Utils.DATETIME, new Date());
            db.collection(Utils.PATH_CHAT).add(message);
            edtMess.setText("");
        }
    }

    private void listenMess() {
        db.collection(Utils.PATH_CHAT)
                .whereEqualTo(Utils.SENDID, String.valueOf(Utils.user_current.getId()))
                .whereEqualTo(Utils.RECEIVEDID, iduser_str)
                .addSnapshotListener(eventListener);

        db.collection(Utils.PATH_CHAT)
                .whereEqualTo(Utils.SENDID, iduser_str)
                .whereEqualTo(Utils.RECEIVEDID, String.valueOf(Utils.user_current.getId()))
                .addSnapshotListener(eventListener);
    }

    //=> Lấy data từ fireStore đổ vào recycler view
    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = list.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.sendid = documentChange.getDocument().getString(Utils.SENDID);
                    chatMessage.receivedid = documentChange.getDocument().getString(Utils.RECEIVEDID);
                    chatMessage.mess = documentChange.getDocument().getString(Utils.MESS);
                    chatMessage.dateObj = documentChange.getDocument().getDate(Utils.DATETIME);
                    chatMessage.datetime = format_date(documentChange.getDocument().getDate(Utils.DATETIME));
                    list.add(chatMessage);
                }
            }
            Collections.sort(list, (obj1, obj2) -> obj1.dateObj.compareTo(obj2.dateObj));
            if (count == 0) {
                adapter.notifyDataSetChanged();
            } else {
                adapter.notifyItemRangeInserted(list.size(), list.size());
                recyclerView.smoothScrollToPosition(list.size() - 1);
            }
        }
    });

    private String format_date(Date date) {
        return new SimpleDateFormat("MMMM dd yyyy-hh:mm a", Locale.getDefault()).format(date);
    }

    private void initView() {
        list = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.toolbar_chat);
        recyclerView = findViewById(R.id.recyclerview_chat);
        imgSend = findViewById(R.id.imagechat);
        clearMess = findViewById(R.id.img_clear_mess);
        edtMess = findViewById(R.id.edtinputtex);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        //=>
        adapter = new ChatAdapter(getApplicationContext(), list, String.valueOf(Utils.user_current.getId()));
        recyclerView.setAdapter(adapter);
        //=>
        edtMess.requestFocus();
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