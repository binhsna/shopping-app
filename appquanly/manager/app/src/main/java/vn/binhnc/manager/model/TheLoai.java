package vn.binhnc.manager.model;

import java.io.Serializable;

public class TheLoai implements Serializable {
    private int id;
    private String tentheloai;
    private String hinhanh;
    private String mota;

    public TheLoai() {
    }

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
