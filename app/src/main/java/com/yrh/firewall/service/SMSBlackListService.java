package com.yrh.firewall.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yrh.firewall.receiver.SMSReceiver;

/**
 * Created by Yrh on 2015/12/18.
 */
public class SMSBlackListService extends Service {

    public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private SMSReceiver smsReceiver;

    public SMSBlackListService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("INFO", "Service start");

        smsReceiver = new SMSReceiver();

        IntentFilter smsFilter=new IntentFilter();
        smsFilter.addAction(SMS_RECEIVED);
        smsFilter.setPriority(10000);
        registerReceiver(smsReceiver, smsFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("INFO", "Service stop");

        unregisterReceiver(smsReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
