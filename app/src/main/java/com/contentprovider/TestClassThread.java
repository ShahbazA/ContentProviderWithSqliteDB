package com.contentprovider;

import android.util.Log;

/**
 * Created by hp on 8/23/2016.
 */
public class TestClassThread extends Thread{

    @Override
    public void run() {
        super.run();
        String importantInfo[] = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"};
        for (int i = 0;   i < importantInfo.length;    i++) {
            //Pause for 4 seconds
            try {
                Thread.sleep(4000);
                Log.e("ThreadA",importantInfo[i]);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        }

    }


