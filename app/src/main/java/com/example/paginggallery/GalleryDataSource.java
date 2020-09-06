package com.example.paginggallery;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

public class GalleryDataSource extends PageKeyedDataSource<Integer, LoadPhotosResult.PhotoItem> {
    private static final String baseUrl = "https://pixabay.com/api/?key=18091106-22deb961167d59b82212b023a&image_type=photo&pretty=true&q=";
    private static final String[] keys = {"smile", "fruit", "cat", "dog", "beauty"};
    private static int keySeed = 0;
    private static MutableLiveData<LoadDataStatus> currentLoadDataStatusLiveData = new MutableLiveData<>();
    private Context context;
    private String currKey;
    private int pageSize = 200;
    private int totalPage;
    private RetryStatus retryStatus = RetryStatus.NONE;
    private LoadInitialParams<Integer> initialParams;
    private LoadInitialCallback<Integer, LoadPhotosResult.PhotoItem> initialCallback;
    private LoadParams<Integer> params;
    private LoadCallback<Integer, LoadPhotosResult.PhotoItem> callback;

    public GalleryDataSource(Context context, InvalidatedCallback invalidatedCallback) {
        this.context = context;
        if(invalidatedCallback != null) {
            this.addInvalidatedCallback(invalidatedCallback);
        }
    }
    public static LiveData<LoadDataStatus> getCurrentLoadDataStatusLiveData() {
        return currentLoadDataStatusLiveData;
    }

    public void retry() {
        Log.w("myTag", "retry - " + retryStatus);
        if(retryStatus == RetryStatus.LOAD_INITIAL) {
            loadInitial(initialParams, initialCallback);
        } else if(retryStatus == RetryStatus.LOAD_AFTER) {
            loadAfter(params, callback);
        }
        retryStatus = RetryStatus.NONE;
    }

    @Override
    public void loadInitial(@NonNull final LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, LoadPhotosResult.PhotoItem> callback) {
        currKey = keys[keySeed++ % keys.length];
        Log.w("myTag", "loadInitial - page " + 1 + " - currentLoadDataStatus " + currentLoadDataStatusLiveData);
        String url = baseUrl + currKey + "&page=1&per_page=" + pageSize;
        StringRequest req = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LoadPhotosResult result = new Gson().fromJson(response, LoadPhotosResult.class);
                        totalPage = (int) Math.ceil(result.getTotalHits() * 1.0 / pageSize);
                        callback.onResult(result.getHits(), null, 2);
                        currentLoadDataStatusLiveData.postValue(LoadDataStatus.COMPLETED);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("myTag", "Response.ErrorListener.onErrorResponse - " + error.getMessage());
                        currentLoadDataStatusLiveData.postValue(LoadDataStatus.NETWORK_ERROR);
                        retryStatus = RetryStatus.LOAD_INITIAL;
                        initialParams = params;
                        initialCallback = callback;
                    }
                }
        );
        VolleySingleton.getInstance(context).getRequestQueue().add(req);
    }

    @Override
    public void loadAfter(@NonNull final LoadParams<Integer> params, @NonNull final LoadCallback<Integer, LoadPhotosResult.PhotoItem> callback) {
        final int page = params.key;
        Log.w("myTag", "loadAfter - page " + page + " - currentLoadDataStatus " + currentLoadDataStatusLiveData);
        if(page == totalPage) {
            currentLoadDataStatusLiveData.postValue(LoadDataStatus.NO_MORE_DATA);
            return;
        }
        currentLoadDataStatusLiveData.postValue(LoadDataStatus.LOADING);
        String url = baseUrl + currKey + "&page=" + page + "&per_page=" + pageSize;
        StringRequest req = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LoadPhotosResult result = new Gson().fromJson(response, LoadPhotosResult.class);
                        callback.onResult(result.getHits(),  page + 1);
                        currentLoadDataStatusLiveData.postValue(LoadDataStatus.COMPLETED);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("myTag", "Response.ErrorListener.onErrorResponse - " + error.getMessage());
                        currentLoadDataStatusLiveData.postValue(LoadDataStatus.NETWORK_ERROR);
                        retryStatus = RetryStatus.LOAD_AFTER;
                        GalleryDataSource.this.params = params;
                        GalleryDataSource.this.callback = callback;
                    }
                }
        );
        VolleySingleton.getInstance(context).getRequestQueue().add(req);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, LoadPhotosResult.PhotoItem> callback) {
    }

    static enum LoadDataStatus {
        LOADING, COMPLETED, NETWORK_ERROR, NO_MORE_DATA
    }

    static enum RetryStatus {
        NONE, LOAD_INITIAL, LOAD_AFTER
    }
}
