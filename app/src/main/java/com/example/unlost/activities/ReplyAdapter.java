package com.example.unlost.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.unlost.R;
import java.util.ArrayList;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    ArrayList<Reply> replyList;
    ItemClick activity;

    public interface ItemClick{
        void onClick(int index);
    }

    public ReplyAdapter(ArrayList<Reply> replyList, Context context) {
        this.replyList = replyList;
        this.activity=(ItemClick) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName, userReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName= itemView.findViewById(R.id.tvuserName);
            userReply=itemView.findViewById(R.id.tvreply);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onClick(replyList.indexOf((Reply)v.getTag()));
                }
            });
        }
    }

    @NonNull
    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(replyList.get(position));
        holder.userName.setText(replyList.get(position).getUserName());
        holder.userReply.setText(replyList.get(position).getUserAnswer());
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }
}