package vn.binhnc.manager.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import vn.binhnc.manager.model.GioHang;

@Dao
public interface GioHangDAO {

    @Insert
    void insertItem(GioHang cart);

    // Lấy ra danh sách giỏ hàng
    @Query("SELECT * FROM giohang")
    List<GioHang> getListCart();

    // Kiểm tra sản phẩm có trong giỏ hàng
    @Query("SELECT * FROM giohang WHERE idsp=:id")
    List<GioHang> checkItemInCart(int id);

    @Query("SELECT * FROM giohang WHERE isChecked=1")
    List<GioHang> getListOrder();

    // Kiểm tra sản phẩm đã được chọn để mua hàng?
    @Query("SELECT * FROM giohang WHERE idsp=:id AND isChecked=:isOrder LIMIT 1")
    GioHang checkItemInOrder(int id, boolean isOrder);

    // Lấy ra số lượng sản phẩm có trong giỏ hàng (Tính sản phẩm riêng biệt)
    @Query("SELECT COUNT(*) FROM giohang")
    int getCountCart();

    @Query("SELECT COUNT(*) FROM giohang WHERE isChecked=1")
    int getCountOrder();

    // Cập nhật lại sản phẩm trong giỏ hàng
    @Update
    void updateItem(GioHang cart);

    // Xóa sản phẩm khỏi giỏ hàng
    @Delete
    void deleteItem(GioHang cart);

    // Xóa tất cả (Khi đã thanh toán)
    @Query("DELETE FROM giohang WHERE isChecked=1")
    void deleteOrder();
}