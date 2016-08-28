package com.contentprovider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by hp on 8/17/2016.
 */
public class MyBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Shahbaz", "MyBroadcastReceiver called");
        Intent startServiceIntent = new Intent(context, MyService.class);
//        context.startService(startServiceIntent);
        Log.i("SimpleWakefulReceiver", "Starting service @ " + SystemClock.elapsedRealtime());
        startWakefulService(context, startServiceIntent);
    }

}
