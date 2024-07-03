package vn.binhnc.banhanga.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "sanpham")
public class SanPham implements Serializable {
    @PrimaryKey
    private int id;
    private String tensp;
    private String hinhanh;
    private String giasp;
    private String mota;
    private int moi;
    private int giamgia;
    private int loai;
    private String linkvideo;
    private String linksource;
    private int sltonkho;
    // Lưu id user
    private int user_id;

    // Kiểm tra add to card
    private boolean isAddToCart;
    // Tính số lượng sản phẩm trong card theo id
    private int count;
    // Tổng giá tiền sản phẩm theo id
    private int totalPrice;


    public SanPham() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    public String getGiasp() {
        return giasp;
    }

    public void setGiasp(String giasp) {
        this.giasp = giasp;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getGiaThat() {
        int gia = Integer.valueOf(giasp);
        if (giamgia <= 0) {
            return gia;
        }
        return gia - (gia * giamgia / 100);
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }

    public int getMoi() {
        return moi;
    }

    public void setMoi(int moi) {
        this.moi = moi;
    }

    public int getGiamgia() {
        return giamgia;
    }

    public void setGiamgia(int giamgia) {
        this.giamgia = giamgia;
    }

    public int getLoai() {
        return loai;
    }

    public void setLoai(int loai) {
        this.loai = loai;
    }

    public String getLinkvideo() {
        return linkvideo;
    }

    public void setLinkvideo(String linkvideo) {
        this.linkvideo = linkvideo;
    }

    public String getLinksource() {
        return linksource;
    }

    public void setLinksource(String linksource) {
        this.linksource = linksource;
    }

    public int getSltonkho() {
        return sltonkho;
    }

    public void setSltonkho(int sltonkho) {
        this.sltonkho = sltonkho;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public boolean isAddToCart() {
        return isAddToCart;
    }

    public void setAddToCart(boolean addToCart) {
        isAddToCart = addToCart;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
