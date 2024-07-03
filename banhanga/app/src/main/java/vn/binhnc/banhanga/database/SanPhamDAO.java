package vn.binhnc.banhanga.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import vn.binhnc.banhanga.model.SanPham;

@Dao
public interface SanPhamDAO {

    @Insert
    void insertSanPham(SanPham sanpham);

    @Query("SELECT * FROM sanpham WHERE user_id=:user_id")
    List<SanPham> getListSanPhamCart(int user_id);

    @Query("SELECT * FROM sanpham WHERE id=:id AND user_id=:user_id")
    List<SanPham> checkSanPhamInCart(int id, int user_id);

    @Query("SELECT COUNT(*) FROM sanpham WHERE user_id=:user_id")
    int getCartCount(int user_id);

    @Delete
    void deleteSanPham(SanPham sanpham);

    @Update
    void updateSanPham(SanPham sanpham);

    // Xóa giỏ hàng (Khi đã thanh toán)
    @Query("DELETE FROM sanpham WHERE user_id=:user_id")
    void deleteCart(int user_id);

    @Query("DELETE from sanpham")
    void deleteAllSanPham();
}