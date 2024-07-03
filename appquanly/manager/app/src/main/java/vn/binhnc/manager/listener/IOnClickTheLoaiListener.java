package vn.binhnc.manager.listener;


import java.util.List;

import vn.binhnc.manager.model.TheLoai;

public interface IOnClickTheLoaiListener {
    void onClickTheLoai(List<TheLoai> list, int position);
}
