package com.pouzadf.tinyloading.Converter;

import android.content.Context;
import android.graphics.Bitmap;

import java.lang.ref.WeakReference;

/**
 *  This class provides an interface for conversion tool in the library.
 **/
public interface Converter<To,From> {
    /**
     * @param input Image to convert
     * @param c Weak reference to context to prevent mem leaks.
     * @return image converted to T type, null if the context has been corrupted or an error occurred.
     */
     To convert(From input, WeakReference<Context> c);
}
