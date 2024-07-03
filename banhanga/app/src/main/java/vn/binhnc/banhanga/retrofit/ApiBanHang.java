package vn.binhnc.banhanga.retrofit;


import androidx.annotation.Nullable;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import vn.binhnc.banhanga.model.DonHangModel;
import vn.binhnc.banhanga.model.HinhAnhSanPhamModel;
import vn.binhnc.banhanga.model.KhuyenMaiModel;
import vn.binhnc.banhanga.model.MeetingModel;
import vn.binhnc.banhanga.model.MessageModel;
import vn.binhnc.banhanga.model.OrderModel;
import vn.binhnc.banhanga.model.SanPhamModel;
import vn.binhnc.banhanga.model.TheLoaiModel;
import vn.binhnc.banhanga.model.UserModel;
import vn.binhnc.banhanga.model.YeuThichModel;

public interface ApiBanHang {
    // GET DATA
    // Start Home Page
    @GET("getbanner.php")
    Observable<SanPhamModel> getBanner();

    @GET("gettheloai.php")
    Observable<TheLoaiModel> getTheLoai();

    @POST("getsanphammoi.php")
    @FormUrlEncoded
    Observable<SanPhamModel> getSanPhamMoi(
            @Field("page") int page
    );

    // End Home Page
    @GET("khuyenmai.php")
    Observable<KhuyenMaiModel> getKhuyenMai();

    @GET("getsanpham.php")
    Observable<SanPhamModel> getSanPham();

    // Lấy ra thêm hình ảnh theo id sản phẩm
    @POST("gethinhanhsanpham.php")
    @FormUrlEncoded
    Observable<HinhAnhSanPhamModel> getHinhAnhSanPham(
            @Field("id_sp") int id_sp
    );

    // Lấy về sản phẩm theo thể loại (Có phân trang)
    @POST("chitiet.php")
    @FormUrlEncoded
    Observable<SanPhamModel> getSanPhamOnPage(
            @Field("page") int page,
            @Field("loai") int loai
    );
    // Lấy về danh sách các sản phẩm yêu thích theo user_id (Có phân trang)

    @POST("getsanphamfavorite.php")
    @FormUrlEncoded
    Observable<SanPhamModel> getSanPhamFavoriteOnPage(
            @Field("page") int page,
            @Field("user_id") int user_id
    );

    // Đăng ký tài khoản user
    @POST("dangky.php")
    @FormUrlEncoded
    Observable<UserModel> dangky(
            @Field("email") String email,
            @Field("pass") String pass,
            @Field("username") String username,
            @Field("mobile") String mobile,
            @Field("uid") String uid
    );

    // get token
    @POST("gettoken.php")
    @FormUrlEncoded
    Observable<UserModel> gettoken(
            @Field("status") int status
    );

    // Update Token
    @POST("updatetoken.php")
    @FormUrlEncoded
    Observable<MessageModel> updateToken(
            @Field("id") int id,
            @Field("token") String token
    );

    // Update Token cho momo in donhang
    @POST("updateonlinecode.php")
    @FormUrlEncoded
    Observable<MessageModel> updateOnlineCode(
            @Field("iddonhang") int iddonhang,
            @Field("token") String token
    );

    // Đăng nhập
    @POST("dangnhap.php")
    @FormUrlEncoded
    Observable<UserModel> dangnhap(
            @Field("email") String email,
            @Field("pass") String pass
    );

    // Đăng nhập với OTP
    @POST("dangnhapotp.php")
    @FormUrlEncoded
    Observable<UserModel> dangnhap_otp(
            @Field("mobile") String mobile
    );

    // Reset lại mật khẩu
    // C1 -> lỗi
    @POST("reset.php")
    @FormUrlEncoded
    Observable<UserModel> resetPass(
            @Field("email") String email
    );

    // C2 -> Cập nhật pass trên firebase
    // ...
    // Đặt hàng
    @POST("donhang.php")
    @FormUrlEncoded
    Observable<OrderModel> createOrder(
            @Field("email") String email,
            @Field("sdt") String sdt,
            @Field("tongtien") String tongtien,
            @Field("iduser") int iduser,
            @Field("diachi") String diachi,
            @Field("soluong") int soluong,
            @Field("chitiet") String chitiet
    );

    // Xem lịch sử mua hàng (Đơn hàng đã mua)
    @POST("xemdonhang.php")
    @FormUrlEncoded
    Observable<DonHangModel> xemDonHang(
            @Field("user_id") int user_id
    );

    // Tìm kiếm sản phẩm
    @POST("timkiem.php")
    @FormUrlEncoded
    Observable<SanPhamModel> search(
            @Field("search") @Nullable String search
    );

    // Thêm/xóa sản phẩm yêu thích
    @POST("myfavorite.php")
    @FormUrlEncoded
    Observable<MessageModel> updateFavorite(
            @Field("sp_id") int sp_id,
            @Field("user_id") int user_id,
            @Field("action") String action
    );

    // Lấy ra danh sách sản phẩm yêu thích theo user_id
    @POST("getmyfavorite.php")
    @FormUrlEncoded
    Observable<YeuThichModel> favoriteList(
            @Field("user_id") int user_id
    );

    // Xóa đơn hàng
    @POST("deleteorder.php")
    @FormUrlEncoded
    Observable<OrderModel> deleteOrder(
            @Field("iddonhang") int id
    );

    // Upload file từ phone lên server
    @Multipart
    @POST("updateprofile.php")
    Call<MessageModel> uploadFile(
            @Part MultipartBody.Part file,
            @Part("id") RequestBody id,
            @Part("email") RequestBody email,
            @Part("username") RequestBody username,
            @Part("file_name") RequestBody file_name,
            @Part("mobile") RequestBody mobile
    );

    // Lấy về meeting
    @GET("getmeeting.php")
    Observable<MeetingModel> getMeeting();
}
