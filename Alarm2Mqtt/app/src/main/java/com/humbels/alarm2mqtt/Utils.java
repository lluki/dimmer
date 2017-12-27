package com.humbels.alarm2mqtt;

import android.app.AlarmManager;
import android.content.Context;
import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by luki on 25.04.16.
 */
public class Utils {

    public static String getFormattedTime(Context context, Calendar time) {
        final String skeleton = DateFormat.is24HourFormat(context) ? "EHm" : "Ehma";
        final String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), skeleton);
        return (String) DateFormat.format(pattern, time);
    }

    public static Calendar getNextAlarmCal(Context context) {
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final AlarmManager.AlarmClockInfo info = am.getNextAlarmClock();
        if (info != null) {
            final long triggerTime = info.getTriggerTime();
            final Calendar alarmTime = Calendar.getInstance();
            alarmTime.setTimeInMillis(triggerTime);
            return alarmTime;
        }

        return null;
    }

    public static String getNextAlarmStr(Context context){
        Calendar nextAlarm = getNextAlarmCal(context);
        if(nextAlarm != null){
            return getFormattedTime(context, nextAlarm);
        } else {
            return "No alarm scheduled";
        }
    }

    /*
     * Returns UTC wall clock timestamp in MILLISECONDS
     */
    public static long getNextAlarmTimeMS(Context context){
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final AlarmManager.AlarmClockInfo info = am.getNextAlarmClock();
        if (info != null) {
            return info.getTriggerTime();
        }
        return 0;
    }
}
