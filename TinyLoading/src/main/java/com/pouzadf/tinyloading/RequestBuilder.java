package com.pouzadf.tinyloading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import android.view.ViewTreeObserver;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.pouzadf.tinyloading.Converter.Converter;
import com.pouzadf.tinyloading.Converter.ConverterFactory;
import com.pouzadf.tinyloading.Utils.DataInfos;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * This class implements the builder design pattern. It will create a {@link Request} object with
 * parameters provided by the user.
 */
public class RequestBuilder {

    private WeakReference<Context> c;
    private DataInfos infos;

    /* Will be used in case of network request, users might need to pass special headers to http request*/
    private Map<String, String> headers;

    /* If provided will be used as default image if fetching task does not succeed*/
    private Bitmap fallback;

    /* Will hold the image view reference and prevent mem leaks*/
    private WeakReference<ImageView> ctn;


    /**
     *
     * @param c Current caller context
     */
    public RequestBuilder(@NonNull Context c)
    {
            this.c = new WeakReference<Context>(c);
            infos = null;
    }


    /**
     * @return new Request with current builder parameters.
     */
    public Request build()
    {

        Request res = new Request();
        res.setContext(this.c);
        res.setInfos(this.infos);
        res.setFallback(fallback);
        res.setHeaders(headers);
        res.setCtn(ctn);
        return res;
    }


    /**
     * @param   path Path where data is located, can be an url or an uri. Will be used as a key in
     *               {@link TinyLoading#getCache()}
     * @return  RequestBuilder with infos field set
     */
    public RequestBuilder load(String path)
    {
        if(URLUtil.isValidUrl(path))
            infos = new DataInfos(DataInfos.type.URL, path, -1);
        else
            infos = new DataInfos(DataInfos.type.FILE, path, -1);
        return this;
    }

    /**
     *
     * @param d Drawable to load if the fetch task fails
     * @return current request builder with fallback field set
     */
    @SuppressWarnings("unchecked")
    public RequestBuilder fallback(Drawable d)
    {
        if(c.get() == null)
            return this;

        Converter<Bitmap, Drawable> converter =  ConverterFactory.getConverter(ConverterFactory.type.DrawableToBitmap);
        this.fallback = converter.convert(d, c);
        return this;
    }



    /**
     *
     * @param bm Bitmap to load if the fetch task fails
     * @return current request builder with fallback field set
     */
    public RequestBuilder fallback(Bitmap bm)
    {
        this.fallback = bm;
        return this;
    }

    /**
     *
     * @param headers http headers provided by the user to be sent with any http fetching request.
     * @return current request builder with headers field set.
     */
    public RequestBuilder headers(Map<String, String> headers)
    {
        this.headers = headers;
        return this;
    }







    /**
     *  Creates {@link DataInfos} object that will hold informations regarding the source of the data
     *  to fetch.
     * @param id Drawable id of the resource data, will be transform in a string and used as a key in
     *               {@link TinyLoading#getCache()}
     * @return  RequestBuilder with infos field set
     */
    public RequestBuilder load(@DrawableRes int id)
    {
        String cacheId = String.valueOf(id);
        infos = new DataInfos(DataInfos.type.RES, cacheId, id );
        return this;
    }


    /**
     * Creates and process a request based on current builder attributes. If the imageView parameter
     * has not been measured yet, register a listener to prevent the start of the request processing
     * until it measurement.
     * @param v The ImageView that will display image resources, generally Bitmaps.
     */
    public void into(ImageView v)
    {
        ctn = new WeakReference<ImageView>(v);

        /*ImageView has not been measured yet, registers a listener to ensure that request processing
           will not start until the container has been measured
         */
        if(ctn.get().getWidth() == ctn.get().getHeight() && ctn.get().getHeight() == 0)
        {
            final ViewTreeObserver vto = ctn.get().getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(ctn.get() != null) {

                        /*removeOnGlobal has been added with SDK 16.
                            remove listener for better performance*/
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                            ctn.get().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            ctn.get().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }


                        Request req = build();
                        req.process();
                    }
                }
            });
        }
        else
        {
            Request req = build();
            req.process();
        }
    }
}
