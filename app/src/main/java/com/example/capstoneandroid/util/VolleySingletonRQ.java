package com.example.capstoneandroid.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingletonRQ {
    private static VolleySingletonRQ instance;
    private RequestQueue requestQueue;
    private static Context context;

    private VolleySingletonRQ(Context context){
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleySingletonRQ getInstance(Context context){
        if(instance == null){
            instance = new VolleySingletonRQ(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}
