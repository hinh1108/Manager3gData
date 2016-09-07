package com.app.hinh.manager3gdata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    //B4: xay dung class SQLiteOpenHelper de giup cho viec
    //upgrade, downgrade DB
    //B6: Xadung databese
    public DatabaseManager(Context context){
        OpenHelper helper=new OpenHelper(context);
        database=helper.getWritableDatabase();
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
    }
    //B5 xay dung phung thuc lam viec voi db
    public void insertManager3g(int UID,double data){
        ContentValues contentValues=new ContentValues();
        contentValues.put("UID",UID);
        contentValues.put("DATA",data);
        database.insertOrThrow(TB_TOTAL,null,contentValues);

    }
    public void updateManager3g(int UID,double data){
        ContentValues values=new ContentValues();
        values.put("DATA",data);
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
    public Cursor getListManager3g(){
        return database.query(TB_TOTAL,null,null,null,null,null,null);
    }
}
