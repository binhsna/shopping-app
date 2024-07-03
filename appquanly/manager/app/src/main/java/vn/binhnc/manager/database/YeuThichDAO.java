package vn.binhnc.manager.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import vn.binhnc.manager.model.YeuThich;

@Dao
public interface YeuThichDAO {

    @Insert
    void insertYeuThich(YeuThich yeuThich);

    @Query("SELECT * FROM yeuthich WHERE user_id=:user_id")
    List<YeuThich> getListYeuThich(int user_id);

    @Query("SELECT * FROM yeuthich WHERE sp_id=:sp_id AND user_id=:user_id LIMIT 1")
    YeuThich checkSanPhamYeuThich(int sp_id, int user_id);

    @Query("SELECT COUNT(*) FROM yeuthich WHERE user_id=:user_id")
    int getCount(int user_id);

    @Delete
    void deleteSanPhamYeuThich(YeuThich yeuThich);

    @Update
    void updateYeuThich(YeuThich yeuThich);

    @Query("DELETE from yeuthich")
    void deleteAllYeuThich();
}