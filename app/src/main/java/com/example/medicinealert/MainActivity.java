package com.example.medicinealert;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ImageButton plusClick, btnTimepicker;
    Dialog dialog;
    EditText editName, editHour, editDay;
    Button btnCreate, btnReset;
    TimePickerDialog timePickerDialog;
    TextView textStartTime, textMainTime, textHour, textDay, textName;
    Switch aSwitch;
    DatabaseHelper databaseHelper;
    String minutesDB, hoursDB, minutes, hours, ampm;
    String disName="", disHour="", disDay="", disTime="", ck_status="", end_day="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textName = findViewById(R.id.text_name_id);
        textHour = findViewById(R.id.text_hour_id);
        textDay = findViewById(R.id.text_day_id);
        textMainTime = findViewById(R.id.text_starttime_id);
        plusClick = findViewById(R.id.add_taskButton_ID);
        aSwitch = findViewById(R.id.switch_id);
        btnReset = findViewById(R.id.btnReset_ID);
        dialog = new Dialog(this);

        databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        Cursor cursor = databaseHelper.displayRemInfo();
        while (cursor.moveToNext()){
            disName = cursor.getString(1);
            disHour = cursor.getString(2);
            disDay = cursor.getString(3);
            disTime = cursor.getString(4);
            ck_status = cursor.getString(8);
            end_day = cursor.getString(9);

            textName.setText(disName);
            textHour.setText(disHour + " Hour");
            textDay.setText(end_day + " Day");
            textMainTime.setText(disTime);
        }

        if(ck_status.matches("true")){
            aSwitch.setChecked(true);
        }

        //....Future date AlarmCancel()
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String currentDate = sdf.format(calendar.getTime());
            String targetDate = disDay;

            Date date1 = sdf.parse(currentDate);
            Date date2 = sdf.parse(targetDate);

            if(date1.compareTo(date2)==0){
                databaseHelper.updateRemStatus("1", "false");
                aSwitch.setChecked(false);
                //....alarm service stop
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), AlarmRec.class);
                intent.putExtra("data","off");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                        467, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);

                databaseHelper.deleteRemInfo();
                textName.setText("Not yet entered.");
                textHour.setText("Not yet entered.");
                textDay.setText("Not yet entered.");
                textMainTime.setText("Not yet entered.");
            }
            else{
                Log.d("TAG","Date mismatch");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        plusClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.task_dialog);
                editName = dialog.findViewById(R.id.dialog_name_id);
                editHour = dialog.findViewById(R.id.dialog_hour_id);
                editDay = dialog.findViewById(R.id.dialog_day_id);
                textStartTime = dialog.findViewById(R.id.dialog_starttime_id);
                btnCreate = dialog.findViewById(R.id.dialog_button_id);
                btnTimepicker = dialog.findViewById(R.id.dialog_timepicker_id);

                btnTimepicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePicker timePicker = new TimePicker(MainActivity.this);
                        final int currentHour = timePicker.getCurrentHour();
                        final int currentMinute = timePicker.getCurrentMinute();
                        timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                hours= String.valueOf(hourOfDay);
                                minutes= String.valueOf(minute);

                                hoursDB= String.valueOf(hourOfDay);
                                minutesDB= String.valueOf(minute);

                                if(hourOfDay>=0 && hourOfDay<=11){
                                    if(hourOfDay==0){
                                        hours=String.valueOf(12);
                                    }
                                    ampm="am";
                                }
                                else{
                                    if(hourOfDay>=13 && hourOfDay<=23){
                                        hours=String.valueOf(hourOfDay-12);
                                    }
                                    ampm="pm";
                                }
                                if(minute<10){
                                    minutes=String.valueOf("0"+String.valueOf(minute));
                                }
                                String timeStr=hours+":"+minutes+" "+ampm;
                                textStartTime.setText(timeStr);
                            }
                        },currentHour, currentMinute, true);
                        timePickerDialog.show();
                    }
                });

                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String strName = editName.getText().toString();
                        String strHour = editHour.getText().toString();
                        String strTime = textStartTime.getText().toString();
                        String startFireHour = hoursDB;
                        String startFireMinute = minutesDB;
                        String ckstatus = "false";

                        //Future date calculation
                        String strDay = editDay.getText().toString();
                        int intDay = Integer.parseInt(strDay);

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        calendar.add(calendar.DATE, intDay);
                        String targetDate = sdf.format(calendar.getTime());

                        //Time interval calculation
                        int calcMinute = Integer.parseInt(strHour)*1000*60*60;
                        String intervalHour = String.valueOf(calcMinute);

                        long rowID = databaseHelper.insertRemInfo(strName, strHour, targetDate, strTime, startFireHour,
                                startFireMinute, intervalHour, ckstatus, strDay);

                        textName.setText(strName);
                        textHour.setText(strHour + " Hour");
                        textDay.setText(strDay + " Day");
                        textMainTime.setText(strTime);

                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    databaseHelper.updateRemStatus("1", "true");
                    aSwitch.setChecked(true);

                    String strH = "", strM = "", strI = "";
                    Cursor cursor = databaseHelper.displayRemInfo();
                    while (cursor.moveToNext()){
                        strH = cursor.getString(5);
                        strM = cursor.getString(6);
                        strI = cursor.getString(7);
                    }
                    int int_fireHour = Integer.parseInt(strH);
                    int int_fireMinute = Integer.parseInt(strM);
                    int int_fireInterval = Integer.parseInt(strI);
                    String disName1 = disName;

                    //....alarm service start
                    AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), AlarmRec.class);
                    intent.putExtra("data","on");//status data
                    intent.putExtra("data1", 467);
                    intent.putExtra("data2", disName1);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 467, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.HOUR_OF_DAY, int_fireHour);
                    calendar.set(Calendar.MINUTE, int_fireMinute);

                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), int_fireInterval, pendingIntent);
                }
                else {
                    databaseHelper.updateRemStatus("1", "false");
                    aSwitch.setChecked(false);

                    //....alarm service stop
                    AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getApplicationContext(), AlarmRec.class);
                    intent.putExtra("data","off");//status data
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                            467, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.cancel(pendingIntent);
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aSwitch.setChecked(false);
                databaseHelper.deleteRemInfo();
                textName.setText("Not yet entered.");
                textHour.setText("Not yet entered.");
                textDay.setText("Not yet entered.");
                textMainTime.setText("Not yet entered.");
                Toast.makeText(MainActivity.this, "All Info Delete. Now DB is clean!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
