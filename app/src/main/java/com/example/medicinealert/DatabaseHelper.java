package com.example.medicinealert;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "MedicineApp.db";
    private static final String TB_NAME = "remainder_details";
    private static final String DROP_TB = "DROP TABLE IF EXISTS "+TB_NAME;
    private static final int VERSION_NUMBER = 3;

    private static final String SERIALNO = "serial_no";
    private static final String MEDICINENAME = "medicine_name";
    private static final String INTERVALHOUR = "interval_hour";
    private static final String TOTALDAY = "total_day";
    private static final String STARTEDTIME = "started_time";

    private static final String STARTFIREHOUR = "fire_hour";
    private static final String STARTFIREMINUTE = "fire_minute";
    private static final String GAPHOUR = "fire_minute_interval";
    private static final String CKSTATUS = "ck_status";
    private static final String ENDDAY = "end_day";
    private Context context;

    private static final String CREATE_TABLE = "CREATE TABLE "+TB_NAME+"" +
            "("+SERIALNO+" INTEGER PRIMARY KEY AUTOINCREMENT," +
            "" + " "+MEDICINENAME+" VARCHAR(255), "+INTERVALHOUR+" VARCHAR(255)," +
            " "+TOTALDAY+" VARCHAR(255),"+STARTEDTIME+" VARCHAR(255), "+STARTFIREHOUR+" VARCHAR(255)," +
            ""+STARTFIREMINUTE+" VARCHAR(255), "+GAPHOUR+" VARCHAR(255), "+CKSTATUS+" VARCHAR(255), "+ENDDAY+" VARCHAR(255))";
    private static final String DISPLAY_TABLE="SELECT * FROM "+TB_NAME;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION_NUMBER);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(CREATE_TABLE);
        }catch (Exception e){
            Toast.makeText(context, "CREATE TABLE Exception: "+e, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            db.execSQL(DROP_TB);
            onCreate(db);
        }catch (Exception e){
            Toast.makeText(context, "DROP TABLE Exception: "+e, Toast.LENGTH_LONG);
        }
    }

    //Insert remainder information
    public long insertRemInfo(String strName, String strHour, String strDay, String strTime, String startFireHour,
                              String startFireMinute, String intervalHour, String ckstatus, String end_day){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MEDICINENAME, strName);
        contentValues.put(INTERVALHOUR, strHour);
        contentValues.put(TOTALDAY, strDay);
        contentValues.put(STARTEDTIME, strTime);
        contentValues.put(STARTFIREHOUR, startFireHour);
        contentValues.put(STARTFIREMINUTE, startFireMinute);
        contentValues.put(GAPHOUR, intervalHour);
        contentValues.put(CKSTATUS, ckstatus);
        contentValues.put(ENDDAY, end_day);

        long rowID = sqLiteDatabase.insert(TB_NAME, null, contentValues);
        return rowID;
    }

    //Display remainder information
    public Cursor displayRemInfo() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(DISPLAY_TABLE,null);
        return  cursor;
    }

    //Delete ALL remainder data
    public void deleteRemInfo() {
        context.deleteDatabase(DB_NAME);
    }

    //Update Remainder data
    public boolean updateRemStatus(String sl, String ckstatus){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CKSTATUS, ckstatus);

        sqLiteDatabase.update(TB_NAME, contentValues, SERIALNO+" = ?", new String[]{sl});
        return true;
    }
}
