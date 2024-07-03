package vn.binhnc.banhanga.adapter;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import vn.binhnc.banhanga.databinding.ItemOrderBinding;
import vn.binhnc.banhanga.listener.ItemClickListener;
import vn.binhnc.banhanga.model.DonHang;
import vn.binhnc.banhanga.model.EventBus.DeleteOrderEvent;
import vn.binhnc.banhanga.utils.Utils;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    List<DonHang> listDonHang;

    public OrderAdapter(List<DonHang> listDonHang) {
        this.listDonHang = listDonHang;
    }

    public void DeleteOrder(int index) {
        listDonHang.remove(index);
        notifyDataSetChanged();
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
        holder.binding.trangthaidon.setText(Utils.statusOrder(donHang.getTrangthai()));
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
                    EventBus.getDefault().postSticky(
                            new DeleteOrderEvent(
                                    orderEvent.getId(),
                                    index,
                                    orderEvent.getTrangthai())
                    );
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

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, View.OnLongClickListener {
        private ItemClickListener itemClickListener;

        private final ItemOrderBinding binding;

        public OrderViewHolder(ItemOrderBinding mItemOrderBinding) {
            super(mItemOrderBinding.getRoot());
            this.binding = mItemOrderBinding;

            this.binding.getRoot().setOnClickListener(this);
            this.binding.getRoot().setOnCreateContextMenuListener(this);
            this.binding.getRoot().setOnLongClickListener(this);
        }

        //=>
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            contextMenu.add(0, 0, getAdapterPosition(), "Xóa");
        }

        @Override
        public boolean onLongClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), true);
            return false;
        }
    }
}
