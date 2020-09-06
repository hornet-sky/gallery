package com.example.paginggallery;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class PhotoViewPagerFragment extends Fragment {
    private static final int REQUEST_CODE_PHOTO_SAVE = 101;
    private List<LoadPhotosResult.PhotoItem> photoList;
    private int currentPosition;
    private PhotoViewPagerAdapter adapter;
    private ViewPager2 photoViewPager;
    private TextView pageInfoTextView;
    private ImageView downloadBtn;
    public PhotoViewPagerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_view_pager, container, false);
        photoViewPager = view.findViewById(R.id.photoViewPager);
        pageInfoTextView = view.findViewById(R.id.pageInfoTextView);
        downloadBtn = view.findViewById(R.id.downloadBtnImageView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GalleryViewModel vm = new ViewModelProvider(getActivity(),
                new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()))
                .get(GalleryViewModel.class);
        photoList = vm.getPhotoItemsLiveData().getValue();
        currentPosition = getArguments().getInt("currentPosition");
        adapter = new PhotoViewPagerAdapter(downloadBtn);
        adapter.submitList(photoList);
        photoViewPager.setAdapter(adapter);
        photoViewPager.setCurrentItem(currentPosition, false);
        // photoViewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL); // 上下滑动
        photoViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                pageInfoTextView.setText(position + 1 + " of " + photoList.size());
            }
        });
        pageInfoTextView.setText(currentPosition + 1 + " of " + photoList.size());

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("myTag", "SDK_INT " + Build.VERSION.SDK_INT);
                if(Build.VERSION.SDK_INT < 29
                        && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                    Log.w("myTag", "downloadBtn.onClick - 申请保存到外部存储设备的权限");
                    requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_CODE_PHOTO_SAVE);
                } else {
                    saveImage();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(REQUEST_CODE_PHOTO_SAVE == requestCode) {
            if(grantResults != null && grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                Toast.makeText(requireActivity(), "授权失败，无法保存图片", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveImage() {
        Log.w("myTag", "saveImage - currentPosition - " + currentPosition);
        PhotoView photoView = ((PhotoViewPagerAdapter.PhotoViewPagerViewHolder) ((RecyclerView) photoViewPager.getChildAt(0))
                .findViewHolderForAdapterPosition(currentPosition)).photoImageView;
        Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
        /* api 29 之前
        String r = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), bitmap, "", "");
        if(r != null) { // content://media/external/images/media/65
            Toast.makeText(requireContext(), "保存成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "保存失败", Toast.LENGTH_SHORT).show();
        }
         */

        // api 29 & 异步操作
        new AsyncDownloadImageTask(requireContext())
                .execute(bitmap);

    }

    static class AsyncDownloadImageTask extends AsyncTask<Bitmap, Void, Boolean> {
        private Context ctx;
        public AsyncDownloadImageTask(Context context) {
            this.ctx = context;
        }
        @Override
        protected Boolean doInBackground(Bitmap... args) {
            Bitmap bitmap = (Bitmap) args[0];
            Uri uri = ctx.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
            Log.w("myTag", "EXTERNAL_CONTENT_URI: " + MediaStore.Images.Media.EXTERNAL_CONTENT_URI); // content://media/external/images/media
            Log.w("myTag", "EXTERNAL_CONTENT_URI - uri: " + uri); // content://media/external/images/media/77
            if(uri == null) {
                return false;
            }
            OutputStream out = null;
            try {
                out = ctx.getContentResolver().openOutputStream(uri);
                return bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (IOException e) {
                Log.e("myTag", e.getMessage());
                return false;
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch(IOException e) {
                    Log.e("myTag", e.getMessage());
                }
            }
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(result) {
                Toast.makeText(ctx, "保存成功！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ctx, "保存失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}