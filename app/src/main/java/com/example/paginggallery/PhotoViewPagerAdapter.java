package com.example.paginggallery;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class PhotoViewPagerAdapter extends ListAdapter<LoadPhotosResult.PhotoItem, PhotoViewPagerAdapter.PhotoViewPagerViewHolder> {
    private ImageView downloadBtn;
    protected PhotoViewPagerAdapter(ImageView downloadBtn) {
        super(new DiffUtil.ItemCallback<LoadPhotosResult.PhotoItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull LoadPhotosResult.PhotoItem oldItem, @NonNull LoadPhotosResult.PhotoItem newItem) {
                return oldItem == newItem || oldItem.getId() == newItem.getId();
            }
            @Override
            public boolean areContentsTheSame(@NonNull LoadPhotosResult.PhotoItem oldItem, @NonNull LoadPhotosResult.PhotoItem newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.downloadBtn = downloadBtn;
    }

    @NonNull
    @Override
    public PhotoViewPagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_view_pager_cell, parent, false);
        return new PhotoViewPagerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoViewPagerViewHolder holder, int position) {
        String largeImageURL = getItem(position).getLargeImageURL();
        holder.shimmerLayout.setShimmerColor(0x55FFFFFF);
        holder.shimmerLayout.setShimmerAngle(0);
        holder.shimmerLayout.startShimmerAnimation();
        downloadBtn.setVisibility(View.INVISIBLE);
        Log.w("myTag", "PhotoViewPagerAdapter - onBindViewHolder");
        Glide.with(holder.itemView)
                .load(largeImageURL)
                .placeholder(R.drawable.ic_baseline_photo_24)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.w("myTag", "Glide - onLoadFailed - " + e.getMessage());
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.w("myTag", "Glide - onResourceReady");
                        holder.shimmerLayout.stopShimmerAnimation();
                        downloadBtn.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(holder.photoImageView);
        if(holder.photoImageView.getDrawable() instanceof BitmapDrawable) { // 可能有内存缓存或硬盘缓存，所以没走网络
            downloadBtn.setVisibility(View.VISIBLE);
        }
    }

    static class PhotoViewPagerViewHolder extends RecyclerView.ViewHolder {
        ShimmerLayout shimmerLayout;
        PhotoView photoImageView;
        public PhotoViewPagerViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            shimmerLayout = itemView.findViewById(R.id.shimmerLayout);
        }
    }
}
