package com.pouzadf.tinyloading.Utils;

import androidx.annotation.DrawableRes;

import com.pouzadf.tinyloading.TinyLoading;

/**
 * This class holds information regarding the source where fetch the data.
 */
public class DataInfos {

    /**
     * If the data source is URI or URL {@param id} will be the source
     * to fetch data from, otherwise it will be the
     * result of toString methods on drawable id.
     */
    private String id;
    private type dtype;

    /* default value is -1 if data source is not resources. */
    private int drawableId = -1;

    public enum type
    {
        FILE,
        URL,
        RES
    }


    public String getId() {
        return id;
    }

    public type getDtype() {
        return dtype;
    }

    public int getDrawableId() {
        return drawableId;
    }

    /**
     * @param drawableID Drawable id if data is stored in resources. (Will be -1 otherwise)
     * @param dtype      Data type
     * @param id         Data id, will be the key to retrieve data from {@link TinyLoading#mCache}
     **/
    public DataInfos(type dtype, String id, @DrawableRes int drawableID)
    {
        if(drawableID != -1)
            this.drawableId = drawableID;
        this.dtype = dtype;
        this.id = id;
    }
}
