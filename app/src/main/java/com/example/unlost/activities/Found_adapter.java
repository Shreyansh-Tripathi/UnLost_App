package com.example.unlost.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlost.R;

import java.util.ArrayList;

public class Found_adapter extends RecyclerView.Adapter<Found_adapter.ViewHolder>
{


    private ArrayList<Product> products;
    ItemClicked activity;

    public interface ItemClicked{
        void onItemClicked(int index);
    }
    public Found_adapter(Context context, ArrayList<Product> list)
    {
        products=list;
        activity=(ItemClicked)context;
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
        }
    }
    @NonNull
    @Override
    public Found_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Found_adapter.ViewHolder holder, int position) {
        holder.itemView.setTag(products.get(position));
        holder.tvCategory.setText(products.get(position).getCategory());
        holder.tvSubCategory.setText(products.get(position).getSubcategory());
        holder.ivProduct.setImageResource(R.drawable.ic_baseline_search_24);

    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
