package vn.binhnc.banhanga.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.binhnc.banhanga.R;
import vn.binhnc.banhanga.activity.MainActivity;
import vn.binhnc.banhanga.adapter.OrderAdapter;
import vn.binhnc.banhanga.constant.GlobalFuntion;
import vn.binhnc.banhanga.databinding.FragmentOrderBinding;
import vn.binhnc.banhanga.model.EventBus.DeleteOrderEvent;
import vn.binhnc.banhanga.model.NotiSendData;
import vn.binhnc.banhanga.retrofit.ApiBanHang;
import vn.binhnc.banhanga.retrofit.ApiPushNotification;
import vn.binhnc.banhanga.retrofit.RetrofitClient;
import vn.binhnc.banhanga.retrofit.RetrofitClientNoti;
import vn.binhnc.banhanga.utils.Utils;

public class OrderFragment extends BaseFragment {

    private FragmentOrderBinding mFragmentOrderBinding;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ApiBanHang apiBanHang;
    private int iddonhang, index, status;
    private OrderAdapter adapter;

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
        compositeDisposable.add(apiBanHang.xemDonHang(Utils.user_current.getId())
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

    private void pushNotiToUser() {
        // Gettoken
        compositeDisposable.add(apiBanHang.gettoken(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            if (userModel.isSuccess()) {
                                for (int i = 0; i < userModel.getResult().size(); i++) {
                                    Map<String, String> data = new HashMap<>();
                                    data.put("title", "Thông báo");
                                    data.put("body", "Khách hàng đã hủy đơn có id: " + iddonhang);
                                    NotiSendData notiSendData = new NotiSendData(userModel.getResult().get(i).getToken(), data);
                                    ApiPushNotification apiPushNotification = RetrofitClientNoti.getInstance().create(ApiPushNotification.class);
                                    compositeDisposable.add(apiPushNotification.sendNotification(notiSendData)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    notiResponse -> {
                                                        Log.d("log_noti", "respose");
                                                    }
                                                    , throwable -> {
                                                        Log.d("log_noti", throwable.getMessage());
                                                    }
                                            ));
                                }
                            }
                        },
                        throwable -> {
                            Log.d("logg", throwable.getMessage());
                        }
                ));
    }

    private void deleteOder(int iddonhang) {
        compositeDisposable.add(apiBanHang.deleteOrder(iddonhang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        messageModel -> {
                            if (messageModel.isSuccess()) {
                                adapter.DeleteOrder(index);
                                String title = "Thông báo";
                                String message = "Hủy đơn hàng thành công!";
                                GlobalFuntion.showAlertMessageDialog(
                                        requireContext(), title, message, 1000);
                                pushNotiToUser();
                            }
                        }, throwable -> {
                            Log.d("logg", Objects.requireNonNull(throwable.getMessage()));
                            Toast.makeText(requireContext(),
                                    "Lỗi: " + throwable.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                ));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        // Thực hiện xóa đơn hàng theo iddonhang
        if (item.getItemId() == 0) {
            if (status == 3) {
                String title = "Thông báo";
                String message = "Đơn hàng không thể xóa (Đã giao)!";
                GlobalFuntion.showAlertMessageDialog(
                        requireContext(), title, message, 2000);
            } else {
                deleteOder(iddonhang);
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventDeleteOrder(DeleteOrderEvent event) {
        if (event != null) {
            iddonhang = event.getIddonhang();
            index = event.getIndex();
            status = event.getStatus();
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
