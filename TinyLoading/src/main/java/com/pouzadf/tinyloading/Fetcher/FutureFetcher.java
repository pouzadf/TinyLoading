package com.pouzadf.tinyloading.Fetcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.pouzadf.tinyloading.Fetcher.Tasks.CompletionListener;
import com.pouzadf.tinyloading.TinyLoading;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 *  Wrapped Generic Future Task class. Makes the junction between fetching task and completion tasks. Using
 *  Future as the easiest way to perform asynchronous task cancellation.
 *
 *  T is the type of the callable task. Use of generics here for the flexibility of
 *  the callable return type.
 */
public class FutureFetcher<T>
{
    /** Task which will be executed by fetchTask. May be download from url,
     *  decoding from file or resouces. Using a generic callable for the flexibility of result type.
     **/
    private @NonNull Callable<T> task;

    /* Interface providing callback success and error methods for fetchTask*/
    private @NonNull CompletionListener listener;


    private final String TAG = "Future Fetcher";

    private FutureTask<T> fetchTask;
    /**
     *
     * @param callable first task to perform by the future: it's a fetching task
     * @param listener Interface providing callback for the sucess or failure of the callable.
     */
    @SuppressWarnings("unchecked")
    public FutureFetcher(@NonNull Callable<T> callable, final @NonNull CompletionListener listener)
    {
        this.task = callable;
        this.listener = listener;
        fetchTask = new FutureTask<T>(this.task)
        {
            @Override
            protected void done() {
                try {
                /*If fetch fails returns null or fallback, returns data otherwise*/
                    T res = get();
                    if (!isCancelled()) {
                        if (res != null)
                            listener.taskSucceeded(res);
                        else
                            listener.taskFailed(res,false);
                    }
                    listener.taskFailed(res,true);
                } catch (InterruptedException | CancellationException e) {
                /* Interrupts your task  successfully*/
                    listener.taskFailed(null,true);
                } catch (ExecutionException e) {
                /* Something went wrong while thread was being executed */
                    listener.taskFailed(null,true);
                    Log.e(TAG, "Exception has been raised while executing task: " + e.toString());
                }
            }
        };
    }


    public FutureTask<T> getFetchTask() {
        Log.d(TAG, "task null ? :" + String.valueOf(fetchTask == null));
        return fetchTask;
    }
}
