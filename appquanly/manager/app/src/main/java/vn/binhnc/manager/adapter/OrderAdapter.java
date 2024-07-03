package vn.binhnc.manager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import vn.binhnc.manager.databinding.ItemOrderBinding;

import vn.binhnc.manager.listener.ItemClickListener;
import vn.binhnc.manager.model.DonHang;
import vn.binhnc.manager.model.EventBus.DonHangEvent;
import vn.binhnc.manager.utils.Utils;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    List<DonHang> listDonHang;

    public OrderAdapter(List<DonHang> listDonHang) {
        this.listDonHang = listDonHang;
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        DonHang donHang = listDonHang.get(position);
        holder.binding.iddonhang.setText("Đơn hàng: " + donHang.getId());
        holder.binding.diachiDonhang.setText("Địa chỉ: " + donHang.getDiachi());
        holder.binding.userDonhang.setText("Người đặt: " + donHang.getUsername());
        holder.binding.tinhtrang.setText(Utils.statusOrder(donHang.getTrangthai()));
        //=>
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.binding.recyclerviewChitiet.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(donHang.getItem().size());
        // adapter chitiet
        ChitietAdapter chitietAdapter = new ChitietAdapter(
                holder.binding.getRoot().getContext(),
                donHang.getItem());
        holder.binding.recyclerviewChitiet.setLayoutManager(layoutManager);
        holder.binding.recyclerviewChitiet.setAdapter(chitietAdapter);
        holder.binding.recyclerviewChitiet.setRecycledViewPool(viewPool);
        //=>
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if (isLongClick) {
                    int index = holder.getBindingAdapterPosition();
                    DonHang orderEvent = listDonHang.get(index);
                    EventBus.getDefault().postSticky(new DonHangEvent(orderEvent));
                }
            }
        });
        // <=
    }

    @Override
    public int getItemCount() {
        if (listDonHang != null) {
            return listDonHang.size();
        }
        return 0;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private ItemClickListener itemClickListener;

        private final ItemOrderBinding binding;

        public OrderViewHolder(ItemOrderBinding mItemOrderBinding) {
            super(mItemOrderBinding.getRoot());
            this.binding = mItemOrderBinding;

            this.binding.getRoot().setOnLongClickListener(this);
        }

        //=>
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return false;
        }
    }
}
