package vn.binhnc.manager.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.binhnc.manager.R;
import vn.binhnc.manager.activity.MainActivity;
import vn.binhnc.manager.adapter.OrderAdapter;
import vn.binhnc.manager.constant.GlobalFuntion;
import vn.binhnc.manager.databinding.FragmentOrderBinding;
import vn.binhnc.manager.model.DonHang;
import vn.binhnc.manager.model.EventBus.DonHangEvent;
import vn.binhnc.manager.model.NotiSendData;
import vn.binhnc.manager.retrofit.ApiBanHang;
import vn.binhnc.manager.retrofit.ApiPushNotification;
import vn.binhnc.manager.retrofit.RetrofitClient;
import vn.binhnc.manager.retrofit.RetrofitClientNoti;
import vn.binhnc.manager.utils.Utils;

public class OrderFragment extends BaseFragment {

    private FragmentOrderBinding mFragmentOrderBinding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiBanHang apiBanHang;
    private DonHang donHang;
    private int status;
    private OrderAdapter adapter;
    private AlertDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), true);
        mFragmentOrderBinding = FragmentOrderBinding.inflate(inflater, container, false);
        //=>
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        compositeDisposable = new CompositeDisposable();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        //=>
        initUI();
        initActionBar();
        getOrder();
        return mFragmentOrderBinding.getRoot();
    }

    private void getOrder() {
        compositeDisposable.add(apiBanHang.xemDonHang(0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        donHangModel -> {
                            // Toast.makeText(getApplicationContext(), donHangModel.getResult().get(0).getItem().get(0).getTensp(), Toast.LENGTH_SHORT).show();
                            try {
                                mFragmentOrderBinding.progressBarOrder.setVisibility(View.GONE);
                                adapter = new OrderAdapter(donHangModel.getResult());
                                mFragmentOrderBinding.rcvOrderList.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Toast.makeText(requireActivity(),
                                        "Lỗi: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }, throwable -> Toast.makeText(requireActivity(),
                                "Lỗi: " + throwable.getMessage(),
                                Toast.LENGTH_LONG).show()
                ));
    }

    private void showCustomDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_donhang, null);
        Spinner spinner = view.findViewById(R.id.spinner_dialog);
        AppCompatButton btndongy = view.findViewById(R.id.dongy_dialog);
        // Đổ dữ liệu vào cho spinner
        List<String> list = new ArrayList<>();
        list.add("Đang xử lý");
        list.add("Chấp nhận");
        list.add("Vận chuyển");
        list.add("Đã giao");
        list.add("Hủy");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(),
                android.R.layout.simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
        spinner.setSelection(donHang.getTrangthai());
        // Bắt sự kiện spinner thay đổi giá trị
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                status = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btndongy.setOnClickListener(view1 -> capNhatDonHang());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }

    private void capNhatDonHang() {
        compositeDisposable.add(apiBanHang.updateOrder(donHang.getId(), status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            getOrder();
                            pushNotiToUser();
                            dialog.dismiss();
                        },
                        throwable -> {

                        }
                ));
    }

    // Gửi thông báo đến user khi admin cập nhật trạng thái đơn hàng
    private void pushNotiToUser() {
        // Gettoken
        Log.d("log_noti", String.valueOf(donHang.getIduser()));
        compositeDisposable.add(apiBanHang.gettoken(0, donHang.getIduser())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                for (int i = 0; i < userModel.getResult().size(); i++) {
                                    Map<String, String> data = new HashMap<>();
                                    data.put("title", "Thông báo");
                                    data.put("body", Utils.statusOrder(status));
                                    NotiSendData notiSendData = new NotiSendData(userModel.getResult().get(i).getToken(), data);
                                    ApiPushNotification apiPushNotification = RetrofitClientNoti.getInstance().create(ApiPushNotification.class);
                                    compositeDisposable.add(apiPushNotification.sendNotification(notiSendData)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    notiResponse -> Log.d("log_noti", "respose")
                                                    , throwable -> Log.d("log_noti", Objects.requireNonNull(throwable.getMessage()))
                                            ));
                                }
                            }
                        },
                        throwable -> Log.d("logg", Objects.requireNonNull(throwable.getMessage()))
                ));
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventDonHang(DonHangEvent event) {
        if (event != null) {
            donHang = event.getDonHang();
            if (donHang.getTrangthai() != 3) {
                showCustomDialog();
            } else {
                String title = "Thông báo";
                String message = "Đơn hàng đã giao\nKhông thao tác được!";
                GlobalFuntion.showAlertMessageDialog(requireContext(), title, message, 5000);
            }
        }
    }

    private void initUI() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        mFragmentOrderBinding.rcvOrderList.setLayoutManager(layoutManager);
        mFragmentOrderBinding.progressBarOrder.setVisibility(View.VISIBLE);
    }

    private void initActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        assert activity != null;
        activity.setSupportActionBar(mFragmentOrderBinding.toolbarOrder);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Danh sách đơn hàng");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), true);
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(false, false, getString(R.string.order_list));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
