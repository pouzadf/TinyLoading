package com.pouzadf.tinyloading;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import android.util.LruCache;
import android.widget.ImageView;

import com.pouzadf.tinyloading.Utils.MainThreadExecutor;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * This class implements the singleton pattern. It contains the main objects of the library such as
 * the executors which deal with threads logic or the caches which  enhance
 * performance.
 */

public class TinyLoading {

    /* Singleton reference */
    private static TinyLoading mInstance;

    /* Cache to read from , Use of Lru algorithm to evict elements when the cache is full.
    * Max size set to 1/8 of the current memory available when singleton is init. More infos
     * https://developer.android.com/topic/performance/graphics/cache-bitmap*/
    private static LruCache<String, Bitmap> mCache;

    /* Thread Pool used to perform fetching task*/
    private static ExecutorService tasksExecutor;

    /*HashMap that store all requests being performed to allow user to interrupt
    * them if needed. Each request will be removed when complete. Request are mapped to their target
    * Imageview, stored as Weakreference to prevent mem leaks. Synchronized with Collections.synchronizedMap.
    */
    private static Map<WeakReference<ImageView>, FutureTask> requests;

    /*Will communicate with the UI thread and sets the images in their containers*/
    private static MainThreadExecutor mainThreadExecutor;

    public static ExecutorService getTasksExecutor() {
        return tasksExecutor;
    }

    /**
     * Private constructor to implement singleton pattern. Define bitmap cache size */
    private TinyLoading()
    {
        tasksExecutor = Executors.newFixedThreadPool(5);
        mainThreadExecutor = new MainThreadExecutor();
        requests = Collections.
                synchronizedMap(new HashMap<WeakReference<ImageView>, FutureTask>());

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static MainThreadExecutor getMainThreadExecutor() {
        return mainThreadExecutor;
    }


    /**
     * Init singleton when it is called for the first time, returns his reference otherwise.
     * @return current instance of the singleton
     */
    public static TinyLoading get() {
        if(mInstance == null)
            mInstance = new TinyLoading();
        return mInstance;
    }

    public LruCache<String, Bitmap> getCache() {
        return mCache;
    }


    /**
     * @param c Caller context
     */
    public RequestBuilder with(@NonNull Context c)
    {
        return new RequestBuilder(c);
    }


    /**
     * This method will release resource and stop tasks safely. Beware that it will clean the caches.
     */
    public static void shutDown()
    {
        /* Using shutDownNow instead of shutdown to stop current task processing */
        tasksExecutor.shutdownNow();
        mCache.evictAll();
        requests.clear();
    }

    /**
     * This method adds Bitmap in the cache if it has not been added yet
     * @param key Key identifying bitmap in the map.
     * @param bitmap value to add in the map
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mCache.put(key, bitmap);
        }
    }

    /**
     *
     * @param key  Key identifying bitmap in the map.
     * @return Bitmap value if the key has been found, null otherwise.
     */
    public Bitmap getBitmapFromMemCache(String key) {
        return mCache.get(key);
    }

    /**
     *
     * @param task Task value to add in the map.
     * @param ref key identifying task in the map Use of WeakReference to prevent memleaks.
     */
    public static void addRequest(FutureTask task, WeakReference<ImageView> ref)
    {
        requests.put(ref, task);
    }

    /**
     * Returns
     * @param ref key identifying task value in the map.
     */
    public static void removeRequest(WeakReference<ImageView> ref)
    {
        requests.remove(ref);
    }
    /**
     * Synchronized iteration on the map, returns task mapped to v WeakReference if found.
     * @param v Imageview that might be stored in a WeakReference in the map.
     */
    @SuppressWarnings("unchecked")
    private static FutureTask getRequest(ImageView v)
    {
        Set set = requests.entrySet();

        /*using a synchronized block to prevent race issues while iterating on hashmap.
        * There can be only one instance of hashmap as it is initialized
        * in the private constructor of singleton called only once */
        synchronized (requests)
        {
            Iterator iterator = set.iterator();
            while(iterator.hasNext())
            {
                Map.Entry curr =  (Map.Entry) iterator.next();
                if( ((WeakReference<ImageView>) curr.getKey()).get() == v)
                    return (FutureTask) curr.getValue();
            }
        }
        return null;
    }

    /**
     * This method cancel any request that targets view.
     * @param view Request target
     */
    public void cancel(ImageView view)
    {

        FutureTask task = getRequest(view);
        if(task != null)
            task.cancel(true);
    }

}
