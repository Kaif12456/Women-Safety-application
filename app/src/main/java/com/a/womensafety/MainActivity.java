package com.a.womensafety;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {
    db dbhandler;
    static HashMap<String, String> recorArray = null;
    Context context = MainActivity.this;

    ProgressDialog pd;
    EditText Msg;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;


    public void onclick(View view) {
        Intent i = new Intent(this, Add_Numbers.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
    }

    public void onclick2(View view) {
        Intent i = new Intent(this, Tips.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure, you want to exit?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("no", null).show();
    }

    //btnsend
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void message(View view) {
        if (dbhandler.number() == 2) {
            String phoneNo1 = dbhandler.databaseToPhoneFirst();
            String phoneNo2 = dbhandler.databaseToPhoneSecond();
            Double latitude = 0.0, longitude;
            String message = "Need Your Help. I am in danger.Please Contact me ASAP";
            LocationManager mlocManager = null;
            LocationListener mlocListener;
            mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mlocListener = new MyLocationListener(this);
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                latitude = MyLocationListener.latitude;
                longitude = MyLocationListener.longitude;    //latitude],[longitude]
                message = message + "\n My Location is - " + "https://maps.google.com/maps?daddr=" + latitude + ","
                        + longitude;
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                if (latitude == 0.0) {
                    Toast.makeText(getApplicationContext(), "Currently gps has not found your location....", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "GPS is currently off...", Toast.LENGTH_LONG).show();
            }
            //message sending

            recorArray.put(phoneNo1, message);
            recorArray.put(phoneNo2,message);
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo1, null, message, null, null);
                //Toast.makeText(getApplicationContext(), "SMS1 sent.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // Toast.makeText(getApplicationContext(), "SMS1 faild, please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo2, null, message, null, null);
                //Toast.makeText(getApplicationContext(), "SMS2 sent.", Toast.LENGTH_LONG).show();
                // Toast.makeText(getApplicationContext(), "You have sent this message: "+ message, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // Toast.makeText(getApplicationContext(), "SMS2 faild, please try again.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please add two phone numbers of close ones first.....", Toast.LENGTH_LONG).show();
        }

        Toast.makeText(context, "x "+recorArray, Toast.LENGTH_SHORT).show();
        fireAlarm();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recorArray = new HashMap<String, String>();

        getSupportActionBar().setTitle("WOMEN LIFESTYLE BOOK");
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.SEND_SMS,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }).check();

//key board
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dbhandler = new db(this, null, null, 1);
        Bundle numbers = getIntent().getExtras();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onShake(int count) {
                if (count == 3) {

                    if (dbhandler.number() == 2) {
                        String phoneNo1 = dbhandler.databaseToPhoneFirst();
                        String phoneNo2 = dbhandler.databaseToPhoneSecond();
                        Double latitude = 0.0, longitude;
                        String message = "Need Your Help. I am in danger.Please Contact me ASAP";
                        LocationManager mlocManager = null;
                        LocationListener mlocListener;
                        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        mlocListener = new MyLocationListener(MainActivity.this);
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    Activity#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.
                            return;
                        }
                        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
                        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                            latitude = MyLocationListener.latitude;
                            longitude = MyLocationListener.longitude;    //latitude],[longitude]
                            message = message + "\n My Location is - " + "https://maps.google.com/maps?daddr=" + latitude + ","
                                    + longitude;
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            if (latitude == 0.0) {
                                Toast.makeText(getApplicationContext(), "Currently gps has not found your location....", Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "GPS is currently off...", Toast.LENGTH_LONG).show();
                        }
                        //message sending

                        recorArray.put(phoneNo1,message);
                        recorArray.put(phoneNo2,message);
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNo1, null, message, null, null);
                            //Toast.makeText(getApplicationContext(), "SMS1 sent.", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            // Toast.makeText(getApplicationContext(), "SMS1 faild, please try again.", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNo2, null, message, null, null);
                            //Toast.makeText(getApplicationContext(), "SMS2 sent.", Toast.LENGTH_LONG).show();
                            // Toast.makeText(getApplicationContext(), "You have sent this message: "+ message, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            // Toast.makeText(getApplicationContext(), "SMS2 faild, please try again.", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please add two phone numbers of close ones first.....", Toast.LENGTH_LONG).show();
                    }

                }
                Toast.makeText(context, "x "+recorArray, Toast.LENGTH_SHORT).show();
                fireAlarm();

            }
        });


        if (numbers == null) {
            return;
        }
        String number1 = numbers.getString("Number1");
        String number2 = numbers.getString("Number2");

        phone_number n1 = new phone_number(number1);
        phone_number n2 = new phone_number(number2);
        dbhandler.addnumber1(n1);
        dbhandler.addnumber2(n2);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    public void onclick3(View view) {
        Intent i = new Intent(this, Laws.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
    }

    public void onclick4(View view) {
        Intent i = new Intent(this, SafetyThreat.class);
        startActivity(i);
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
    }

    public void onclick5(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        startActivity(new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", "8881471882",
                null)));
    }


    public static void sendSms() {

        Set<?> set = recorArray.entrySet();
        // Get an iterator
        Iterator<?> i = set.iterator();
        // Display elements
        while (i.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry me = (Map.Entry) i.next();
            System.out.print(me.getKey() + ": ");
            Log.d("msg123",me.getKey() + ": ");
            System.out.println(me.getValue());
            Log.d("msg456",me.getValue().toString());

            try {
                android.telephony.gsm.SmsManager smsManager = android.telephony.gsm.SmsManager.getDefault();
                smsManager.sendTextMessage(me.getKey().toString(), null, me
                        .getValue().toString(), null, null);
                System.out.println("message sent");
            } catch (Exception e) {
                System.out.println("sending failed!");
                e.printStackTrace();
            }
        }
    }
    public void fireAlarm() {
        /**
         * call broadcost reciver for send sms
         */
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("com.a.alarmcheck");
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(MainActivity.this, 0, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        AlarmManager alarm = (AlarmManager) MainActivity.this
                .getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                100000, pendingIntent);
    }

}
