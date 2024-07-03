package vn.binhnc.banhanga.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import vn.binhnc.banhanga.R;
import vn.binhnc.banhanga.activity.MainActivity;
import vn.binhnc.banhanga.adapter.CartAdapter;
import vn.binhnc.banhanga.constant.Constant;
import vn.binhnc.banhanga.constant.GlobalFuntion;
import vn.binhnc.banhanga.database.LocalDatabase;
import vn.binhnc.banhanga.databinding.FragmentCartBinding;
import vn.binhnc.banhanga.event.ReloadListCartEvent;
import vn.binhnc.banhanga.model.CreateOrder;
import vn.binhnc.banhanga.model.GioHang;
import vn.binhnc.banhanga.model.NotiSendData;
import vn.binhnc.banhanga.retrofit.ApiBanHang;
import vn.binhnc.banhanga.retrofit.ApiPushNotification;
import vn.binhnc.banhanga.retrofit.RetrofitClient;
import vn.binhnc.banhanga.retrofit.RetrofitClientNoti;
import vn.binhnc.banhanga.utils.StringUtil;
import vn.binhnc.banhanga.utils.Utils;
import vn.momo.momo_partner.AppMoMoLib;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;


public class CartFragment extends BaseFragment {
    private FragmentCartBinding mFragmentCartBinding;
    private CartAdapter mCartAdapter;
    private List<GioHang> mCartList;
    MainActivity mainActivity;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    int type_payment_index = 0;
    //=>
    // Tổng tiền
    private int mAmount = 0;
    public static int iddonhang;
    // Tổng số sản phẩm
    int totalItem = 0;

    // config thanh toán momo
    private String amount = "10000";
    private String fee = "0";
    int environment = 0; //developer default
    private String merchantName = "BinhNC";
    // merchantCode cần sửa khi đăng ký xong tài khoản momo cho doanh nghiệp
    private String merchantCode = "MOMOC2IC20220510";
    private String merchantNameLabel = "BinhNC";
    private String description = "Mua hàng online";

