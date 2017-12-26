package com.humbels.alarm2mqtt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;

import com.humbels.mqtt.PublishMsg;


/**
 * Created by luki on 25.04.16.
 */
public class NextAlarmChangedReceiver extends BroadcastReceiver {

    private static final String TAG = "NextAlarmChangedRcv";

    @Override
    public void onReceive(Context context, Intent intent) {
        syncAlarmNow(context);
    }

    public static void syncAlarmNow(Context context) {
        AlarmLightSettings set = new AlarmLightSettings(context);
        //String message = "Setting device alarm to: " + Utils.getNextAlarmStr(context);

        if(set.getAlarmSync()){
            String message = String.valueOf(Utils.getNextAlarmTime(context));
            Log.d(TAG, "Send to mqtt(" + set.getAlarmTopic() + "): " + message);
            LogUtil.debug("Send to mqtt(" + set.getAlarmTopic() + "): " + message);
            new MySendMessageAsyncTask(context).execute(new PublishMsg(set.getAlarmTopic(), message.getBytes()));
        } else {
            Log.e(TAG, "Ignoring new alarm: " + Utils.getNextAlarmStr(context));
            LogUtil.debug("Ignoring new alarm: " + Utils.getNextAlarmStr(context));
        }
    }

    private static class MySendMessageAsyncTask extends SendMessageAsyncTask {
        public MySendMessageAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(this.e == null){
                Log.d(TAG, "Transmission succesful");
                LogUtil.debug("Transmission succesful");
            } else {
                Log.d(TAG, "Transmission failed: " + e.toString());
                LogUtil.debug("Transmission failed: " + e.toString());
            }
        }
    }
}
