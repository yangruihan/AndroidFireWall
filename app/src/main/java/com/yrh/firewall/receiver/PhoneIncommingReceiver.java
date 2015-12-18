package com.yrh.firewall.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.yrh.firewall.utils.DBOpenHelper;

import java.lang.reflect.Method;

import com.android.internal.telephony.*;

/**
 * Created by Yrh on 2015/12/18.
 */
public class PhoneIncommingReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneIncommingReceiver";
    private static boolean incomingFlag = false;
    private static String incoming_number = null;
    private Context mycon;

    private DBOpenHelper dbOpenHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 初始化数据库
        dbOpenHelper = new DBOpenHelper(context, "db", null, DBOpenHelper.DB_VERSION);

        // 如果是去电
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            incomingFlag = false;
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.i(TAG, "call OUT:" + phoneNumber);
        } else {
            // 如果是来电
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING: {
                    incomingFlag = true;
                    incoming_number = intent.getStringExtra("incoming_number");

                    // 如果来电在来电黑名单中，则拦截
                    if (dbOpenHelper.isInIncommingBlackList(incoming_number)) {
                        stopCall(incoming_number); // 拦截电话
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
    }

    /**
     * 停止通话
     * @param incoming_number
     */
    private void stopCall(String incoming_number) {
        AudioManager mAudioManager = (AudioManager) mycon.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);//静音处理
        ITelephony iTelephony = getITelephony(mycon); //获取电话接口
        try {

            iTelephony.endCall();//结束电话
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //再恢复正常铃声
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Log.i("----", "来电 :" + incoming_number);
    }

    private static ITelephony getITelephony(Context context) {

        TelephonyManager mTelephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        Class c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            getITelephonyMethod = c.getDeclaredMethod("getITelephony",
                    (Class[]) null); // 获取声明的方法
            getITelephonyMethod.setAccessible(true);
        } catch (SecurityException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        ITelephony iTelephony = null;
        try {
            iTelephony = (ITelephony) getITelephonyMethod.invoke(
                    mTelephonyManager, (Object[]) null); // 获取实例
            return iTelephony;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iTelephony;
    }
}
