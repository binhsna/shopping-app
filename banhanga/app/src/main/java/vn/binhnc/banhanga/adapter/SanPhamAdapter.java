package vn.binhnc.banhanga.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import vn.binhnc.banhanga.constant.Constant;
import vn.binhnc.banhanga.databinding.ItemSanphamBinding;
import vn.binhnc.banhanga.databinding.ItemLoadingBinding;
import vn.binhnc.banhanga.listener.IOnClickSanPhamListener;
import vn.binhnc.banhanga.model.SanPham;
import vn.binhnc.banhanga.utils.GlideUtils;
import vn.binhnc.banhanga.utils.Utils;

public class SanPhamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SanPham> mList;
    public final IOnClickSanPhamListener iOnClickSanPhamListener;
    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    public SanPhamAdapter(List<SanPham> mList, IOnClickSanPhamListener iOnClickSanPhamListener) {
        this.mList = mList;
        this.iOnClickSanPhamListener = iOnClickSanPhamListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATA) {
            ItemSanphamBinding binding = ItemSanphamBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MyViewHolder(binding);
        } else {
            ItemLoadingBinding loading = ItemLoadingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new LoadingViewHolder(loading);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            final SanPham sanPham = mList.get(position);
            if (sanPham == null) {
                return;
            }
            String strHinhAnh = "";
            if (sanPham.getHinhanh().contains("http")) {
                strHinhAnh = sanPham.getHinhanh();
            } else {
                strHinhAnh = Utils.BASE_URL + "images/" + sanPham.getHinhanh();
            }
            GlideUtils.loadUrl(strHinhAnh, myViewHolder.mItemSanphamBinding.imgSanpham);
            if (sanPham.getGiamgia() <= 0) {
                myViewHolder.mItemSanphamBinding.tvSaleOff.setVisibility(View.GONE);
                myViewHolder.mItemSanphamBinding.tvPrice.setVisibility(View.GONE);

                String strPrice = sanPham.getGiasp() + Constant.CURRENCY;
                myViewHolder.mItemSanphamBinding.tvPriceSale.setText(strPrice);
            } else {
                myViewHolder.mItemSanphamBinding.tvSaleOff.setVisibility(View.VISIBLE);
                myViewHolder.mItemSanphamBinding.tvPrice.setVisibility(View.VISIBLE);

                String strSale = "Giảm " + sanPham.getGiamgia() + "%";
                myViewHolder.mItemSanphamBinding.tvSaleOff.setText(strSale);

                String strOldPrice = sanPham.getGiasp() + Constant.CURRENCY;
                myViewHolder.mItemSanphamBinding.tvPrice.setText(strOldPrice);
                myViewHolder.mItemSanphamBinding.tvPrice.setPaintFlags(myViewHolder.mItemSanphamBinding.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                String strRealPrice = sanPham.getGiaThat() + Constant.CURRENCY;
                myViewHolder.mItemSanphamBinding.tvPriceSale.setText(strRealPrice);
            }
            myViewHolder.mItemSanphamBinding.tvFoodName.setText(sanPham.getTensp());
            //
            myViewHolder.mItemSanphamBinding.layoutItem.setOnClickListener(v -> iOnClickSanPhamListener.onClickSanPham(sanPham));
            //
        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.mItemLoadingBinding.progressbar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return null == mList ? 0 : mList.size();
    }

    // class loading progress bar
    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ItemLoadingBinding mItemLoadingBinding;

        public LoadingViewHolder(ItemLoadingBinding mItemLoadingBinding) {
            super(mItemLoadingBinding.getRoot());
            this.mItemLoadingBinding = mItemLoadingBinding;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ItemSanphamBinding mItemSanphamBinding;

        public MyViewHolder(ItemSanphamBinding mItemSanphamBinding) {
            super(mItemSanphamBinding.getRoot());
            this.mItemSanphamBinding = mItemSanphamBinding;
        }
    }
}
