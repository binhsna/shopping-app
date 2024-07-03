package vn.binhnc.banhanga.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.binhnc.banhanga.databinding.ItemBannerBinding;
import vn.binhnc.banhanga.listener.IOnClickSanPhamListener;
import vn.binhnc.banhanga.model.SanPham;
import vn.binhnc.banhanga.utils.GlideUtils;
import vn.binhnc.banhanga.utils.Utils;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.MyViewHolder> {

    private final List<SanPham> mList;
    public final IOnClickSanPhamListener iOnClickSanPhamListener;

    public BannerAdapter(List<SanPham> mList, IOnClickSanPhamListener iOnClickSanPhamListener) {
        this.mList = mList;
        this.iOnClickSanPhamListener = iOnClickSanPhamListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBannerBinding binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SanPham sanPham = mList.get(position);
        if (sanPham == null) {
            return;
        }
        String strHinhAnh = "";
        if (sanPham.getHinhanh().contains("http")) {
            strHinhAnh = sanPham.getHinhanh();
        } else {
            strHinhAnh = Utils.BASE_URL + "images/" + sanPham.getHinhanh();
        }
        GlideUtils.loadUrlBanner(strHinhAnh, holder.mItemBannerBinding.imageSanpham);
        if (sanPham.getGiamgia() <= 0) {
            holder.mItemBannerBinding.tvSaleOff.setVisibility(View.GONE);
        } else {
            holder.mItemBannerBinding.tvSaleOff.setVisibility(View.VISIBLE);
            String strSale = "Giảm " + sanPham.getGiamgia() + "%";
            holder.mItemBannerBinding.tvSaleOff.setText(strSale);
        }
        holder.mItemBannerBinding.layoutItem.setOnClickListener(v -> iOnClickSanPhamListener.onClickSanPham(sanPham));
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private final ItemBannerBinding mItemBannerBinding;

        public MyViewHolder(@NonNull ItemBannerBinding mItemBannerBinding) {
            super(mItemBannerBinding.getRoot());
            this.mItemBannerBinding = mItemBannerBinding;
        }
    }
}
