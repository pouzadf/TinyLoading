package com.pouzadf.tinyloading.Fetcher.Tasks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 *  This interface provides custom implementation for fetching task callbacks.
 **/
public interface CompletionListener<T> {

    /**
     *
     * @param data Image as T type will most of the time be a Bitmap or Drawable.
     */
    abstract  void taskSucceeded(@NonNull T data);

    /**
     *
     * @param data will be the placeholder if it has been set, might be null.
     * @param hasBeenCancelled indicates whether the task has been cancelled or not
     */
    abstract public void taskFailed(@Nullable T data, boolean hasBeenCancelled);

}
