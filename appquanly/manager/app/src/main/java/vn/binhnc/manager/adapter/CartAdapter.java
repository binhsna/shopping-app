package vn.binhnc.manager.adapter;

import android.os.Handler;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.binhnc.manager.constant.Constant;
import vn.binhnc.manager.database.LocalDatabase;
import vn.binhnc.manager.databinding.ItemCartBinding;
import vn.binhnc.manager.model.GioHang;
import vn.binhnc.manager.utils.GlideUtils;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<GioHang> mCartList;
    private final IClickListener iClickListener;

    public interface IClickListener {
        void clickDeleteItem(GioHang cart, int position);

        void updateItem(GioHang cart, int position);

        void updateTotalPrice();
    }

    public CartAdapter(List<GioHang> mList, IClickListener iClickListener) {
        this.mCartList = mList;
        this.iClickListener = iClickListener;
    }

    public void setListCart(List<GioHang> mList) {
        this.mCartList = mList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding itemCartBinding = ItemCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(itemCartBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        GioHang cart = mCartList.get(position);
        if (cart == null) {
            return;
        }
        //=> IsChecked
        GioHang checkItemInOrder = LocalDatabase.getInstance(holder.itemView.getContext()).GioHangDAO().checkItemInOrder(cart.getIdsp(), true);
        if (checkItemInOrder != null) {
            cart.setChecked(true);
            holder.mItemCartBinding.itemCheckCart.setChecked(true);
        } else {
            cart.setChecked(false);
            holder.mItemCartBinding.itemCheckCart.setChecked(false);
        }
        //=>
        GlideUtils.loadUrl(cart.getHinhsp(), holder.mItemCartBinding.imgFoodCart);
        holder.mItemCartBinding.tvFoodNameCart.setText(cart.getTensp());

        String strPriceCart = cart.getGiasp() + Constant.CURRENCY;
        if (cart.getGiamgia() > 0) {
            strPriceCart = cart.getGiaThat() + Constant.CURRENCY;
        }
        holder.mItemCartBinding.tvFoodPriceCart.setText(strPriceCart);
        holder.mItemCartBinding.tvCount.setText(String.valueOf(cart.getSoluong()));
        //=> Giảm số lượng
        holder.mItemCartBinding.tvSubtract.setOnClickListener(v -> {
            String strCountSub = holder.mItemCartBinding.tvCount.getText().toString().trim();
            int countSub = Integer.parseInt(strCountSub);
            if (countSub > 1) {
                int newCount = countSub - 1;
                holder.mItemCartBinding.tvCount.setText(String.valueOf(newCount));
                int totalPrice = cart.getGiaThat() * newCount;
                cart.setSoluong(newCount);
                cart.setTotalPrice(totalPrice);
                iClickListener.updateItem(cart, holder.getBindingAdapterPosition());
            } else {
                iClickListener.clickDeleteItem(cart, holder.getBindingAdapterPosition());
                notifyDataSetChanged();
            }
        });
        //=> Thêm số lượng
        holder.mItemCartBinding.tvAdd.setOnClickListener(v -> {
            String strCountAdd = holder.mItemCartBinding.tvCount.getText().toString().trim();
            int countAdd = Integer.parseInt(strCountAdd);
            if (countAdd < cart.getSltonkho()) {
                int newCount = countAdd + 1;
                holder.mItemCartBinding.tvCount.setText(String.valueOf(newCount));
                int totalPrice = cart.getGiaThat() * newCount;
                cart.setSoluong(newCount);
                cart.setTotalPrice(totalPrice);
                iClickListener.updateItem(cart, holder.getBindingAdapterPosition());
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Thông báo")
                        .setMessage("Hết hàng.");

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                // Đóng thông báo sau 2 giây
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                    }
                }, 2000);
                // Đặt sự kiện khi người dùng nhấn bất kỳ nơi nào bên ngoài để đóng thông báo
                alertDialog.setCanceledOnTouchOutside(true);
            }
        });
        // Xóa sản phẩm
        holder.mItemCartBinding.tvDelete.setOnClickListener(v -> {
            iClickListener.clickDeleteItem(cart, holder.getBindingAdapterPosition());
            notifyDataSetChanged();
        });
        //=>
        holder.mItemCartBinding.itemCheckCart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GioHang tempCart = mCartList.get(holder.getBindingAdapterPosition());
                if (isChecked) {
                    if (!tempCart.isChecked()) {
                        tempCart.setChecked(true);
                    }
                } else {
                    tempCart.setChecked(false);
                }
                LocalDatabase.getInstance(holder.itemView.getContext()).GioHangDAO().updateItem(tempCart);
                iClickListener.updateTotalPrice();
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == mCartList ? 0 : mCartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        private ItemCartBinding mItemCartBinding;

        public CartViewHolder(ItemCartBinding itemCartBinding) {
            super(itemCartBinding.getRoot());
            this.mItemCartBinding = itemCartBinding;
        }
    }
}
