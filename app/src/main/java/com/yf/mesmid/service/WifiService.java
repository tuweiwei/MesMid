package com.yf.mesmid.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


/**
 * @author tuwei
 */
public class WifiService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
