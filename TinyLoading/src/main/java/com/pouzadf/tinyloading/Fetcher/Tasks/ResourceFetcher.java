package com.pouzadf.tinyloading.Fetcher.Tasks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.util.Pair;

import com.pouzadf.tinyloading.Converter.Converter;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;



/**
 * Task that will be performed by {@link com.pouzadf.tinyloading.Fetcher.FutureFetcher}. Retrieves
 * Drawable from {@link ResourceFetcher#dRes}, converts it if required.
 * Use of generics to handle multiple data types as results.
 */
public class ResourceFetcher<T> implements Callable{

    private final String TAG = "Res Fetcher";

    private @DrawableRes int dRes;

    /*Using weak reference to prevent mem leaks if activity or fragment changes*/
    private WeakReference<Context> c;

    /*Represents container dimensions where data will be load.*/
    private Pair<Integer, Integer> dims;

    /* Data provided by users if data can't be fetched.*/
    @Nullable
    private T default_result;

    /**
     * Resources data are easiest to get as drawable.
     * Converter provides method to transform drawable in T type if needed.
     */
    @NonNull
    private Converter<T, Drawable> converter;

    public ResourceFetcher(@DrawableRes int dRes, @Nullable T default_result, WeakReference<Context> c,
                           @NonNull Converter<T, Drawable> converter, Pair<Integer, Integer> dims)
    {
        this.dRes = dRes;
        this.default_result = default_result;
        this.converter = converter;
        this.c = c;
        this.dims = dims;
    }


    /**
     * @return image fetched as T type. If an error occurred while fetching data and
     * {@link ResourceFetcher#default_result} has been set, it will be return otherwise
     * the callable will return null.
     */
    @Override
    public T call() throws Exception {
        Log.d(TAG, "has been called");
        T res = default_result;
        if(c.get() != null)
        {
            Drawable d = ContextCompat.getDrawable(c.get(), dRes);
            if(d != null)
            {
                res = converter.convert(d, c);
                Log.d(TAG, "drawable has been loaded successfully");
            }
        }
        return res;
    }


}
