package com.example.paginggallery;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public final class VolleySingleton {
    private Context context;
    private RequestQueue requestQueue;
    private static VolleySingleton instance;
    private VolleySingleton(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }
    public RequestQueue getRequestQueue() {
        return this.requestQueue;
    }
    public static VolleySingleton getInstance(Context context) {
        if(instance == null) {
            synchronized (VolleySingleton.class) {
                if(instance == null) {
                    instance = new VolleySingleton(context);
                }
            }
        }
        return instance;
    }
}
