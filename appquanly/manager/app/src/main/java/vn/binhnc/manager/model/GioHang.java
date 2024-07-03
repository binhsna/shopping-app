package vn.binhnc.manager.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "giohang")
public class GioHang {
    @PrimaryKey
    private int idsp;
    private String tensp;
    private int giasp;
    private int giamgia;
    private String hinhsp;
    private int soluong;
    // Kiểm tra add to card?
    private boolean isAddToCart;

    // Kiểm tra đã chọn để mua hàng?
    private boolean isChecked;
    private int sltonkho;
    // Tổng giá tiền sản phẩm theo id
    private int totalPrice;

    public GioHang() {
    }

    public int getIdsp() {
        return idsp;
    }

    public void setIdsp(int idsp) {
        this.idsp = idsp;
    }

    public String getTensp() {
        return tensp;
    }

    public void setTensp(String tensp) {
        this.tensp = tensp;
    }

    public int getGiasp() {
        return giasp;
    }

    public void setGiasp(int giasp) {
        this.giasp = giasp;
    }

    public int getGiaThat() {
        if (giamgia <= 0) {
            return giasp;
        }
        return giasp - (giasp * giamgia / 100);
    }

    public String getHinhsp() {
        return hinhsp;
    }

    public void setHinhsp(String hinhsp) {
        this.hinhsp = hinhsp;
    }

    public int getSoluong() {
        return soluong;
    }

    public void setSoluong(int soluong) {
        this.soluong = soluong;
    }

    public int getGiamgia() {
        return giamgia;
    }

    public void setGiamgia(int giamgia) {
        this.giamgia = giamgia;
    }

    public boolean isAddToCart() {
        return isAddToCart;
    }

    public void setAddToCart(boolean addToCart) {
        isAddToCart = addToCart;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getSltonkho() {
        return sltonkho;
    }

    public void setSltonkho(int sltonkho) {
        this.sltonkho = sltonkho;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

}
