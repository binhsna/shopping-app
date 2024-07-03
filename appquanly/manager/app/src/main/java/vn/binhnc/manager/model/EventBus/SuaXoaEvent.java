package vn.binhnc.manager.model.EventBus;

import vn.binhnc.manager.model.SanPham;

public class SuaXoaEvent {
    SanPham sanPham;

    public SuaXoaEvent(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }
}
