package com.app.hinh.smart3g.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hinh1 on 7/23/2016.
 */
public class DatabaseManager {
    //dinh nghia thong tin database
    private static final String TAG = "Smart3g.Database";
    private static boolean once = true;

    private static final String DB_NAME = "manager3g";
    private static final String TB_DAYS = "managerdays";
    private static final String TB_TOTAL = "manager3g";
    private static final String TB_LIMITDATA="limitdata";
    private static final int DB_VERSION = 1;
    private static SQLiteDatabase database;
    private static HandlerThread hthread = null;
    private static Handler handler = null;

    //B4: xay dung class SQLiteOpenHelper de giup cho viec
    //upgrade, downgrade DB
    //B6: Xadung databese
    public DatabaseManager(Context context) {

        database = OpenHelper.getInstance(context).getWritableDatabase();
    }

    static {
        hthread = new HandlerThread("DatabaseManager");
        hthread.start();
        handler = new Handler(hthread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {

            }
        };
    }

    private static DatabaseManager databaseManager = null;

    public static DatabaseManager getDatabaseManager(Context context) {
        if (databaseManager == null)
            databaseManager = new DatabaseManager(context.getApplicationContext());
        else
            database = OpenHelper.getInstance(context).getWritableDatabase();

        return databaseManager;
    }

    public static class OpenHelper extends SQLiteOpenHelper {
        static OpenHelper dh = null;

        public static OpenHelper getInstance(Context context) {
            if (dh == null)
                dh = new OpenHelper(context.getApplicationContext());
            return dh;
        }

        public OpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);

