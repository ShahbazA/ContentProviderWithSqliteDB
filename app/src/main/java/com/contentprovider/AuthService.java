package com.contentprovider;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by hp on 8/23/2016.
 */
public class AuthService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mAbstractAccountAuthenticator authenticator = new mAbstractAccountAuthenticator(this);
        return authenticator.getIBinder();
    }
}
