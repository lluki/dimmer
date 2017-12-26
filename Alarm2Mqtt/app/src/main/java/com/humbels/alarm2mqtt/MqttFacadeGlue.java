package com.humbels.alarm2mqtt;

import android.content.Context;
import android.content.SharedPreferences;

import com.humbels.mqtt.ConnectionRefusedException;
import com.humbels.mqtt.MqttClient;
import com.humbels.mqtt.MqttFacade;
import com.humbels.mqtt.PublishMsg;

import java.io.IOException;

/**
 * Created by luki on 17.11.16.
 */

public class MqttFacadeGlue {

    public static void sendMessage(Context ctx, PublishMsg msg) throws IOException, ConnectionRefusedException {
        SharedPreferences pref = ctx.getSharedPreferences(AlarmLightSettings.PREFS_NAME, 0);
        MqttFacade.sendMessage(
                pref.getString(AlarmLightSettings.PREFS_USERNAME, ""),
                pref.getString(AlarmLightSettings.PREFS_PASSWORD, ""),
                pref.getString(AlarmLightSettings.PREFS_SERVER, ""),
                pref.getInt(AlarmLightSettings.PREFS_PORT, 1883),
                msg);
    }
}
