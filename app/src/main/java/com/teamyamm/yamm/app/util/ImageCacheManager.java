package com.teamyamm.yamm.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.teamyamm.yamm.app.network.VolleyController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;

import retrofit.http.HEAD;

/**
 * Implementation of volley's ImageCache interface. This manager tracks the application image loader and cache.
 *
 * Volley recommends an L1 non-blocking cache which is the default MEMORY CacheType.
 * @author Trey Robinson
 *
 */
public class ImageCacheManager{

    public final static int MIN_CACHE_SIZE = 10 * 1024 * 1024;

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

    /**
     * @return
     * 		instance of the cache manager
     */
    public static ImageCacheManager getInstance(){
        if(mInstance == null) {
            mInstance = new ImageCacheManager();
        }
        return mInstance;
    }

    /*
    * Referenced for Bitmap Cache Memory Management
    * https://chris.banes.me/2011/12/28/android-bitmap-caching/
    * http://developer.android.com/training/displaying-bitmaps/manage-memory.html
    * See also overide method of entryRemoved in BitmapLruImageCacahe
    * */

//    private static Set<SoftReference<Bitmap>> usedBitmap;

   /* public static void addFromUsedBitmaps(Bitmap bitmap){
        usedBitmap.add(new SoftReference<Bitmap>(bitmap));
        Log.d("ImageCacheManager/addFromUsedBitmap", usedBitmap.size() + " Remaining. Bitmap Added");

    }


    public static void removeFromUsedBitmaps(String url){
        usedBitmap.remove(url);
        Log.d("ImageCacheManager/removeFromUsedBitmap",usedBitmap.size() + " Remaining. Bitmap Removed " + url);
    }

    public static boolean isInUsedBitmaps(String url){
        return usedBitmap.contains(url);
    }
    public static void removeFromUsedBitmaps(Bitmap bitmap){
        usedBitmap.remove(new SoftReference<Bitmap>(bitmap));
        Log.d("ImageCacheManager/removeFromUsedBitmap",usedBitmap.size() + " Remaining. Bitmap Removed ");
    }

    public static boolean isInUsedBitmaps(Bitmap bitmap){
        return usedBitmap.contains(new SoftReference<Bitmap>(bitmap));
    }*/

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

    public class BitmapLruImageCache extends LruCache<String, Bitmap> implements ImageCache{
        private int count = 0;

        public BitmapLruImageCache(int maxSize) {
            super(maxSize);

        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
                return value.getRowBytes() * value.getHeight();
            } else {
                return value.getByteCount();
            }
        }


        @Override
        protected void entryRemoved(boolean evicted, String key,
                                    Bitmap oldValue, Bitmap newValue) {
            Log.d("ImageCacheManager/entryRemoved", --count + ":Entry Removed" + key);
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
            Log.d("BitmapLruImageCache/putBitmap", count++ + ":Added item to Mem Cache ");
            put(url, bitmap);
        }

        private static final int COMPRESS_QUALITY = 70;

        public Bitmap getDecodedBitmap(Bitmap b){
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                b.compress(CompressFormat.JPEG, COMPRESS_QUALITY, out);
                return BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            }catch(OutOfMemoryError e){
                Log.e("ImageCacheManager/getDecodedBitmap","Out of Memory Error Caught");

                return b;
            }
        }
    }
}