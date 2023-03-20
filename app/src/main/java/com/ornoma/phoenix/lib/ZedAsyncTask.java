package com.ornoma.phoenix.lib;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by de76 on 5/27/17.
 */

public abstract class ZedAsyncTask {
    public abstract void doInBackground();
    public abstract void onPostExecute();

    public void execute(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                doInBackground();
                onFinish();
            }
        });
        thread.start();
    }

    private void onFinish(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                onPostExecute();
            }
        });
    }
}