            if (!once) {
                once = true;

                File dbfile = context.getDatabasePath(DB_NAME);
                if (dbfile.exists()) {
                    Log.w(TAG, "Deleting " + dbfile);
                    dbfile.delete();
                }

                File dbjournal = context.getDatabasePath(DB_NAME + "-journal");
                if (dbjournal.exists()) {
                    Log.w(TAG, "Deleting " + dbjournal);
                    dbjournal.delete();
                }
            }
        }


        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String creadTableDays = "CREATE TABLE IF NOT EXISTS managerdays(DATE TEXT PRIMARY KEY,DATA DOUBLE)";
            String createTableHuors = "CREATE TABLE IF NOT EXISTS manager3g(UID INT PRIMARY KEY, DATA DOUBLE)";
            String createTableLimitData="CREATE TABLE IF NOT EXISTS limitdata(START TEXT,DATA DOUBLE)";
            sqLiteDatabase.execSQL(creadTableDays);
            sqLiteDatabase.execSQL(createTableHuors);
            sqLiteDatabase.execSQL(createTableLimitData);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //thuc hien viec upgrade
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS managerdays");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS manager3g");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS limitdata");
            onCreate(sqLiteDatabase);
        }

        @Override
        public synchronized void close() {
            super.close();
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
        }

        @Override
        public SQLiteDatabase getWritableDatabase() {
            return super.getWritableDatabase();
        }

        @Override
        public SQLiteDatabase getReadableDatabase() {
            return super.getReadableDatabase();
        }

        @Override
        public void onConfigure(SQLiteDatabase db) {
            db.enableWriteAheadLogging();
            super.onConfigure(db);
        }
    }

    public void close() {
        database.close();
    }


    //B5 xay dung phung thuc lam viec voi db
    public void insertManager3g(int UID, double data) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("UID", UID);
        contentValues.put("DATA", data);
        database.insertOrThrow(TB_TOTAL, null, contentValues);

    }

    /* public void updateManager3g(int UID,double data){
         ContentValues values=new ContentValues();
         values.put("DATA",data);
         database.update(TB_TOTAL,values,"UID="+UID,null);
     }*/
    //update
    public void updateManager3g(int UID, double data) {



        ContentValues values = new ContentValues();
        values.put("DATA", data);
        database.update(TB_TOTAL, values, "UID=" + UID, null);
        // do some work with the cursor here.


    }

    public void delete(int uid) {
        database.delete(TB_TOTAL, "UID= " + uid, null);
    }

    public boolean constain(int UID) {
        String[] tableColumns = new String[]{
                "UID"
        };
        String whereClause = "UID = ? ";
        String[] whereArgs = new String[]{
                String.valueOf(UID)
        };
        Cursor cursor = null;
        boolean check = false;

        try {
            cursor = database.query(TB_TOTAL, tableColumns, whereClause, whereArgs, null, null, null);
            if (cursor.getCount() >= 1) {
                check = true;
            }
            // do some work with the cursor here.
        } finally {
            // this gets called even if there is an exception somewhere above
            if (cursor != null)
                cursor.close();
        }

        return check;
    }

    public double dataUID(int UID) {
        String[] tableColumns = new String[]{
                "DATA"
        };
        String whereClause = "UID = ? ";
        String[] whereArgs = new String[]{
                String.valueOf(UID)
        };
        Cursor cursor = null;
        double data = 0;
        try {
            cursor = database.query(TB_TOTAL, tableColumns, whereClause, whereArgs, null, null, null);
            cursor.moveToFirst();
            data = cursor.getDouble(0);
            // do some work with the cursor here.
        } finally {
            // this gets called even if there is an exception somewhere above
            if (cursor != null)
                cursor.close();
        }

        return data;
    }

    public Cursor getListManager3g() {
        return database.query(TB_TOTAL, null, null, null, null, null, null);
    }

    public void insertManagerDays(String date, double data){
        ContentValues contentValues = new ContentValues();
        contentValues.put("DATE", date);
        contentValues.put("DATA", data);
        database.insertOrThrow(TB_DAYS, null, contentValues);
    }
    public void updateManagerDays(String date, double data){
        String whereClause = "DATE = ? ";
        String[] whereArgs = new String[]{
                date
        };
        ContentValues values = new ContentValues();
        values.put("DATA", data);
        database.update(TB_DAYS, values,whereClause, whereArgs);
        // do some work with the cursor here.

    }

    public boolean constainDate(String date) {
        String[] tableColumns = new String[]{
                "DATE"
        };
        String whereClause = "DATE = ? ";
        String[] whereArgs = new String[]{
                date
        };
        Cursor cursor = null;
        boolean check = false;

        try {
            cursor = database.query(TB_DAYS, tableColumns, whereClause, whereArgs, null, null, null);
            if (cursor.getCount() >= 1) {
                check = true;
            }
            // do some work with the cursor here.
        } finally {
            // this gets called even if there is an exception somewhere above
            if (cursor != null)
                cursor.close();
        }

        return check;
    }

    public double getDataDays(String date){
        String[] tableColumns = new String[]{
                "DATA"
        };
        String whereClause = "DATE = ? ";
        String[] whereArgs = new String[]{
                String.valueOf(date)
        };
        Cursor cursor = null;
        double data = 0;
        try {
            cursor = database.query(TB_DAYS, tableColumns, whereClause, whereArgs, null, null, null);
            cursor.moveToFirst();
            data = cursor.getDouble(0);
            // do some work with the cursor here.
        } finally {
            // this gets called even if there is an exception somewhere above
            if (cursor != null)
                cursor.close();
        }

        return data;

    }

    public Cursor getListManagerDays(){
        return database.query(TB_DAYS, null, null, null, null, null, null);

    }
    public double totalLimitedDays(String firtday,String lastday){
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        String[] tableColumns = new String[]{
                "DATE","DATA"
        };

        Cursor cursor = null;
        double data = 0;
        try {
            Date firtdate=formatter.parse(firtday);
            Date lastdate=formatter.parse(lastday);
            cursor = database.query(TB_DAYS, tableColumns, null, null, null, null, null);
            Log.d("count now",String.valueOf(cursor.getCount()));

            if (cursor.getCount()==0)
                return data;
            while (cursor.moveToNext()){
                Log.d("day now",cursor.getString(0));
                String date=cursor.getString(0);
                if (formatter.parse(date).before(lastdate)||
                        formatter.parse(date).equals(lastdate)&&
                        formatter.parse(date).after(firtdate)||
                        formatter.parse(date).equals(firtdate)){
                    data+=cursor.getDouble(1);
                }


            }
            // do some work with the cursor here.
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        finally {
            // this gets called even if there is an exception somewhere above
            if (cursor != null)
                cursor.close();
        }
        Log.d("data1",String.valueOf(data));
        return data;

    }
// table limitdata
    public Cursor getLimitData(){
        String[] tableColumns = new String[]{
                "START","DATA"
        };

        return database.query(TB_LIMITDATA, tableColumns, null, null, null, null, null);
    }

    public void insertTableLimit(String start,Double data){
        ContentValues contentValues = new ContentValues();
        contentValues.put("START", start);
        contentValues.put("DATA", data);
        database.insertOrThrow(TB_LIMITDATA, null, contentValues);
    }

    public void updateTableLimit(String start,Double data){
        ContentValues values = new ContentValues();
        values.put("START", start);
        values.put("DATA", data);
        database.update(TB_LIMITDATA, values,null, null );
    }
}
