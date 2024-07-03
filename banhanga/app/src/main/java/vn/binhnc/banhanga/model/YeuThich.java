package vn.binhnc.banhanga.model;

import androidx.room.Entity;
import androidx.room.Ignore;

import java.io.Serializable;

@Entity(tableName = "yeuthich", primaryKeys = {"sp_id", "user_id"})
public class YeuThich implements Serializable {
    private int sp_id;
    private int user_id;

    public YeuThich() {
    }

    @Ignore
    public YeuThich(int sp_id, int user_id) {
        this.sp_id = sp_id;
        this.user_id = user_id;
    }

    public int getSp_id() {
        return sp_id;
    }

    public void setSp_id(int sp_id) {
        this.sp_id = sp_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}