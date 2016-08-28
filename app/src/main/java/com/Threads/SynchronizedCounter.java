package com.Threads;

import android.util.Log;

/**
 * Created by hp on 8/23/2016.
 */
public class SynchronizedCounter {

    private int c = 0;

    public synchronized void setC(int c) {
        this.c = c;
    }

    public synchronized void increment() {
        c++;
    }

    public synchronized  void decrement() {
        c--;
    }

    public synchronized int value(String className) {
        Log.e("Value of C",String.valueOf(c+"from class: "+ className));
        return c;
    }
}
