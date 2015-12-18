package com.yrh.firewall.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yrh.firewall.receiver.PhoneIncommingReceiver;

/**
 * Created by Yrh on 2015/12/18.
 */
public class IncommingBlackListService extends Service {

    public static final String CALL_PHONE_RECEIVED = "android.intent.action.NEW_OUTGOING_CALL";
    public static final String PHONE_STATE_RECEIVED = "android.intent.action.PHONE_STATE";

    private PhoneIncommingReceiver phoneIncommingReceiver;

    public IncommingBlackListService() {
        phoneIncommingReceiver = new PhoneIncommingReceiver();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i("INFO", "Service start");

        IntentFilter phoFilter=new IntentFilter();
        phoFilter.addAction(CALL_PHONE_RECEIVED);
        phoFilter.addAction(PHONE_STATE_RECEIVED);
        phoFilter.setPriority(1000);
        registerReceiver(phoneIncommingReceiver, phoFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(phoneIncommingReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
