package com.example.medicinealert;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;

public class AfterBootSetup extends IntentService {
    DatabaseHelper databaseHelper;
    public AfterBootSetup() {
        super("AfterBootSetup");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        getAlarmInfo();
    }

    private void getAlarmInfo(){
        databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        String strH = "", strM = "", strI = "", disName = "";

        Cursor cursor = databaseHelper.displayRemInfo();
        while (cursor.moveToNext()){
            disName = cursor.getString(1);
            strH = cursor.getString(5);
            strM = cursor.getString(6);
            strI = cursor.getString(7);
        }
        int int_fireHour = Integer.parseInt(strH);
        int int_fireMinute = Integer.parseInt(strM);
        int int_fireInterval = Integer.parseInt(strI);
        String disName1 = disName;

        Intent intent = new Intent(AfterBootSetup.this, AlarmRec.class);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, int_fireHour);
        calendar.set(Calendar.MINUTE, int_fireMinute);

        intent.putExtra("data","on");
        intent.putExtra("data1", 467);
        intent.putExtra("data2", disName1);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(AfterBootSetup.this,
                467, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), int_fireInterval, pendingIntent);
    }
}
