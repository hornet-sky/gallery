package com.example.paginggallery;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class GalleryFragment extends Fragment {
    private View createdView;
    private GalleryViewModel vm;
    private GalleryRecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private ImageView scrollToTopBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    public GalleryFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(createdView != null) {
            return createdView;
        }
        createdView = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = createdView.findViewById(R.id.recyclerView);
        scrollToTopBtn = createdView.findViewById(R.id.scrollToTopBtn);
        scrollToTopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(0);
            }
        });
        adapter = new GalleryRecyclerViewAdapter();
        adapter.setProgressTextViewClickListener(new GalleryRecyclerViewAdapter.ProgressTextViewClickListener() {
            @Override
            public void onClick(View view) {
                vm.retry();
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new MyStaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        swipeRefreshLayout = createdView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                vm.refreshPhotoItems();
            }
        });
        return createdView;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vm = new ViewModelProvider(getActivity(),
                new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()))
            .get(GalleryViewModel.class);
        vm.setRefreshPhotoItemsListener(new GalleryViewModel.RefreshPhotoItemsListener() {
            @Override
            public void done() {
                if(swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    Log.w("myTag", "GalleryViewModel.RefreshPhotoItemsListener.done");
                    swipeRefreshLayout.setRefreshing(false);
                    recyclerView.scrollToPosition(0);
                }
            }
        });
        vm.getPhotoItemsLiveData().observe(getViewLifecycleOwner(), new Observer<PagedList<LoadPhotosResult.PhotoItem>>() {
            @Override
            public void onChanged(PagedList<LoadPhotosResult.PhotoItem> photoItems) {
                Log.w("myTag", "photoItemsLiveData.observe - onChanged - itemSize - " + photoItems.size());
                adapter.submitList(photoItems);
            }
        });

        vm.getCurrentLoadDataStatusLiveData().observe(getViewLifecycleOwner(), new Observer<GalleryDataSource.LoadDataStatus>() {
            @Override
            public void onChanged(GalleryDataSource.LoadDataStatus loadDataStatus) {
                Log.w("myTag", "currentLoadDataStatusLiveData.observe - onChanged - loadDataStatus - " + loadDataStatus);
                switch (loadDataStatus) {
                    case LOADING:
                        adapter.loading();
                        break;
                    case COMPLETED:
                        adapter.hideProgress();
                        break;
                    case NO_MORE_DATA:
                        adapter.noMoreData();
                        break;
                    case NETWORK_ERROR:
                        adapter.networkError();
                        break;
                    default:;
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.refreshBtn) {
            swipeRefreshLayout.setRefreshing(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    vm.refreshPhotoItems();
                }
            }, 1000);
        }
        return true;
    }
}