package com.georide.georide;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyNetworking {
    private static VolleyNetworking myInstance;
    private static RequestQueue myRequestQueue;
    private static Context myContext;

    private VolleyNetworking(Context context) {
        myContext = context;
        myRequestQueue = getRequestQueue();
    }

    public static synchronized VolleyNetworking getInstance(Context context) {
        if (myInstance == null) {
            myInstance = new VolleyNetworking(context);
        }
        return myInstance;
    }

    public RequestQueue getRequestQueue() {
        if (myRequestQueue == null) {
            myRequestQueue = Volley.newRequestQueue(myContext.getApplicationContext());
        }
        return myRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
