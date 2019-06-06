package com.pouzadf.tinyloading.Utils;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 *  Simple executor to perform task on the UI thread.
 */

public class MainThreadExecutor implements Executor {

    private Handler mHandler;

    public MainThreadExecutor()
    {
        mHandler =  new Handler(Looper.getMainLooper());
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        mHandler.post(runnable);
    }
}
