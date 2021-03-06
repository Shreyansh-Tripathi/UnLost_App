package com.example.unlost.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlost.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class Lost_adapter extends RecyclerView.Adapter<Lost_adapter.ViewHolder>
{
    private ArrayList<Product> products;
    ItemClicked activity;

    LongItemClicked activity2;

    public interface ItemClicked{
        void onItemClicked(int index);
    }

    public interface LongItemClicked{
         void onLongItemClicked(int index, View view);
    }

    public Lost_adapter(ArrayList<Product> products, ItemClicked activity, LongItemClicked activity2) {
        this.products = products;
        this.activity = activity;
        this.activity2 = activity2;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView ivProduct;
        TextView tvCategory,tvSubCategory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct=itemView.findViewById(R.id.ivProduct);
            tvCategory=itemView.findViewById(R.id.tvCategory);
            tvSubCategory=itemView.findViewById(R.id.tvSubCategory);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onItemClicked(products.indexOf((Product) v.getTag()));
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                      activity2.onLongItemClicked(products.indexOf((Product) v.getTag()), v);
                    return true;
                }
            });
        }
    }
    @NonNull
    @Override
    public Lost_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Lost_adapter.ViewHolder holder, int position) {
        holder.itemView.setTag(products.get(position));
        holder.tvCategory.setText(products.get(position).getCategory());
        holder.tvSubCategory.setText(products.get(position).getSubcategory());
        Picasso.with((Context) activity).load(products.get(position).getUrl())
                .resize(75,75).centerInside().into(holder.ivProduct);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void filterList(ArrayList<Product> filterList){
        products=filterList;
        notifyDataSetChanged();
    }
}
