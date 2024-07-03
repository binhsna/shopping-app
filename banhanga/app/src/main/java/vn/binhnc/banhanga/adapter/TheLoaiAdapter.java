package vn.binhnc.banhanga.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.binhnc.banhanga.databinding.ItemTheloaiBinding;
import vn.binhnc.banhanga.listener.IOnClickTheLoaiListener;
import vn.binhnc.banhanga.model.TheLoai;
import vn.binhnc.banhanga.utils.GlideUtils;
import vn.binhnc.banhanga.utils.Utils;


public class TheLoaiAdapter extends RecyclerView.Adapter<TheLoaiAdapter.MyViewHolder> {
    List<TheLoai> list;
    private IOnClickTheLoaiListener listener;


    public TheLoaiAdapter(List<TheLoai> list, IOnClickTheLoaiListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTheloaiBinding binding = ItemTheloaiBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TheLoai theLoai = list.get(position);
        holder.setBinding(list, position);
        holder.binding.tvCategoryName.setText(theLoai.getTentheloai());
        String strHinhAnh = "";
        if (theLoai.getHinhanh().contains("http")) {
            strHinhAnh = theLoai.getHinhanh();
        } else {
            strHinhAnh = Utils.BASE_URL + "images/" + theLoai.getHinhanh();
        }
        GlideUtils.loadUrl(strHinhAnh, holder.binding.imageCategory);
        // Glide.with(holder.itemView).load(list.get(position).getHinhanh()).into(holder.binding.imageCategory);
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemTheloaiBinding binding;

        public MyViewHolder(ItemTheloaiBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setBinding(List<TheLoai> list, int position) {
            binding.getRoot().setOnClickListener(view -> listener.onClickTheLoai(list, position));
        }
    }
}
