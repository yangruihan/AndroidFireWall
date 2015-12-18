package com.yrh.firewall.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.yrh.firewall.utils.DBOpenHelper;

import java.lang.reflect.Method;

/**
 * Created by Yrh on 2015/12/18.
 */
public class PhoneIncommingReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneIncommingReceiver";
    private static boolean incomingFlag = false;
    private static String incoming_number = null;
    private TelephonyManager tm;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Info", "ON RECEIVE");

        // 初始化数据库
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context, "db", null, DBOpenHelper.DB_VERSION);

        // 如果是来电
        tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

        switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING: {
                incomingFlag = true;
                incoming_number = intent.getStringExtra("incoming_number");

                // 如果来电在来电黑名单中，则拦截
                if (dbOpenHelper.isInIncommingBlackList(incoming_number)) {
                    endCall(); // 拦截电话
                    abortBroadcast(); // 截断广播
                }
                Log.i(TAG, "RINGING :" + incoming_number);
                break;
            }
            case TelephonyManager.CALL_STATE_OFFHOOK: {
                if (incomingFlag) {
                    Log.i(TAG, "incoming ACCEPT :" + incoming_number);
                }
                break;
            }
            case TelephonyManager.CALL_STATE_IDLE: {
                if (incomingFlag) {
                    Log.i(TAG, "incoming IDLE");
                }
                break;
            }

        }
    }

    /**
     * 挂断电话
     */
    private void endCall() {
        Class<TelephonyManager> c = TelephonyManager.class;
        try {
            Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            Log.e(TAG, "End call.");
            iTelephony = (ITelephony) getITelephonyMethod.invoke(tm, (Object[]) null);
            iTelephony.endCall();
        } catch (Exception e) {
            Log.e(TAG, "Fail to answer ring call.", e);
        }
    }
}