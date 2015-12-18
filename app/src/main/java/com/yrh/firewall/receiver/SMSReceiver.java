package com.yrh.firewall.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.yrh.firewall.utils.DBOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Yrh on 2015/12/18.
 */
public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSReceiver";
    //广播消息类型
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Info", "ON RECEIVE");

        // 初始化数据库
        DBOpenHelper dbOpenHelper = new DBOpenHelper(context, "db", null, DBOpenHelper.DB_VERSION);

        //StringBuilder body=new StringBuilder("");//短信内容
        //StringBuilder sender=new StringBuilder("");//发件人
        //先判断广播消息
        String action = intent.getAction();
        if (SMS_RECEIVED_ACTION.equals(action)) {
            //获取intent参数
            Bundle bundle = intent.getExtras();
            //判断bundle内容
            if (bundle != null) {
                //取pdus内容,转换为Object[]
                Object[] pdus = (Object[]) bundle.get("pdus");
                //解析短信
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < messages.length; i++) {
                    byte[] pdu = (byte[]) pdus[i];
                    messages[i] = SmsMessage.createFromPdu(pdu);
                }
                //解析完内容后分析具体参数
                for (SmsMessage msg : messages) {
                    //获取短信内容
                    String content = msg.getMessageBody();
                    String sender = msg.getOriginatingAddress();
                    Date date = new Date(msg.getTimestampMillis());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String sendTime = sdf.format(date);

                    // 如果发送者在黑名单里
                    if (dbOpenHelper.isInSMSBlackList(sender)) {
                        Log.i("INFO", "发送者在黑名单中");
                        abortBroadcast();
                        return;
                    }

                    // 如果发送者不在黑名单里，再判断发送内容是否包含敏感词
                    List<String> sensitiveWord = dbOpenHelper.getSensitiveWords();
                    boolean flag = false;
                    for (String s : sensitiveWord) {
                        if (content.contains(s)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        Log.i("INFO", "发送者使用了敏感词语");
                        abortBroadcast();
                    }
                }
            }
        }//if 判断广播消息结束
    }
}
