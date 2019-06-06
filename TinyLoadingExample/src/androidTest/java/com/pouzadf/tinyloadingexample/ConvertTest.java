package com.pouzadf.tinyloadingexample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import androidx.test.InstrumentationRegistry;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.runner.AndroidJUnit4;
import androidx.core.content.ContextCompat;

import com.pouzadf.tinyloading.Converter.ConverterFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by flowz on 05/06/2019.
 */

@RunWith(AndroidJUnit4ClassRunner.class)
public class ConvertTest {

    private Context c;
    private Bitmap bm;

    @Before
    public void setUp()
    {
        c =  InstrumentationRegistry.getTargetContext();
        String fname = "little.jpeg";
        try
        {
            InputStream testInput = c.getResources().getAssets().open(fname);
            bm = BitmapFactory.decodeStream(testInput);
        }
        catch (IOException e){

            bm = null;
        }

    }
    @Test
    @SuppressWarnings("unchecked")
    public void assertNotNull_whenConvertFromDrawableToBitmap()
    {
        WeakReference<Context> wk = new WeakReference<Context>(c);
        Bitmap bitmap = (Bitmap) ConverterFactory.getConverter(ConverterFactory.type.DrawableToBitmap)
                .convert(ContextCompat.getDrawable(c, R.drawable.ic_launcher_background), wk);
        assertNotNull(bitmap);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void assertNotNull_whenConvertFromBitmapToBitmap()
    {
        WeakReference<Context> wk = new WeakReference<Context>(c);
        Bitmap bitmap = (Bitmap) ConverterFactory.getConverter(ConverterFactory.type.def)
                .convert(bm, wk);
        assertNotNull(bitmap);
    }



    @Test
    @SuppressWarnings("unchecked")
    public void assertNotNull_whenConvertFromBitmapToDrawable()
    {
        WeakReference<Context> wk = new WeakReference<Context>(c);
        Drawable d = (Drawable) ConverterFactory.getConverter(ConverterFactory.type.BitmapToDrawable)
                .convert(bm, wk);
        assertNotNull(d);
    }

}

