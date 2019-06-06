package com.pouzadf.tinyloading.Converter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;

import java.lang.ref.WeakReference;

/**
 *  This class implements the factory design pattern. It provides any kind of converter implemented yet.
 *  Converter will be use to get the image as an object that can be set in user's container.
 */

public class ConverterFactory {

    public enum type
    {
        DrawableToBitmap,
        BitmapToDrawable,
        def

    }

    public static Converter getConverter(type t)
    {
        switch (t)
        {
            case BitmapToDrawable:
                return BitmapToDrawable;
            case DrawableToBitmap:
                return DrawableToBitmap;
            default:
                return def;
        }
    }



    private static Converter<Drawable, Bitmap> BitmapToDrawable = new Converter<Drawable, Bitmap>() {
        /**
         * @param input Bitmap to convert
         * @param c Weak reference to context to prevent mem leaks.
         * @return Drawable from existing bitmap, null if the context has been corrupted.
         */
        @Override
        public Drawable convert(Bitmap input, WeakReference<Context> c) {
            return c.get() != null ? new BitmapDrawable(c.get().getResources(), input) : null;
        }
    };



    private static Converter<Bitmap, Drawable> DrawableToBitmap = new Converter<Bitmap, Drawable>() {
        /**
         * @param drawable Drawable to convert
         * @param c Weak reference to context to prevent mem leaks
         * More infos, https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
         */
        @Override
        public Bitmap convert(Drawable drawable, WeakReference<Context> c) {
            Bitmap bitmap;

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if(bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    };


    private static Converter<Bitmap, Bitmap> def = new Converter<Bitmap, Bitmap>() {
        @Override
        public Bitmap convert(Bitmap input, WeakReference<Context> c) {
            return c.get() != null ?  input : null;
        }
    };
}
