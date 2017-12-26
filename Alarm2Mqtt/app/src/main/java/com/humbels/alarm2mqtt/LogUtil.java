package com.humbels.alarm2mqtt;

import java.util.ArrayList;

/**
 * Created by luki on 18.11.16.
 */

public class LogUtil {
    public static ArrayList<String> logMsgs = new ArrayList<String>();

    public static void debug(String msg){
        logMsgs.add(msg);
        while(logMsgs.size() > 10){
            logMsgs.remove(0);
        }
    }

    public static String getDebugMessages(){
        StringBuilder b = new StringBuilder();
        for(String s : logMsgs){
            b.append(s);
            b.append("\n");
        }
        return b.toString();
    }
}
