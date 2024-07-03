package vn.binhnc.manager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.binhnc.manager.R;
import vn.binhnc.manager.activity.ChatActivity;
import vn.binhnc.manager.listener.ItemClickListener;
import vn.binhnc.manager.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    List<User> userList;
    Context context;

    public UserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent,
                false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = userList.get(position);
        String user_id = user.getId() + "";
        holder.txtid.setText(user_id);
        holder.txtuser.setText(user.getUsername());
        holder.setItemClickListener((view, pos, isLongClick) -> {
            if (!isLongClick) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("id", user.getId());
                intent.putExtra("username", user.getUsername());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtid, txtuser;
        ItemClickListener itemClickListener;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtid = itemView.findViewById(R.id.iduser);
            txtuser = itemView.findViewById(R.id.username);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getBindingAdapterPosition(), false);
        }
    }
}