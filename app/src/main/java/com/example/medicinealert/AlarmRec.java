package com.example.medicinealert;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmRec extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        ComponentName receiver = new ComponentName(context, AlarmRec.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        String mName = null;
        String mDay = null;
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        Cursor cursor = databaseHelper.displayRemInfo();
        while (cursor.moveToNext()){
            mName = cursor.getString(1);
            mDay = cursor.getString(3);
        }

        try{
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String currentDate = sdf.format(calendar.getTime());
            String targetDate = mDay;

            Date date1 = sdf.parse(currentDate);
            Date date2 = sdf.parse(targetDate);

            if(date1.compareTo(date2)==0){
                Log.d("TAG", "Date Matched --> AlarmRec OFF");

                AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                intent = new Intent(context, AlarmRec.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        467, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
            }else{
                Log.d("TAG", "Date Mismatched --> AlarmRec ON");

                String get_data = intent.getExtras().getString("data");
                int s = intent.getExtras().getInt("data1");
                String getNotify = mName;

                Intent intent_rec = new Intent(context, AlarmNotify.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent_rec.putExtra("data", get_data);
                intent_rec.putExtra("data1", s);
                intent_rec.putExtra("data2", getNotify);
                context.startService(intent_rec);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
