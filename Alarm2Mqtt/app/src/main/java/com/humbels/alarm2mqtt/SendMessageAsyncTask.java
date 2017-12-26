package com.humbels.alarm2mqtt;

import android.content.Context;
import android.os.AsyncTask;

import com.humbels.mqtt.ConnectionRefusedException;
import com.humbels.mqtt.PublishMsg;

import java.io.IOException;

/**
 * Created by luki on 17.11.16.
 */

public class SendMessageAsyncTask extends AsyncTask<PublishMsg, Object, Boolean> {
    private final Context ctx;
    public Exception e = null;

    SendMessageAsyncTask(Context ctx){
        this.ctx = ctx;
    }

    @Override
    protected Boolean doInBackground(PublishMsg... params) {
        try {
            MqttFacadeGlue.sendMessage(ctx, params[0]);
            return true;
        } catch (IOException e) {
            this.e = e;
            return false;
        } catch (ConnectionRefusedException e) {
            this.e = e;
            return false;
        }
    }
}
