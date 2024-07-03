package vn.binhnc.banhanga.model;

import java.io.Serializable;

public class TheLoai implements Serializable {
    int id;
    String tentheloai;
    String hinhanh;
    String mota;

    public TheLoai(String tentheloai, String hinhanh, String mota) {
        this.tentheloai = tentheloai;
        this.hinhanh = hinhanh;
        this.mota = mota;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTentheloai() {
        return tentheloai;
    }

    public void setTentheloai(String tentheloai) {
        this.tentheloai = tentheloai;
    }

    public String getHinhanh() {
        return hinhanh;
    }

    public void setHinhanh(String hinhanh) {
        this.hinhanh = hinhanh;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }
}
