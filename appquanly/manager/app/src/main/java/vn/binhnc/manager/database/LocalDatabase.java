package vn.binhnc.manager.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import vn.binhnc.manager.model.GioHang;
import vn.binhnc.manager.model.SanPham;
import vn.binhnc.manager.model.YeuThich;

@Database(entities = {SanPham.class, GioHang.class, YeuThich.class}, version = 1)
public abstract class LocalDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "local_database.db";

    private static LocalDatabase instance;

    public static synchronized LocalDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), LocalDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract SanPhamDAO SanPhamDAO();

    public abstract GioHangDAO GioHangDAO();

    public abstract YeuThichDAO YeuThichDAO();
}
