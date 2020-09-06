package com.example.paginggallery;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

public class GalleryViewModel extends AndroidViewModel {
    private LiveData<PagedList<LoadPhotosResult.PhotoItem>> photoItemsLiveData;
    private LiveData<GalleryDataSource.LoadDataStatus> currentLoadDataStatusLiveData;
    private RefreshPhotoItemsListener refreshPhotoItemsListener;

    public GalleryViewModel(@NonNull Application application) {
        super(application);
        DataSource.Factory factory = new GalleryDataSourceFactory(application, new DataSource.InvalidatedCallback() {
            @Override
            public void onInvalidated() {
                Log.w("myTag", "DataSource.InvalidatedCallback.onInvalidated");
                if(refreshPhotoItemsListener != null) {
                    refreshPhotoItemsListener.done();
                }
            }
        });
        currentLoadDataStatusLiveData = GalleryDataSource.getCurrentLoadDataStatusLiveData();
        photoItemsLiveData = new LivePagedListBuilder(factory, 1).build();
    }
    public LiveData<PagedList<LoadPhotosResult.PhotoItem>> getPhotoItemsLiveData() {
        return photoItemsLiveData;
    }

    public LiveData<GalleryDataSource.LoadDataStatus> getCurrentLoadDataStatusLiveData() {
        return currentLoadDataStatusLiveData;
    }

    public void setRefreshPhotoItemsListener(RefreshPhotoItemsListener refreshPhotoItemsListener) {
        this.refreshPhotoItemsListener = refreshPhotoItemsListener;
    }

    public void refreshPhotoItems() {
        photoItemsLiveData.getValue().getDataSource().invalidate();
    }

    public void retry() {
        ((GalleryDataSource) photoItemsLiveData.getValue().getDataSource()).retry();
    }

    static interface RefreshPhotoItemsListener {
        void done();
    }
}
