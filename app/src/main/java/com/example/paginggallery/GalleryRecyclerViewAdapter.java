package com.example.paginggallery;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class GalleryRecyclerViewAdapter extends PagedListAdapter<LoadPhotosResult.PhotoItem, GalleryRecyclerViewAdapter.GalleryViewHolder> {
    private ProgressBar progressBar;
    private TextView progressTextView;
    private ProgressTextViewClickListener progressTextViewClickListener;
    protected GalleryRecyclerViewAdapter() {
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
    }

    public void setProgressTextViewClickListener(ProgressTextViewClickListener progressTextViewClickListener) {
        this.progressTextViewClickListener = progressTextViewClickListener;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() == 0 ? 0 : super.getItemCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == getItemCount() - 1 ? R.layout.gallery_load_status_cell : R.layout.gallery_cell;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == R.layout.gallery_load_status_cell) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_load_status_cell, parent, false);
            ((StaggeredGridLayoutManager.LayoutParams) itemView.getLayoutParams()).setFullSpan(true);
            return new GalleryViewHolder(itemView);
        }
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_cell, parent, false);
        final GalleryViewHolder holder = new GalleryViewHolder(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("myTag", "itemView - onClick - " + view.getClass());
                NavController navController = Navigation.findNavController(view);
                Bundle bundle = new Bundle();
                bundle.putInt("currentPosition", holder.getAdapterPosition());
                navController.navigate(R.id.action_galleryFragment_to_photoFragment, bundle);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {
        if(position == getItemCount() - 1) {
            progressBar = holder.progressBar;
            progressTextView = holder.progressTextView;
            progressTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.w("myTag", "progressTextView.onClick - " + progressTextViewClickListener);
                    if(progressTextViewClickListener != null) {
                        progressTextViewClickListener.onClick(view);
                    }
                }
            });
            return;
        }
        LoadPhotosResult.PhotoItem item = getItem(position);
        holder.imageView.getLayoutParams().height = item.getWebformatHeight();
        holder.userTextView.setText(item.getUser());
        holder.likesTextView.setText(String.valueOf(item.getLikes()));
        holder.favoritesTextView.setText(String.valueOf(item.getFavorites()));
        holder.downloadsTextView.setText(String.valueOf(item.getDownloads()));
        // 闪烁
        holder.shimmerLayout.setShimmerColor(0x55FFFFFF);
        holder.shimmerLayout.setShimmerAngle(0);
        holder.shimmerLayout.startShimmerAnimation();

        // 加载图片
        Glide.with(holder.itemView)
            .load(item.getWebformatURL())
            .placeholder(R.drawable.ic_baseline_photo_24)
            .listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.e("myTag", "Glide.listener - onLoadFailed " + e.getMessage());
                    return false;
                }
                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.shimmerLayout.stopShimmerAnimation(); // 需要判断一下 shimmerLayout是否为null
                    return false;
                }
            })
            .into(holder.imageView);
    }

    public void hideProgress() {
        if(progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        if(progressTextView != null) {
            progressTextView.setVisibility(View.INVISIBLE);
        }
    }

    public void loading() {
        if(progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if(progressTextView != null) {
            progressTextView.setText("加载中");
            progressTextView.setClickable(false);
            progressTextView.setVisibility(View.VISIBLE);
        }
    }

    public void noMoreData() {
        Log.w("myTag", "noMoreData - " + progressBar + " - " + progressTextView);

        if(progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        if(progressTextView != null) {
            progressTextView.setText("全部加载完毕");
            progressTextView.setClickable(false);
            progressTextView.setVisibility(View.VISIBLE);
        }
    }

    public void networkError() {
        if(progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
        if(progressTextView != null) {
            progressTextView.setText("网络异常，点击重试");
            progressTextView.setClickable(true);
            progressTextView.setVisibility(View.VISIBLE);
        }
    }

    static interface ProgressTextViewClickListener {
        public void onClick(View view);
    }

    static class GalleryViewHolder extends  RecyclerView.ViewHolder {
        ShimmerLayout shimmerLayout;
        ImageView imageView;
        TextView userTextView;
        ImageView likesImageView;
        TextView likesTextView;
        ImageView favoritesImageView;
        TextView favoritesTextView;
        ImageView downloadsImageView;
        TextView downloadsTextView;

        ProgressBar progressBar;
        TextView progressTextView;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.shimmerLayout = itemView.findViewById(R.id.shimmerLayout);
            this.imageView = itemView.findViewById(R.id.photoImageView);
            this.userTextView = itemView.findViewById(R.id.userTextView);
            this.likesImageView = itemView.findViewById(R.id.likesImageView);
            this.likesTextView = itemView.findViewById(R.id.likesTextView);
            this.favoritesImageView = itemView.findViewById(R.id.favoritesImageView);
            this.favoritesTextView = itemView.findViewById(R.id.favoritesTextView);
            this.downloadsImageView = itemView.findViewById(R.id.downloadsImageView);
            this.downloadsTextView = itemView.findViewById(R.id.downloadsTextView);

            this.progressBar = itemView.findViewById(R.id.progressBar);
            this.progressTextView = itemView.findViewById(R.id.progressTextView);
        }
    }
}
