package vn.binhnc.manager.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.binhnc.manager.databinding.ItemMoreImageBinding;
import vn.binhnc.manager.model.HinhAnh;
import vn.binhnc.manager.utils.GlideUtils;
import vn.binhnc.manager.utils.Utils;

public class MoreImageAdapter extends RecyclerView.Adapter<MoreImageAdapter.MoreImageViewHolder> {

    private final List<HinhAnh> mListImages;

    public MoreImageAdapter(List<HinhAnh> mListImages) {
        this.mListImages = mListImages;
    }

    @NonNull
    @Override
    public MoreImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMoreImageBinding itemMoreImageBinding = ItemMoreImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MoreImageViewHolder(itemMoreImageBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreImageViewHolder holder, int position) {
        HinhAnh hinhAnh = mListImages.get(position);
        if (hinhAnh == null) {
            return;
        }
        String strHinhAnh = "";
        if (hinhAnh.getUrl().contains("http")) {
            strHinhAnh = hinhAnh.getUrl();
        } else {
            strHinhAnh = Utils.BASE_URL + "images/" + hinhAnh.getUrl();
        }
        GlideUtils.loadUrl(strHinhAnh, holder.mItemMoreImageBinding.imageFood);
    }

    @Override
    public int getItemCount() {
        return null == mListImages ? 0 : mListImages.size();
    }

    public static class MoreImageViewHolder extends RecyclerView.ViewHolder {

        private final ItemMoreImageBinding mItemMoreImageBinding;

        public MoreImageViewHolder(ItemMoreImageBinding itemMoreImageBinding) {
            super(itemMoreImageBinding.getRoot());
            this.mItemMoreImageBinding = itemMoreImageBinding;
        }
    }
}