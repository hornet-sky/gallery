package com.example.paginggallery;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.DataSource;

public class GalleryDataSourceFactory extends DataSource.Factory<Integer, LoadPhotosResult.PhotoItem> {
    private Context context;
    private DataSource.InvalidatedCallback invalidatedCallback;
    public GalleryDataSourceFactory(Context context, DataSource.InvalidatedCallback invalidatedCallback) {
        this.context = context;
        this.invalidatedCallback = invalidatedCallback;
    }
    @NonNull
    @Override
    public DataSource<Integer, LoadPhotosResult.PhotoItem> create() {
        Log.w("myTag", "GalleryDataSourceFactory.create");
        return new GalleryDataSource(context, invalidatedCallback);
    }
}
