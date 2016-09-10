package com.app.hinh.smart3g.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

/**
 * Created by hinh1 on 7/23/2016.
 */
public class DatabaseManager {
    //dinh nghia thong tin database
    private static final String DB_NAME="manager3g";
    private static final String TB_DAYS="managerdays";
    private static final String TB_TOTAL="manager3g";
    private static final int DB_VERSION=1;
    private SQLiteDatabase database;
    private static HandlerThread hthread = null;
    private static Handler handler = null;
    //B4: xay dung class SQLiteOpenHelper de giup cho viec
    //upgrade, downgrade DB
    //B6: Xadung databese
    public DatabaseManager(Context context){
        OpenHelper helper=new OpenHelper(context);
        database=helper.getWritableDatabase();
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

    private static DatabaseManager databaseManager=null;
    public static  DatabaseManager getDatabaseManager(Context context) {
        if (databaseManager == null)
            databaseManager = new DatabaseManager(context.getApplicationContext());
        return databaseManager;
    }
    public class OpenHelper extends SQLiteOpenHelper{

        public OpenHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String creadTableDays="CREATE TABLE IF NOT EXISTS managerdays(UID INT,DAY TEXT,DATA DOUBLE)";
            String createTableHuors="CREATE TABLE IF NOT EXISTS manager3g(UID INT, DATA DOUBLE)";
            sqLiteDatabase.execSQL(creadTableDays);
            sqLiteDatabase.execSQL(createTableHuors);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //thuc hien viec upgrade
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS managerdays");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS manager3g");
            onCreate(sqLiteDatabase);
        }

        @Override
        public synchronized void close() {
            super.close();
        }
    }

    public void close(){
        database.close();
    }
    //B5 xay dung phung thuc lam viec voi db
    public void insertManager3g(int UID,double data){
        ContentValues contentValues=new ContentValues();
        contentValues.put("UID",UID);
        contentValues.put("DATA",data);
        database.insertOrThrow(TB_TOTAL,null,contentValues);

    }
   /* public void updateManager3g(int UID,double data){
        ContentValues values=new ContentValues();
        values.put("DATA",data);
        database.update(TB_TOTAL,values,"UID="+UID,null);
    }*/
    //update
    public void updateManager3g(int UID,double data){

        String whereClause = "UID = ? ";
        String[] whereArgs = new String[] {
                String.valueOf(UID)
        };
        Cursor cursor=database.query(TB_TOTAL,null,whereClause,whereArgs,null,null,null);
        cursor.moveToFirst();
        double datafirt=cursor.getDouble(1);

        ContentValues values=new ContentValues();
        values.put("DATA",data+datafirt);
        database.update(TB_TOTAL,values,"UID="+UID,null);
    }
    public void delete(int uid){
        database.delete(TB_TOTAL,"UID= "+uid,null);
    }
    public boolean constain(int UID){
        String[] tableColumns = new String[] {
                "UID"
        };
        String whereClause = "UID = ? ";
        String[] whereArgs = new String[] {
                String.valueOf(UID)
        };
        Cursor cursor=database.query(TB_TOTAL,tableColumns,whereClause,whereArgs,null,null,null);
        boolean check=false;
        if (cursor.getCount()>=1){
            check=true;
        }
        return check;
    }
    public double dataUID(int UID){
        String[] tableColumns = new String[] {
                "DATA"
        };
        String whereClause = "UID = ? ";
        String[] whereArgs = new String[] {
                String.valueOf(UID)
        };
        Cursor cursor=database.query(TB_TOTAL,tableColumns,whereClause,whereArgs,null,null,null);
        cursor.moveToFirst();
        double data=cursor.getDouble(0);
        return data;
    }
    public Cursor getListManager3g(){
        return database.query(TB_TOTAL,null,null,null,null,null,null);
    }

}
