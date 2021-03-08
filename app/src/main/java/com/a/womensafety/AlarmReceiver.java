package com.a.womensafety;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    private final String SOMEACTION = "com.a.alarmcheck";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (SOMEACTION.equals(action)) {
            /**
             * call method to send sms
             */
            MainActivity.sendSms();

        }
    }


}
