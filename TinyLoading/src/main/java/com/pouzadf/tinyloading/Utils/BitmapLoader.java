package com.pouzadf.tinyloading.Utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Map;


/**
 * This class provides methods to prevent OOM errors while loading bitmaps.
 * It will load scaled down version of the image if it better fit the container.
 * For more info, https://developer.android.com/topic/performance/graphics/load-bitmap.html
 */
public class BitmapLoader {


    /*Will calculate inSampleSize using power of two based algorithm.
        Others algorithms might be more efficient, may need to change the implementation*/
    private static int calculateInSampleSize( BitmapFactory.Options options,
                                              int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }



    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     *
     * @param path Source path where fetch data
     * @param reqWidth Width of the container that will display bitmap result
     * @param reqHeight Height of the container that will display bitmap result
     * @return Bitmap fetched from the source, can be null if the file has not been found
     */
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     *
     * @param source Source url where fetch data
     * @param reqWidth Width of the container that will display bitmap result
     * @param reqHeight Height of the container that will display bitmap result
     * @return Bitmap fetched from the source
     * @throws IOException if any IO exception occurs during the fetching of  the data.
     */
    public static Bitmap decodeSampledBitmapFromURL(String source, int reqWidth, int reqHeight,
                                                    @Nullable Map<String, String> headers) throws IOException{

            /* Perform get request on the source */
            HttpURLConnection connection= ConnectionConfiguration.initConnection(source, headers);
            InputStream in = connection.getInputStream();

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            /*Reopening another connection as each instance of HttpURLConnection is supposed to make
            * a single request. More infos https://docs.oracle.com/javase/8/docs/api/java/net/HttpURLConnection.html*/
            in.close();
            connection.disconnect();
            connection = ConnectionConfiguration.initConnection(source, headers);
            in = connection.getInputStream();

            Bitmap bm = BitmapFactory.decodeStream(in, null, options);
            in.close();
            connection.disconnect();

            return bm;
    }
}
