package com.humbels.alarm2mqtt;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by luki on 03.06.16.
 */

public class AlarmLightSettings {
    public static final String PREFS_NAME = "AlarmLightPrefs";
    public static final String PREFS_SERVER = "server";
    public static final String PREFS_PORT = "port";
    public static final String PREFS_USERNAME = "username";
    public static final String PREFS_DEVPREFIX = "devprefix";
    public static final String PREFS_PASSWORD = "password";
    public static final String PREFS_ALARMSYNC = "alarmsync";
    public static final String PREFS_LOG = "log";

    private SharedPreferences prefs;
    AlarmLightSettings(Context context){
        prefs = context.getSharedPreferences(PREFS_NAME,0);
    }

    public Boolean hasServer(){
        return getServer() != null && getServer().length() > 0;
    }

    public String getServer(){
        return prefs.getString(PREFS_SERVER,"");
    }

    public int getPort() {
        return prefs.getInt(PREFS_PORT, 1883);
    }

    public String getPassword(){
        return prefs.getString(PREFS_PASSWORD, "");
    }

    public String getDevprefix(){
        return prefs.getString(PREFS_DEVPREFIX, "");
    }

    public String getUsername(){
        return prefs.getString(PREFS_USERNAME,"");
    }

    public Boolean getAlarmSync(){
        return prefs.getBoolean(PREFS_ALARMSYNC,false);
    }

    public String getAlarmTopic() { return "/" + getDevprefix() + "/alarm"; };
}
