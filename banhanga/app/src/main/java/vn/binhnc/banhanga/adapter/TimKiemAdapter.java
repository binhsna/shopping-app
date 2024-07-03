package vn.binhnc.banhanga.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.binhnc.banhanga.R;
import vn.binhnc.banhanga.constant.Constant;
import vn.binhnc.banhanga.database.LocalDatabase;
import vn.binhnc.banhanga.databinding.ItemSearchSanphamBinding;
import vn.binhnc.banhanga.listener.IOnClickSanPhamListener;
import vn.binhnc.banhanga.model.GioHang;
import vn.binhnc.banhanga.model.SanPham;
import vn.binhnc.banhanga.utils.GlideUtils;
import vn.binhnc.banhanga.utils.Utils;


public class TimKiemAdapter extends RecyclerView.Adapter<TimKiemAdapter.MyViewHolder> {
    List<SanPham> mList;
    private IOnClickSanPhamListener listener;
    private IClickAddToCartListener mIClickAddToCartListener;

    public interface IClickAddToCartListener {
        void onClickAddToCart(ImageView imgAddToCart, SanPham sanPham);
    }

    public TimKiemAdapter(List<SanPham> list, IOnClickSanPhamListener listener, IClickAddToCartListener addToCartListener) {
        this.mList = list;
        this.listener = listener;
        this.mIClickAddToCartListener = addToCartListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchSanphamBinding binding = ItemSearchSanphamBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SanPham sanPham = mList.get(position);
        //holder.setBinding(sanPham);
        String strHinhAnh = "";
        if (sanPham.getHinhanh().contains("http")) {
            strHinhAnh = sanPham.getHinhanh();
        } else {
            strHinhAnh = Utils.BASE_URL + "images/" + sanPham.getHinhanh();
        }
        GlideUtils.loadUrl(strHinhAnh, holder.binding.imgSanpham);
        //=>
        holder.binding.tvTenSp.setText(sanPham.getTensp());
        String strGia = sanPham.getGiaThat() + Constant.CURRENCY;
        holder.binding.tvGia.setText(strGia);
        holder.binding.tvTenSp.setOnClickListener(v -> listener.onClickSanPham(sanPham));
        holder.binding.tvGia.setOnClickListener(v -> listener.onClickSanPham(sanPham));
        holder.binding.imgSanpham.setOnClickListener(v -> listener.onClickSanPham(sanPham));
        //=>
        List<GioHang> mCartList = LocalDatabase.getInstance(holder.itemView.getContext()).GioHangDAO().checkItemInCart(sanPham.getId());
        if (mCartList.isEmpty()) {
            holder.binding.imgAddToCart.setBackgroundResource(R.drawable.bg_red_corner_6);
            holder.binding.imgAddToCart.setOnClickListener(v -> {
                if (!sanPham.isAddToCart()) {
                    mIClickAddToCartListener.onClickAddToCart(holder.binding.imgAddToCart, sanPham);
                }
            });
        } else {
            holder.binding.imgAddToCart.setBackgroundResource(R.drawable.bg_gray_corner_6);
        }
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemSearchSanphamBinding binding;

        public MyViewHolder(ItemSearchSanphamBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setBinding(SanPham sanPham) {
            binding.getRoot().setOnClickListener(view -> listener.onClickSanPham(sanPham));
        }
    }
}
