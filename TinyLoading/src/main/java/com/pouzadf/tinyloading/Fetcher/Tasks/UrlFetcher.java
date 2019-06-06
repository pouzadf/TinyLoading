package com.pouzadf.tinyloading.Fetcher.Tasks;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.pouzadf.tinyloading.Converter.Converter;
import com.pouzadf.tinyloading.Utils.BitmapLoader;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.Callable;


/**
 * Task that will be performed by {@link com.pouzadf.tinyloading.Fetcher.FutureFetcher}. Retrieves
 * Drawable from {@link UrlFetcher#source}, converts it if required.
 * Use of generics to handle multiple data types as results.
 */
public class UrlFetcher<T> implements Callable<T>
{

    private final String TAG = "UrlFetcher";

    private String source;
    /*Using weak reference to prevent mem leaks if activity or fragment changes*/
    private WeakReference<Context> c;
    @Nullable
    private T default_result;

    /*User may need to send specific headers to connect to his API*/
    private Map<String, String> headers;

    /**
     * Most of the time Bitmap will be used as result but it mights be cases where we looked for other
     * results type. Converter provides method to transform bitmap in T type.
     */
    @NonNull
    private Converter<T, Bitmap> converter;

    /*Represents container dimensions where data will be load.*/
    @NonNull private Pair<Integer, Integer> dims;

    public UrlFetcher(String src, @Nullable T default_result, @NonNull WeakReference<Context> c,
                      @NonNull Converter<T,Bitmap> converter, @NonNull Pair<Integer, Integer> dims,
                      @Nullable Map<String, String> headers)
    {
        this.source = src;
        this.default_result = default_result;
        this.converter = converter;
        this.c = c;
        this.dims = dims;
    }


    /**
     * @return image fetched as T type. If an error occurred while fetching data and
     * {@link UrlFetcher#default_result} has been set, it will be return otherwise
     * the callable will return null.
     */
    @Override
    public T call() throws Exception {
        T res = default_result;
        if(c.get() != null)
        {
            try {
                /* Might return null if an error occurs, will throw IO exception if stream error occurs*/
                Bitmap bm = BitmapLoader.decodeSampledBitmapFromURL(source,
                        dims.first, dims.second, headers);
                if(bm != null)
                    res = converter.convert(bm, c);
            } catch (IOException e) {
                Log.e(TAG, "An error occurred while fetching data from: " + source +
                        "  error: " + e.getMessage());
            }
        }
        return res;
    }
}
