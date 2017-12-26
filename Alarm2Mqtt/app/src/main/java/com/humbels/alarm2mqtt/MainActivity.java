package com.humbels.alarm2mqtt;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.humbels.mqtt.MqttFacade;
import com.humbels.mqtt.PublishMsg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    private static final String TAG = "Main";
    private TextView log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText uriText = (EditText) findViewById(R.id.uriText);
        EditText portText = (EditText) findViewById(R.id.portText);
        EditText usernameText = (EditText) findViewById(R.id.usernameText);
        EditText passwordText = (EditText) findViewById(R.id.passwordText);
        EditText devprefixText = (EditText) findViewById(R.id.devprefixText);
        CheckBox activeCb = (CheckBox) findViewById(R.id.active);

        // Initialize log view
        log = (TextView) findViewById(R.id.log);
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.setText(LogUtil.getDebugMessages());
            }
        });
        log.setText(LogUtil.getDebugMessages());

        SharedPreferences pref = getSharedPreferences(AlarmLightSettings.PREFS_NAME, 0);
        usernameText.setText(pref.getString(AlarmLightSettings.PREFS_USERNAME, ""));
        passwordText.setText(pref.getString(AlarmLightSettings.PREFS_PASSWORD, ""));
        uriText.setText(pref.getString(AlarmLightSettings.PREFS_SERVER, ""));
        portText.setText(new Integer(pref.getInt(AlarmLightSettings.PREFS_PORT, 1883)).toString());
        devprefixText.setText(pref.getString(AlarmLightSettings.PREFS_DEVPREFIX, ""));
        activeCb.setChecked(pref.getBoolean(AlarmLightSettings.PREFS_ALARMSYNC, false));

        Button test = (Button)findViewById(R.id.test_conn);

        // Task to test our MQTT connection.
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeSettings();
                new MySendMessageAsyncTask().execute(new PublishMsg("test","hello".getBytes()));
            }
        });

    }

    protected void onStop() {
        super.onStop();
        storeSettings();
    }

    private void storeSettings() {
        EditText uriText = (EditText) findViewById(R.id.uriText);
        EditText portText = (EditText) findViewById(R.id.portText);
        EditText usernameText = (EditText) findViewById(R.id.usernameText);
        EditText passwordText = (EditText) findViewById(R.id.passwordText);
        EditText devprefixText = (EditText) findViewById(R.id.devprefixText);
        CheckBox activeCb = (CheckBox) findViewById(R.id.active);

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(AlarmLightSettings.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(AlarmLightSettings.PREFS_SERVER, uriText.getText().toString());
        editor.putString(AlarmLightSettings.PREFS_PASSWORD, passwordText.getText().toString());
        editor.putString(AlarmLightSettings.PREFS_USERNAME, usernameText.getText().toString());
        editor.putString(AlarmLightSettings.PREFS_DEVPREFIX, devprefixText.getText().toString());
        editor.putBoolean(AlarmLightSettings.PREFS_ALARMSYNC, activeCb.isChecked() );
        try{
            editor.putInt(AlarmLightSettings.PREFS_PORT, Integer.parseInt(uriText.getText().toString()));
        } catch(NumberFormatException e){
            // ignore
        }
        // Commit the edits!
        editor.commit();
    }

    private class MySendMessageAsyncTask extends SendMessageAsyncTask {
        public MySendMessageAsyncTask() {
            super(MainActivity.this);
        }

        @Override
        protected void onPostExecute(Boolean resxx) {
            String res;
            if(this.e != null) {
                res = e.toString();
            } else {
                res = "MQTT connection succeeded!";
            }
            LogUtil.debug("Test message: " + res);
            log.setText(LogUtil.getDebugMessages());
            Log.d(TAG, "Test message: " + res);
            Toast.makeText(MainActivity.this, res, Toast.LENGTH_LONG).show();
        }
    }
}
