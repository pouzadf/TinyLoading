package com.pouzadf.tinyloading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.pouzadf.tinyloading.Converter.Converter;
import com.pouzadf.tinyloading.Converter.ConverterFactory;
import com.pouzadf.tinyloading.Fetcher.FutureFetcher;
import com.pouzadf.tinyloading.Fetcher.Tasks.CompletionListener;
import com.pouzadf.tinyloading.Fetcher.Tasks.FileFetcher;
import com.pouzadf.tinyloading.Fetcher.Tasks.ResourceFetcher;
import com.pouzadf.tinyloading.Fetcher.Tasks.UrlFetcher;
import com.pouzadf.tinyloading.Utils.CustomMeasureHelper;
import com.pouzadf.tinyloading.Utils.DataInfos;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 *  Request class that contains all the mandatory information to retrieve data as a bitmap
 *  from ImageView. Each time the user wants to fetch data a request is created an processed.
 */

public class Request {

    /*Use of weak reference to prevent mem leaks, if context is corrupted, wont process the request*/
    private WeakReference<Context> c;

    /*Data infos will contains data type */
    private DataInfos infos;

    /* If provided will be used as default image if fetching task does not succeed*/
    private Bitmap fallback;

    /* Will be used in case of http request, users might need to pass special headers to the request*/
    private Map<String, String> headers;

    /* Will hold the image view reference and prevent mem leaks*/
    private WeakReference<ImageView> ctn;

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setFallback(Bitmap fallback) {
        this.fallback = fallback;
    }


    public void setCtn(WeakReference<ImageView> ctn) {
        this.ctn = ctn;
    }

    public void setContext(@NonNull WeakReference<Context> c) {
        this.c = c;
    }

    public void setInfos(@NonNull DataInfos infos) {
        this.infos = infos;
    }

    @SuppressWarnings("unchecked")
    /**
     *  Core method of the class, will create and execute the fetching task regarding parameters
     *  provided by the user to the builder
     */
    public void process()
    {
        /*should never happened*/
        if(c.get() == null || ctn.get() == null)
            return;

        /*If any request targeting ctn image view is processing, stops it*/
        TinyLoading.get().cancel(ctn.get());

        Callable<Bitmap> task;
        FutureFetcher<Bitmap> futureFetcher;
        Pair<Integer, Integer> dims;

        /*Some dimensions are set to "wrap content" attributes, computes real dimensions*/
        if(ctn.get().getHeight() == 0 || ctn.get().getWidth() == 0)
            dims = CustomMeasureHelper.measureView(ctn.get(), c.get());
        else
            dims = new Pair<>(ctn.get().getWidth(), ctn.get().getHeight());


        /*data is already stored in the cache, perfoms completion task directly*/
        Bitmap data = TinyLoading.get().getBitmapFromMemCache(infos.getId());
        if(data  != null)
        {
            defaultBitmapCompletionTask.taskSucceeded(data);
            Log.d("Request", "data retrieved from cache:"  + infos.getId());
        }


        switch(infos.getDtype())
        {
            case URL:
                Log.d("Request", "URL task chosen");
                task = new UrlFetcher<Bitmap>(infos.getId(), fallback, c,
                        ConverterFactory.getConverter(ConverterFactory.type.def), dims, headers);
                futureFetcher = new FutureFetcher<>(task, defaultBitmapCompletionTask);
                break;
            case RES:
                Log.d("Request", "Res task chosen");
                task = new ResourceFetcher<Bitmap>(infos.getDrawableId(),fallback, c,
                        ConverterFactory.getConverter(ConverterFactory.type.DrawableToBitmap), dims);
                futureFetcher = new FutureFetcher<>(task, defaultBitmapCompletionTask);
                break;
            default:
                Log.d("Request", "File task chosen");
                task = new FileFetcher<Bitmap>(infos.getId(), fallback, c,
                        ConverterFactory.getConverter(ConverterFactory.type.def), dims);
                futureFetcher = new FutureFetcher<>(task, defaultBitmapCompletionTask);
        }

        final FutureTask futureTask = futureFetcher.getFetchTask();
        TinyLoading.getTasksExecutor().execute(futureTask);
        TinyLoading.addRequest(futureTask, ctn);
    }


    /**
     *  Default interface containing methods to execute after the bitmap fetching task execution.
     */
    private final CompletionListener<Bitmap> defaultBitmapCompletionTask = new CompletionListener<Bitmap>() {
        @Override
        public void taskSucceeded(final @NonNull Bitmap data) {
            TinyLoading.removeRequest(ctn);

            /*ensures image view is still valid*/
            if(ctn.get() != null)
            {
                TinyLoading.getMainThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        ctn.get().setImageBitmap(data);
                    }
                });
                TinyLoading.get().addBitmapToMemoryCache(infos.getId() ,data);
                return;
            }
            Log.d("Request", "Task completion has not been performed as context have been " +
                    "changed during it execution");
        }

        @Override
        public void taskFailed(final @Nullable Bitmap data, boolean hasBeenCancelled) {
            TinyLoading.removeRequest(ctn);
            if(hasBeenCancelled)
                return;

            if(data != null && ctn.get() != null)
            {
                TinyLoading.getMainThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        ctn.get().setImageBitmap(data);
                    }
                });
            }
        }
    };


    /**
     *  Default interface containing methods to execute after the drawable fetching task execution.
     */
    @SuppressWarnings("unchecked")
    private final CompletionListener<Drawable> defaultDrawableCompletionTask = new CompletionListener<Drawable>() {
        @Override
        public void taskSucceeded(final @NonNull Drawable data) {

            TinyLoading.removeRequest(ctn);

            /*ensures image view is still valid*/
            if(ctn.get() != null)
            {
                TinyLoading.getMainThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        ctn.get().setImageDrawable(data);
                    }
                });

                Bitmap bm = (Bitmap) ConverterFactory.
                        getConverter(ConverterFactory.type.DrawableToBitmap).convert(data, c);
                TinyLoading.get().addBitmapToMemoryCache(infos.getId() ,bm);
                return;
            }
            Log.d("Request", "Task completion has not been performed as context have been " +
                    "changed during it execution");
        }

        @Override
        public void taskFailed(final @Nullable Drawable data, boolean hasBeenCancelled) {
            TinyLoading.removeRequest(ctn);
            if(hasBeenCancelled)
                return;

            if(data != null && ctn.get() != null)
            {
                TinyLoading.getMainThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        ctn.get().setImageDrawable(data);
                    }
                });
            }

        }
    };
}
