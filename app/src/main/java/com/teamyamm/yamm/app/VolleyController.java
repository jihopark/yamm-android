package com.teamyamm.yamm.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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

    private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;
    private static int DISK_IMAGECACHE_QUALITY = 100;  //PNG is lossless so quality is ignored but must be provided


    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;
    private static Context context;

    public static void setVolleyController(Context c){
        Log.d("VolleyController/setVolleyController","Controller Set with Context");

        if (context==null) {
            context = c;
            Log.d("VolleyController/setVolleyController", "New Context");
        }
        else{
            Log.d("VolleyController/setVolleyController","Going with Old Context");
        }
    }

    private static void createImageCache(){
        Log.d("VolleyController/createImageCache","Image Cache Size(MB)" + getCacheSize(context) / 1024 / 1024);
        long free = Runtime.getRuntime().maxMemory() - (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        Log.d("VolleyController/createImageCache","Total Free Memory Size(MB)" + (free / 1024 / 1024));

        WTFExceptionHandler.sendLogToServer(context, "Image Cache Size(MB) " + getCacheSize(context) / 1024 / 1024
                                                       + "\n" + "Total Free Memory Size(MB)" + (free / 1024 / 1024));

        ImageCacheManager.getInstance().init(context,
                BaseActivity.packageName
                , getCacheSize(context)
                , DISK_IMAGECACHE_COMPRESS_FORMAT
                , DISK_IMAGECACHE_QUALITY
                , ImageCacheManager.CacheType.MEMORY);
        Log.d("VolleyController/createImageCache","Image Cache Created");
    }

    public static int getCacheSize(Context ctx) {
        final DisplayMetrics displayMetrics = ctx.getResources().
                getDisplayMetrics();
        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;
        // 4 bytes per pixel
        final int screenBytes = screenWidth * screenHeight * 4;

        return screenBytes * 3;
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
            createImageCache();
        }

        return mRequestQueue;
    }

    public static ImageLoader getImageLoader() {
        getRequestQueue();

        return ImageCacheManager.getInstance().getImageLoader();
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
