package com.pouzadf.tinyloading.Fetcher.Tasks;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Pair;

import com.pouzadf.tinyloading.Converter.Converter;
import com.pouzadf.tinyloading.Utils.BitmapLoader;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

/**
 * Task that will be performed by {@link com.pouzadf.tinyloading.Fetcher.FutureFetcher}. Retrieves
 * Bitmap from file path, converts it if required.
 * Use of generics to handle multiple data types as results.
 */
public class FileFetcher<T> implements Callable {

    private String source;
    /*Using weak reference to prevent mem leaks if activity or fragment changes*/
    private WeakReference<Context> c;
    @Nullable private T default_result;

    /*Represents container dimensions where data will be load.*/
    private Pair<Integer, Integer> dims;

    /**
    * Most of the time Bitmap will be used as result but it mights be cases where we looked for other
     * results type. Converter provides method to transform bitmap in T type.
     */
    @NonNull private Converter<T, Bitmap> converter;

    public FileFetcher(String src, @Nullable T default_result, WeakReference<Context> c,
                       @NonNull Converter<T,Bitmap> converter, @NonNull Pair<Integer, Integer> dims)
    {
        this.source = src;
        this.default_result = default_result;
        this.converter = converter;
        this.c = c;
        this.dims = dims;
    }


    /**
     * @return image fetched as T type. If an error occurred while fetching data and
     * {@link FileFetcher#default_result} has been set, it will be return otherwise
     * the callable will return null.
     */
    @Override
    public T call() throws Exception {
        T res = default_result;
        if(c.get() != null)
        {
                /* Returns null if the specified file name is null,
                    or cannot be decoded into a bitmap. This method will load scaled down version
                    of bitmap if needed.*/
            Bitmap bm = BitmapLoader.decodeSampledBitmapFromFile(source, dims.first, dims.second);
            if(bm != null)
                res = converter.convert(bm, c);
        }
        return res;
    }
}
