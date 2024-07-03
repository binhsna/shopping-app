package vn.binhnc.manager.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.binhnc.manager.R;
import vn.binhnc.manager.activity.ChitietSanPhamActivity;
import vn.binhnc.manager.constant.Constant;
import vn.binhnc.manager.constant.GlobalFuntion;
import vn.binhnc.manager.database.LocalDatabase;
import vn.binhnc.manager.databinding.ItemLoadingBinding;
import vn.binhnc.manager.databinding.ItemRecyclerMealBinding;
import vn.binhnc.manager.model.SanPham;
import vn.binhnc.manager.model.YeuThich;
import vn.binhnc.manager.utils.GlideUtils;
import vn.binhnc.manager.utils.Utils;

public class RecyclerViewFavoriteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SanPham> mList;
    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private IOnClickRemoveFavoriteListener listener;

    public interface IOnClickRemoveFavoriteListener {
        void onRemoveFavorite(SanPham s, String action);
    }

    public RecyclerViewFavoriteAdapter(List<SanPham> mList) {
        this.mList = mList;
    }

    public void UpdateFavorite(IOnClickRemoveFavoriteListener listener) {
        this.listener = listener;
        notifyDataSetChanged();
    }

    public void DeleteItemFavorite(SanPham s) {
        mList.remove(s);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATA) {
            ItemRecyclerMealBinding binding = ItemRecyclerMealBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new RecyclerViewHolder(binding);
        } else {
            ItemLoadingBinding loading = ItemLoadingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new LoadingViewHolder(loading);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof RecyclerViewHolder) {
            RecyclerViewHolder myViewHolder = (RecyclerViewHolder) holder;
            SanPham sanPham = mList.get(i);
            if (sanPham == null) {
                return;
            }
            myViewHolder.setBinding(myViewHolder.itemView.getContext(), sanPham);
            String strHinhAnh = "";
            if (sanPham.getHinhanh().contains("http")) {
                strHinhAnh = sanPham.getHinhanh();
            } else {
                strHinhAnh = Utils.BASE_URL + "images/" + sanPham.getHinhanh();
            }
            GlideUtils.loadUrl(strHinhAnh, myViewHolder.binding.mealThumb);
            myViewHolder.binding.mealName.setText(sanPham.getTensp());
            //=> Gán biểu tượng yêu thích
            YeuThich check_favorite = LocalDatabase.getInstance(myViewHolder.itemView.getContext()).YeuThichDAO().checkSanPhamYeuThich(sanPham.getId(), Utils.user_current.getId());
            if (check_favorite != null) {
                myViewHolder.binding.love.setImageResource(R.drawable.ic_baseline_red_favorite);
            } else {
                myViewHolder.binding.love.setImageResource(R.drawable.ic_favorite_border);
            }
            //=>
            myViewHolder.binding.love.setOnClickListener(v -> listener.onRemoveFavorite(sanPham, "remove"));
            //<=
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

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ItemRecyclerMealBinding binding;

        public RecyclerViewHolder(ItemRecyclerMealBinding mItemRecyclerMealBinding) {
            super(mItemRecyclerMealBinding.getRoot());
            this.binding = mItemRecyclerMealBinding;
        }

        public void setBinding(Context context, SanPham sanPham) {
            binding.getRoot().setOnClickListener(view -> {
                goToDetail(context, sanPham);
            });
        }

        private void goToDetail(Context context, SanPham sanPham) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constant.KEY_INTENT_SANPHAM_OBJECT, sanPham);
            GlobalFuntion.startActivity(context, ChitietSanPhamActivity.class, bundle);
        }
    }
}

