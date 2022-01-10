package com.example.netflex.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.netflex.MainScreens.MovieDetails;
import com.example.netflex.Model.CategoryItemList;
import com.example.netflex.R;

import java.util.ArrayList;
import java.util.List;

public class ItemRecyclerAdapter extends RecyclerView.Adapter<ItemRecyclerAdapter.ItemViewHolder> {
    Context context;
    List<CategoryItemList> categoryItemList;
    List<CategoryItemList> allMoviesList;

    public ItemRecyclerAdapter(Context context, List<CategoryItemList> categoryItemList) {
        this.context = context;
        this.categoryItemList = categoryItemList;
        this.allMoviesList = new ArrayList<>(categoryItemList);
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.category_row_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        Glide.with(context).load(categoryItemList.get(position).getImageUrl()).into(holder.itemImage);

        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MovieDetails.class);

                i.putExtra("movieId", categoryItemList.get(position).getId());
                i.putExtra("movieName", categoryItemList.get(position).getMovieName());
                i.putExtra("movieImageUrl", categoryItemList.get(position).getImageUrl());
                i.putExtra("movieFile", categoryItemList.get(position).getFileUrl());

                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryItemList.size();
    }

    public static final class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
        }
    }
}
