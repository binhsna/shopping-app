package vn.binhnc.banhanga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import vn.binhnc.banhanga.R;
import vn.binhnc.banhanga.model.Item;
import vn.binhnc.banhanga.utils.GlideUtils;
import vn.binhnc.banhanga.utils.Utils;


public class ChitietAdapter extends RecyclerView.Adapter<ChitietAdapter.MyViewHolder> {
    List<Item> itemList;
    Context context;

    public ChitietAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.txtten.setText(item.getTensp() + "");
        holder.txtsoluong.setText("Số lượng: " + item.getSoluong() + "");
        if (item.getHinhanh().contains("http")) {
            GlideUtils.loadUrl(item.getHinhanh(), holder.imagechitiet);
        } else {
            GlideUtils.loadUrl(Utils.BASE_URL + "images/" + item.getHinhanh(), holder.imagechitiet);
        }
    }

    @Override
    public int getItemCount() {
        if (itemList != null) {
            return itemList.size();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtten, txtsoluong;
        ImageView imagechitiet;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagechitiet = itemView.findViewById(R.id.item_image_detail);
            txtten = itemView.findViewById(R.id.item_name_detail);
            txtsoluong = itemView.findViewById(R.id.item_number_detail);
        }
    }
}