    //<=
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Chỉnh toàn màn hình
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), false);
        mFragmentCartBinding = FragmentCartBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        //=> Init các phương thức thanh toán online
        // Momo
        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION
        // Zalopay
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        //<=
        displayListFoodInCart();
        mFragmentCartBinding.tvOrderCart.setOnClickListener(v -> onClickOrderCart());
        //=>
        updateCountCart();
        return mFragmentCartBinding.getRoot();
    }

    private void updateCountCart() {
        Utils.COUNT_CART = LocalDatabase.getInstance(getActivity()).GioHangDAO().getCountCart();
        if (Utils.COUNT_CART > 0) {
            mainActivity.getBadgeDrawable().setVisible(true);
            mainActivity.getBadgeDrawable().setNumber(Utils.COUNT_CART);
            mFragmentCartBinding.tvEmptyCart.setVisibility(View.GONE);
            mFragmentCartBinding.layoutBottom.setVisibility(View.VISIBLE);
        } else {
            mainActivity.getBadgeDrawable().setVisible(false);
            mFragmentCartBinding.tvEmptyCart.setVisibility(View.VISIBLE);
            mFragmentCartBinding.layoutBottom.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(false, true, getString(R.string.nav_cart));
        }
    }

    private void displayListFoodInCart() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentCartBinding.rcvFoodCart.setLayoutManager(linearLayoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        mFragmentCartBinding.rcvFoodCart.addItemDecoration(itemDecoration);

        initDataFoodCart();
    }

    private void initDataFoodCart() {
        mCartList = new ArrayList<>();
        mCartList = LocalDatabase.getInstance(requireActivity()).GioHangDAO().getListCart();
        if (mCartList == null || mCartList.isEmpty()) {
            mFragmentCartBinding.tvEmptyCart.setVisibility(View.VISIBLE);
            mFragmentCartBinding.layoutBottom.setVisibility(View.GONE);
            return;
        }
        mFragmentCartBinding.tvEmptyCart.setVisibility(View.GONE);
        mFragmentCartBinding.layoutBottom.setVisibility(View.VISIBLE);
        //=>
        updateAdapter();
        mFragmentCartBinding.rcvFoodCart.setAdapter(mCartAdapter);
        calculateTotalPrice();
    }

    private void updateAdapter() {
        mCartAdapter = new CartAdapter(mCartList, new CartAdapter.IClickListener() {
            @Override
            public void clickDeleteItem(GioHang cart, int position) {
                deleteFromCart(cart, position);
            }

            @Override
            public void updateItem(GioHang cart, int position) {
                LocalDatabase.getInstance(requireActivity()).GioHangDAO().updateItem(cart);
                mCartList = LocalDatabase.getInstance(requireActivity()).GioHangDAO().getListCart();
                mCartAdapter.notifyItemChanged(position);
                calculateTotalPrice();
            }

            @Override
            public void updateTotalPrice() {
                calculateTotalPrice();
            }
        });
    }

    private void clearCart() {
        if (mCartList != null) {
            mCartList = LocalDatabase.getInstance(requireActivity()).GioHangDAO().getListCart();
            updateCountCart();
        }
        mCartAdapter.setListCart(mCartList);
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        List<GioHang> mOrderList = LocalDatabase.getInstance(requireActivity()).GioHangDAO().getListOrder();
        if (mOrderList == null || mOrderList.isEmpty()) {
            String strZero = 0 + Constant.CURRENCY;
            mFragmentCartBinding.tvTotalPrice.setText(strZero);
            mAmount = 0;
            return;
        }

        int totalPrice = 0;
        for (GioHang cart : mOrderList) {
            totalPrice = totalPrice + cart.getTotalPrice();
        }

        String strTotalPrice = totalPrice + Constant.CURRENCY;
        mFragmentCartBinding.tvTotalPrice.setText(strTotalPrice);
        mAmount = totalPrice;
    }

    private void deleteFromCart(GioHang cart, int position) {
        new AlertDialog.Builder(requireActivity())
                .setTitle(getString(R.string.confirm_delete_food))
                .setMessage(getString(R.string.message_delete_food))
                .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    mCartList.remove(position);
                    mCartAdapter.notifyItemRemoved(position);
                    LocalDatabase.getInstance(requireActivity()).GioHangDAO().deleteItem(cart);
                    //=>
                    updateCountCart();
                    //<=
                    calculateTotalPrice();
                })
                .setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Start momo
    //Get token through MoMo app
    private void requestPayment(String iddonhang) {
        AppMoMoLib.getInstance().setAction(AppMoMoLib.ACTION.PAYMENT);
        AppMoMoLib.getInstance().setActionType(AppMoMoLib.ACTION_TYPE.GET_TOKEN);
        if (mAmount > 0) {
            amount = String.valueOf(mAmount * 1000);
        }
        cleanCart();
        Map<String, Object> eventValue = new HashMap<>();
        //client Required
        eventValue.put("merchantname", merchantName); //Tên đối tác. được đăng ký tại https://business.momo.vn. VD: Google, Apple, Tiki , CGV Cinemas
        eventValue.put("merchantcode", merchantCode); //Mã đối tác, được cung cấp bởi MoMo tại https://business.momo.vn
        eventValue.put("amount", amount); //Kiểu integer
        //uniqueue id cho Bill order, giá trị duy nhất cho mỗi đơn hàng
        eventValue.put("orderId", iddonhang);
        eventValue.put("orderLabel", iddonhang); //gán nhãn

        //client Optional - bill info
        eventValue.put("merchantnamelabel", "Dịch vụ");//gán nhãn
        eventValue.put("fee", "0"); //Kiểu integer
        eventValue.put("description", description); //mô tả đơn hàng - short description

        //client extra data
        eventValue.put("requestId", merchantCode + "merchant_billId_" + System.currentTimeMillis());
        eventValue.put("partnerCode", merchantCode);
        //Example extra data
        JSONObject objExtraData = new JSONObject();
        try {
            objExtraData.put("site_code", "008");
            objExtraData.put("site_name", "CGV Cresent Mall");
            objExtraData.put("screen_code", 0);
            objExtraData.put("screen_name", "Special");
            objExtraData.put("movie_name", "Kẻ Trộm Mặt Trăng 3");
            objExtraData.put("movie_format", "2D");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eventValue.put("extraData", objExtraData.toString());

        eventValue.put("extra", "");
        AppMoMoLib.getInstance().requestMoMoCallBack(requireActivity(), eventValue);
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
                                    data.put("body", "Bạn có đơn đặt hàng mới");
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppMoMoLib.getInstance().REQUEST_CODE_MOMO && resultCode == -1) {
            if (data != null) {
                if (data.getIntExtra("status", -1) == 0) {
                    //TOKEN IS AVAILABLE
                    Log.d("thanhcong", data.getStringExtra("message"));
                    String token = data.getStringExtra("data"); //Token response
                    // Update token for database
                    compositeDisposable.add(apiBanHang.updateOnlineCode(iddonhang, token)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        if (messageModel.isSuccess()) {
                                            Toast.makeText(requireActivity(),
                                                    "Thành công!",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    throwable -> {
                                        Log.d("error", throwable.getMessage());
                                    }
                            ));
                    //
                    String phoneNumber = data.getStringExtra("phonenumber");
                    String env = data.getStringExtra("env");
                    if (env == null) {
                        env = "app";
                    }

                    if (token != null && !token.equals("")) {
                        // TODO: send phoneNumber & token to your server side to process payment with MoMo server
                        // IF Momo topup success, continue to process your order
                    } else {
                        Log.d("thanhcong", "Khong thanh cong");
                    }
                } else if (data.getIntExtra("status", -1) == 1) {
                    //TOKEN FAIL
                    String message = data.getStringExtra("message") != null ? data.getStringExtra("message") : "Thất bại";
                    Log.d("thanhcong", "Khong thanh cong");
                } else if (data.getIntExtra("status", -1) == 2) {
                    //TOKEN FAIL
                    Log.d("thanhcong", "Khong thanh cong");
                } else {
                    //TOKEN FAIL
                    Log.d("thanhcong", "Khong thanh cong");
                }
            } else {
                Log.d("thanhcong", "Khong thanh cong");
            }
        } else {
            Log.d("thanhcong", "Khong thanh cong");
        }
    }

    private void requestZalo() {
        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder(String.valueOf(mAmount * 1000));
            cleanCart();
            String code = data.getString("return_code");
            Log.d("test", code);
            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                Log.d("test", token);
                ZaloPaySDK.getInstance().payOrder(requireActivity(), token, "demozpdk://app",
                        new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                compositeDisposable.add(apiBanHang.updateOnlineCode(iddonhang, token)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                messageModel -> {
                                                    if (messageModel.isSuccess()) {
                                                        Toast.makeText(requireActivity(),
                                                                        "Thành công!",
                                                                        Toast.LENGTH_SHORT)
                                                                .show();
                                                    }
                                                },
                                                throwable -> {
                                                    Log.d("error", Objects.requireNonNull(throwable.getMessage()));
                                                }
                                        ));
                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {

                            }

                            @Override
                            public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {

                            }
                        });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickOrderCart() {
        if (getActivity() == null) {
            return;
        }

        if (mCartList == null || mCartList.isEmpty()) {
            return;
        }
        if (!Utils.mangmuahang.isEmpty()) {
            Utils.mangmuahang.clear();
        }
        Utils.mangmuahang = LocalDatabase.getInstance(requireActivity()).GioHangDAO().getListOrder();
        if (Utils.mangmuahang.isEmpty()) {
            return;
        }

        View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_order, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);

        // init ui
        TextView tvFoodsOrder = viewDialog.findViewById(R.id.tv_foods_order);
        TextView tvPriceOrder = viewDialog.findViewById(R.id.tv_price_order);
        EditText edtNameOrder = viewDialog.findViewById(R.id.edt_name_order);
        TextView edtPhoneOrder = viewDialog.findViewById(R.id.edt_phone_order);
        TextView edtAddressOrder = viewDialog.findViewById(R.id.edt_address_order);
        TextView tvCancelOrder = viewDialog.findViewById(R.id.tv_cancel_order);
        TextView tvCreateOrder = viewDialog.findViewById(R.id.tv_create_order);
        //
        edtNameOrder.setFocusable(true);
        edtNameOrder.requestFocus();
        //=> Spinner thanh toán
        Spinner spinner = viewDialog.findViewById(R.id.spinner_order);
        // Set data
        tvFoodsOrder.setText(getStringListOrder());
        tvPriceOrder.setText(mFragmentCartBinding.tvTotalPrice.getText().toString());
        //=>
        List<String> itemList = new ArrayList<>();
        itemList.add("Tiền mặt");
        itemList.add("MoMo");
        itemList.add("ZaloPay");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, itemList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type_payment_index = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // Set listener
        tvCancelOrder.setOnClickListener(v -> bottomSheetDialog.dismiss());
        // Set các giá trị mặc định từ tài khoản user đã đăng nhập
        edtNameOrder.setText(Utils.user_current.getUsername());
        edtPhoneOrder.setText(Utils.user_current.getMobile());
        edtNameOrder.setSelection(edtNameOrder.length());
        //=>
        tvCreateOrder.setOnClickListener(v -> {
            int user_id = Utils.user_current.getId();
            String phonePattern = "^(0|\\+84)\\d{9}$";
            String strName = edtNameOrder.getText().toString().trim();
            String strPhone = edtPhoneOrder.getText().toString().trim();
            String strAddress = edtAddressOrder.getText().toString().trim();
            String strEmail = Utils.user_current.getEmail();
            if (StringUtil.isEmpty(strName) || StringUtil.isEmpty(strPhone) || StringUtil.isEmpty(strAddress)) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.message_enter_infor_order));
            } else if (!strPhone.matches(phonePattern)) {
                Toast.makeText(requireActivity(), "Số điện thoại?", Toast.LENGTH_SHORT).show();
            } else {
                if (strPhone.startsWith("0")) {
                    strPhone = "+84" + strPhone.substring(1);
                }
                if (type_payment_index == 0) {
                    // Thanh toán khi nhận hàng
                    // Log kiểm tra thui
                    Log.d("test", new Gson().toJson(Utils.mangmuahang));
                    compositeDisposable.add(apiBanHang.createOrder(strEmail, strPhone,
                                    String.valueOf(mAmount), user_id, strAddress, totalItem,
                                    new Gson().toJson(Utils.mangmuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        // Gửi thông báo đến admin là có đơn hàng mới
                                        pushNotiToUser();
                                        cleanCart();
                                        bottomSheetDialog.dismiss();
                                        Toast.makeText(requireActivity(), "Thành công", Toast.LENGTH_SHORT).show();
                                    }, throwable -> {
                                        Toast.makeText(requireActivity(),
                                                throwable.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                            ));
                } else if (type_payment_index == 1) {
                    // Momo
                    // Log kiểm tra thui
                    Log.d("test", new Gson().toJson(Utils.mangmuahang));
                    compositeDisposable.add(apiBanHang.createOrder(strEmail, strPhone,
                                    String.valueOf(mAmount), user_id, strAddress, totalItem,
                                    new Gson().toJson(Utils.mangmuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        // Gửi thông báo đến admin là có đơn hàng mới
                                        pushNotiToUser();
                                        iddonhang = Integer.parseInt(messageModel.getIddonhang());
                                        bottomSheetDialog.dismiss();
                                        requestPayment(messageModel.getIddonhang());
                                    }, throwable -> {
                                        Toast.makeText(requireActivity(),
                                                throwable.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                            ));
                } else if (type_payment_index == 2) {
                    // ZaloPay
                    Log.d("test", new Gson().toJson(Utils.mangmuahang)); // Log kiểm tra thui
                    compositeDisposable.add(apiBanHang.createOrder(strEmail, strPhone,
                                    String.valueOf(mAmount), user_id, strAddress, totalItem,
                                    new Gson().toJson(Utils.mangmuahang))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        // Gửi thông báo đến admin là có đơn hàng mới
                                        pushNotiToUser();
                                        iddonhang = Integer.parseInt(messageModel.getIddonhang());
                                        bottomSheetDialog.dismiss();
                                        requestZalo();
                                    }, throwable -> {
                                        Toast.makeText(requireActivity(),
                                                throwable.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                            ));
                }
            }
        });
        bottomSheetDialog.show();
    }

    private void cleanCart() {
        LocalDatabase.getInstance(requireActivity()).GioHangDAO().deleteOrder();
        Utils.mangmuahang.clear();
        clearCart();
        if (mCartList == null || mCartList.isEmpty()) {
            mFragmentCartBinding.tvEmptyCart.setVisibility(View.VISIBLE);
            mFragmentCartBinding.layoutBottom.setVisibility(View.GONE);
        }
    }

    private String getStringListOrder() {
        if (mCartList == null || mCartList.isEmpty()) {
            return "";
        }
        List<GioHang> mOrderList = LocalDatabase.getInstance(requireActivity()).GioHangDAO().getListOrder();
        String result = "";
        for (GioHang cart : mOrderList) {
            if (StringUtil.isEmpty(result)) {
                result = "- " + cart.getTensp() + " (" + cart.getGiaThat() + Constant.CURRENCY + ") "
                        + "- " + getString(R.string.quantity) + " " + cart.getSoluong();
            } else {
                result = result + "\n" + "- " + cart.getTensp() + " (" + cart.getGiaThat() + Constant.CURRENCY + ") "
                        + "- " + getString(R.string.quantity) + " " + cart.getSoluong();
            }
            totalItem += cart.getSoluong();
        }
        return result;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReloadListCartEvent event) {
        displayListFoodInCart();
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

    @Override
    public void onResume() {
        super.onResume();
        WindowCompat.setDecorFitsSystemWindows(requireActivity().getWindow(), false);
    }
}