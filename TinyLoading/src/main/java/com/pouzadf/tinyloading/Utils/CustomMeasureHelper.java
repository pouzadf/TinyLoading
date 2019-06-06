package com.pouzadf.tinyloading.Utils;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewParent;

import java.lang.ref.WeakReference;

/**
 * Simple helper class, main purpose is to compute max dimensions of  Views with wrap content attributes
 */

public class CustomMeasureHelper {

    /**
     *
     * @param v View to compute dimensions
     * @param c Current context, should not have been corrupted
     * @return Returns dimensions of the view in pixels as Pair of Integer.
     */
    public static Pair<Integer, Integer> measureView(View v, Context c)
    {

        int w;
        int h;
        View tmp = v;

        /*Iterates on ancestors view, if all ancestors width attributes are set to wrapp content
            set screen width value, set the value of the first width's ancestor different of 0*/
        while (tmp != null && tmp.getWidth() == 0 )
                tmp = (View) tmp.getParent();
        w = tmp == null ? c.getResources().getDisplayMetrics().widthPixels : tmp.getWidth();

        tmp = v;

        /*Iterates on ancestors view, if all ancestors height attributes are set to wrapp content
            set screen width value, set the value of the first height's ancestor not equal to 0*/
        while (tmp != null && tmp.getHeight() == 0 )
            tmp = (View) tmp.getParent();
        h = tmp == null ? c.getResources().getDisplayMetrics().heightPixels : tmp.getWidth();


        return new Pair<>(w,h);
    }
}
