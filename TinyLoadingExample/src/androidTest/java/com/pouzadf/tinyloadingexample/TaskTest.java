package com.pouzadf.tinyloadingexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.Pair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.test.InstrumentationRegistry;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.pouzadf.tinyloading.Converter.Converter;
import com.pouzadf.tinyloading.Converter.ConverterFactory;
import com.pouzadf.tinyloading.Fetcher.Tasks.ResourceFetcher;
import com.pouzadf.tinyloading.Fetcher.Tasks.UrlFetcher;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4ClassRunner.class)
public class TaskTest {


    private final String src = "https://cdn131.picsart.com/295367758156201.jpg?c256x256";
    private Context c;
    private Converter converter;
    private Pair<Integer, Integer> dims;
    private Bitmap fallback;

    @Before
    @SuppressWarnings("unchecked")
    public void setup()
    {
        c =  InstrumentationRegistry.getTargetContext();
        converter = ConverterFactory.getConverter(ConverterFactory.type.def);
        dims = new Pair<>(200,200);
        Drawable d = ContextCompat.getDrawable(c, R.drawable.ic_launcher_background);
        fallback =(Bitmap) ConverterFactory.getConverter(ConverterFactory.type.DrawableToBitmap).
                convert(d, new WeakReference<Context>(c));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void assertNotNull_WhenFetchingFromUrlSrc()
    {

        Callable<Bitmap> callable = new UrlFetcher<>(src,null,
                new WeakReference<Context>(c),converter,dims,null);
        FutureTask<Bitmap> task = new FutureTask<Bitmap>(callable)
        {
            @Override
            protected void done() {
                super.done();
                try {
                    /*If fetch fails returns null or fallback, returns data otherwise*/
                    Bitmap res = get();
                    Assert.assertNotNull(res);
                } catch (InterruptedException | CancellationException e) {
                    fail();
                } catch (ExecutionException e) {
                    /* Something went wrong while thread was being executed */
                    fail();
                }
            }
        };
        Thread th = new Thread(task);
        th.start();
        try
        {
            th.join();
        }
        catch (InterruptedException e)
        {
            fail();
        }
    }




    @Test
    @SuppressWarnings("unchecked")
    public void assertNull_whenFetchingFromUnknownUrlSrc_withNoFallback()
    {

        Callable<Bitmap> callable = new UrlFetcher<>("http",null,
                new WeakReference<Context>(c),converter,dims,null);
        FutureTask<Bitmap> task = new FutureTask<Bitmap>(callable)
        {
            @Override
            protected void done() {
                super.done();
                try {
                    /*If fetch fails returns null or fallback, returns data otherwise*/
                    Bitmap res = get();
                    Assert.assertNull(res);
                } catch (InterruptedException | CancellationException e) {
                    fail();
                } catch (ExecutionException e) {
                    /* Something went wrong while thread was being executed */
                    fail();
                }
            }
        };
        Thread th = new Thread(task);
        th.start();
        try
        {
            th.join();
        }
        catch (InterruptedException e)
        {
            fail();
        }
    }



    @Test
    @SuppressWarnings("unchecked")
    public void assertEqual_whenFetchingFromUnknownUrlSrc_withFallback()
    {

        Callable<Bitmap> callable = new UrlFetcher<>("http",fallback,
                new WeakReference<Context>(c),converter,dims,null);
        FutureTask<Bitmap> task = new FutureTask<Bitmap>(callable)
        {
            @Override
            protected void done() {
                super.done();
                try {
                    /*If fetch fails returns null or fallback, returns data otherwise*/
                    Bitmap res = get();
                    Assert.assertEquals(fallback, res);
                } catch (InterruptedException | CancellationException e) {
                    fail();
                } catch (ExecutionException e) {
                    /* Something went wrong while thread was being executed */
                    fail();
                }
            }
        };
        Thread th = new Thread(task);
        th.start();
        try
        {
            th.join();
        }
        catch (InterruptedException e)
        {
            fail();
        }
    }




    @Test
    @SuppressWarnings("unchecked")
    public void assertNotNull_whenFetchingFromRes()
    {
        Callable<Bitmap> callable = new ResourceFetcher<>(R.drawable.ic_launcher_background,null,
                new WeakReference<Context>(c),
                ConverterFactory.getConverter(ConverterFactory.type.DrawableToBitmap),dims);
        FutureTask<Bitmap> task = new FutureTask<Bitmap>(callable)
        {
            @Override
            protected void done() {
                super.done();
                try {
                    /*If fetch fails returns null or fallback, returns data otherwise*/
                    Bitmap res = get();
                    Assert.assertNotNull(res);
                } catch (InterruptedException | CancellationException e) {
                    fail();
                } catch (ExecutionException e) {
                    /* Something went wrong while thread was being executed */
                    fail();
                }
            }
        };
        Thread th = new Thread(task);
        th.start();
        try
        {
            th.join();
        }
        catch (InterruptedException e)
        {
            fail();
        }
    }


    @Test
    @SuppressWarnings("unchecked")
    public void throwsExecutionException_WhenFetchingFromRes_UnknownResId()
    {
        Callable<Bitmap> callable = new ResourceFetcher<>(15,null,
                new WeakReference<Context>(c),
                ConverterFactory.getConverter(ConverterFactory.type.DrawableToBitmap),dims);
        FutureTask<Bitmap> task = new FutureTask<Bitmap>(callable)
        {
            @Override
            protected void done() {
                super.done();
                try {
                    /*If fetch fails returns null or fallback, returns data otherwise*/
                    Bitmap res = get();
                    fail();
                } catch (InterruptedException | CancellationException e) {
                    fail();
                } catch (ExecutionException e) {
                    /* Something went wrong while thread was being executed */
                }
            }
        };
        Thread th = new Thread(task);
        th.start();
        try
        {
            th.join();
        }
        catch (InterruptedException e)
        {
            fail();
        }
    }
}
