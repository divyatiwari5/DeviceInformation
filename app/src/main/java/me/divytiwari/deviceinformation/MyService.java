package me.divytiwari.deviceinformation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return onBind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        return super.onStartCommand(intent, flags, startID);
    }
}
