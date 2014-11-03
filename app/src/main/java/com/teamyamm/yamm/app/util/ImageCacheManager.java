package com.teamyamm.yamm.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.teamyamm.yamm.app.network.VolleyController;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of volley's ImageCache interface. This manager tracks the application image loader and cache.
 *
 * Volley recommends an L1 non-blocking cache which is the default MEMORY CacheType.
 * @author Trey Robinson
 *
 */
public class ImageCacheManager{

    /**
     * Volley recommends in-memory L1 cache but both a disk and memory cache are provided.
     * Volley includes a L2 disk cache out of the box but you can technically use a disk cache as an L1 cache provided
     * you can live with potential i/o blocking.
     *
     */
    public enum CacheType {
        DISK
        , MEMORY
    }

    private static ImageCacheManager mInstance;

    /**
     * Volley image loader
     */
    private ImageLoader mImageLoader;

    /**
     * Image cache implementation
     */
    private ImageCache mImageCache;

    private static HashSet<String> usedBitmap;

    /**
     * @return
     * 		instance of the cache manager
     */
    public static ImageCacheManager getInstance(){
        if(mInstance == null) {
            mInstance = new ImageCacheManager();
            usedBitmap = new HashSet<String>();
            Log.d("ImageCacheManager/getInstance","New Instance Created. Used Bitmap Set Initialized");
        }
        return mInstance;
    }

    public static void addFromUsedBitmaps(String url){
        usedBitmap.add(url);
        Log.d("ImageCacheManager/addFromUsedBitmap",usedBitmap.size() + " Remaining. Bitmap Added" + url);

    }


    public static void removeFromUsedBitmaps(String url){
        usedBitmap.remove(url);
        Log.d("ImageCacheManager/removeFromUsedBitmap",usedBitmap.size() + " Remaining. Bitmap Removed " + url);
    }

    public static boolean isInUsedBitmaps(String url){
        return usedBitmap.contains(url);
    }

    /**
     * Initializer for the manager. Must be called prior to use.
     *
     * @param context
     * 			application context
     * @param uniqueName
     * 			name for the cache location
     * @param cacheSize
     * 			max size for the cache
     * @param compressFormat
     * 			file type compression format.
     * @param quality
     */
    public void init(Context context, String uniqueName, int cacheSize, CompressFormat compressFormat, int quality, CacheType type){
        switch (type) {
            case DISK:
                mImageCache= new DiskLruImageCache(context, uniqueName, cacheSize, compressFormat, quality);
                break;
            case MEMORY:
                mImageCache = new BitmapLruImageCache(cacheSize);
            default:
                mImageCache = new BitmapLruImageCache(cacheSize);
                break;
        }

        mImageLoader = new ImageLoader(VolleyController.getRequestQueue(), mImageCache);

        if (android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.HONEYCOMB) {
            mReusableBitmaps =
                    Collections.synchronizedSet(new HashSet<SoftReference<Bitmap>>());
        }

    }

    public ImageCache getImageCache(){
        return mImageCache;
    }

    public Bitmap getBitmap(String url) {
        try {
            return mImageCache.getBitmap(createKey(url));
        } catch (NullPointerException e) {
            throw new IllegalStateException("Disk Cache Not initialized");
        }
    }

    public void putBitmap(String url, Bitmap bitmap) {
        try {
            mImageCache.putBitmap(createKey(url), bitmap);
        } catch (NullPointerException e) {
            throw new IllegalStateException("Disk Cache Not initialized");
        }
    }


    /**
     * 	Executes and image load
     * @param url
     * 		location of image
     * @param listener
     * 		Listener for completion
     */
    public void getImage(String url, ImageListener listener){
        mImageLoader.get(url, listener);
    }

    /**
     * @return
     * 		instance of the image loader
     */
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * Creates a unique cache key based on a url value
     * @param url
     * 		url to be used in key creation
     * @return
     * 		cache key value
     */
    private String createKey(String url){
        return String.valueOf(url.hashCode());
    }

    /**
     * Basic LRU Memory cache.
     *
     * @author Trey Robinson
     *
     */

    Set<SoftReference<Bitmap>> mReusableBitmaps;


    public class BitmapLruImageCache extends LruCache<String, Bitmap> implements ImageCache{

        private final String TAG = this.getClass().getSimpleName();

        public BitmapLruImageCache(int maxSize) {
            super(maxSize);
        }

        @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
        }

        @Override
        protected void entryRemoved(boolean evicted, String key,
                                    Bitmap oldValue, Bitmap newValue) {
            if (!ImageCacheManager.isInUsedBitmaps(key)){
                Log.d("ImageCacheManager/entryRemoved","Bitmap Not in Use. Recycle! " + key);
                if (!oldValue.isRecycled()) {
                    oldValue.recycle();
                    Log.d("ImageCacheManager/entryRemoved","Recycled!");
                }
            }
        }

        @Override
        public Bitmap getBitmap(String url) {
            if (get(url)==null)
                Log.d("BitmapLruImageCache/getBitmap", "Does not exist in Cache " + url);
            else
                Log.d("BitmapLruImageCache/getBitmap", "Retrieved item from Mem Cache " + url);
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            Log.d("BitmapLruImageCache/putBitmap", "Added item to Mem Cache");
            put(url, bitmap);
        }
    }




}