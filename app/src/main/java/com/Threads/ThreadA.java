package com.Threads;

/**
 * Created by hp on 8/23/2016.
 */
public class ThreadA extends Thread{
    SynchronizedCounter synchronizedCounter = new SynchronizedCounter();

    @Override
    public void run() {
        synchronizedCounter.value("A");
        synchronizedCounter.increment();
        synchronizedCounter.setC(synchronizedCounter.value("A"));
        synchronizedCounter.increment();
        synchronizedCounter.setC(synchronizedCounter.value("A"));
        synchronizedCounter.increment();
        synchronizedCounter.setC(synchronizedCounter.value("A"));
        synchronizedCounter.increment();
        synchronizedCounter.setC(synchronizedCounter.value("A"));
        synchronizedCounter.increment();
        synchronizedCounter.setC(synchronizedCounter.value("A"));
        synchronizedCounter.increment();
        synchronizedCounter.setC(synchronizedCounter.value("A"));
    }
}
