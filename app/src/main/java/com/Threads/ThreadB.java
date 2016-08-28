package com.Threads;

/**
 * Created by hp on 8/23/2016.
 */
public class ThreadB extends Thread {
    SynchronizedCounter synchronizedCounter = new SynchronizedCounter();

    @Override
    public void run() {
        synchronizedCounter.value("B");
        synchronizedCounter.decrement();
        synchronizedCounter.setC(synchronizedCounter.value("B"));
        synchronizedCounter.decrement();
        synchronizedCounter.setC(synchronizedCounter.value("B"));
        synchronizedCounter.decrement();
        synchronizedCounter.setC(synchronizedCounter.value("B"));
        synchronizedCounter.decrement();
        synchronizedCounter.setC(synchronizedCounter.value("B"));
        synchronizedCounter.decrement();
        synchronizedCounter.setC(synchronizedCounter.value("B"));
        synchronizedCounter.decrement();
        synchronizedCounter.setC(synchronizedCounter.value("B"));
    }
}
