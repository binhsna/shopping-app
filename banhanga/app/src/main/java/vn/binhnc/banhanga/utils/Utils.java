package vn.binhnc.banhanga.utils;

import android.app.AlertDialog;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import vn.binhnc.banhanga.model.GioHang;
import vn.binhnc.banhanga.model.User;

public class Utils {
    // MyHOME "http://192.168.1.63/banhanga/"
    // --> KHông thì phải đăng ký domain -> hosting
    // https://salefoodbnc.000webhostapp.com/banhanga/
    public static final String BASE_URL = "https://salefoodbnc.000webhostapp.com/banhanga/";
    public static String SHOP_NAME = "BNC Shop";
    public static int COUNT_CART = 0;
    public static List<GioHang> manggiohang;
    public static List<GioHang> mangmuahang = new ArrayList<>();
    public static User user_current = new User();
    public static String ID_RECEIVED;
    public static final String SENDID = "idsend";
    public static final String RECEIVEDID = "idreceived";
    public static final String MESS = "message";
    public static final String DATETIME = "datetime";
    public static final String PATH_CHAT = "chat";

    public static String statusOrder(int status) {
        String result = "";
        switch (status) {
            case 0:
                result = "Đơn hàng đang được xử lý";
                break;
            case 1:
                result = "Đơn hàng đã được tiếp nhận";
                break;
            case 2:
                result = "Đơn hàng đang được vận chuyển";
                break;
            case 3:
                result = "Giao hàng thành công";
                break;
            case 4:
                result = "Đơn hàng đã hủy";
                break;
            default:
                result = "...";
        }
        return result;
    }

    public static AlertDialog showDialogMessage(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title).setMessage(message).show();
        if (alertDialog.isShowing()) {
            alertDialog.cancel();
        }
        return alertDialog;
    }
}
