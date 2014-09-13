package com.teamyamm.yamm.app;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by parkjiho on 9/13/14.
 */
public class VolleyController{
    public static final String TAG = VolleyController.class
            .getSimpleName();

    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;
    private static Context context;

    public static void setVolleyController(Context c){
        Log.d("VolleyController/setVolleyController","Controller Set with Context");

        if (context==null) {
            context = c;
            Log.d("VolleyController/setVolleyController","New Context");
        }
        else{
            Log.d("VolleyController/setVolleyController","Going with Old Context");
        }
    }

    public static void clearRequestQueue(){
        mRequestQueue = null;
        mImageLoader = null;
    }


    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            if (context==null){
                Log.e("VolleyController/getRequestQueue","Should set Controller First");
                return null;
            }
            Log.d("VolleyController/getRequestQueue","New RequestQueue Created");
            mRequestQueue = Volley.newRequestQueue(context);
        }

        return mRequestQueue;
    }

    public static ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(mRequestQueue,
                    new LruBitmapCache());
            Log.d("VolleyController/getImageLoader","New Image Loader Created");

        }
        return mImageLoader;
    }

    public static <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public static  <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public static void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
