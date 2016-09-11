package com.app.hinh.smart3g.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.app.hinh.smart3g.database.DatabaseManager;

/**
 * Created by hinh1 on 8/24/2016.
 */
public class TotalDataService extends Service {
    private static final int delayMillis = 5000;

    private double tx;
    private double rx;
    private Handler mHandler;
    private double[] firtdata;
    private double[] firtDataMobile;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firtdata=new double[size()];
        firtDataMobile=new double[size()];
        mHandler = new Handler();
        addFirtData();
        mHandler.postDelayed(mRunnable, delayMillis);

        //mHandler.postDelayed(mRunnable, delayMillis);

    }
    private double firt=0;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {


            updateData();
            mHandler.postDelayed(mRunnable, delayMillis);



        }
    };
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        updateData();
        DatabaseManager.getDatabaseManager(TotalDataService.this).close();

        super.onDestroy();
    }
    public void addFirtData(){
        int uid;
        Cursor cursor = null;
        try {
            cursor=DatabaseManager.getDatabaseManager(TotalDataService.this).getListManager3g();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                uid = cursor.getInt(0);
                firtdata[i]=totalData(uid);
                firtDataMobile[i]=cursor.getDouble(1);
            }
            // do some work with the cursor here.
        } finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }
        //Cursor cursor=DatabaseManager.getDatabaseManager(TotalDataService.this).getListManager3g();

    }
    public double totalData(int uid){


            tx = TrafficStats.getUidTxBytes(uid);
            rx = TrafficStats.getUidRxBytes(uid);

            return tx+rx;




    }

    public int size(){
        int count=0;

        Cursor cursor = null;
        try {
            cursor=DatabaseManager.getDatabaseManager(TotalDataService.this).getListManager3g();
            Log.d("FUCK",String.valueOf(cursor.getCount()));
            count=cursor.getCount();

            // do some work with the cursor here.
        } finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }

        return count;
    }
    public void updateData(){
        Cursor cursor = null;
        try {
            cursor =DatabaseManager.getDatabaseManager(TotalDataService.this).getListManager3g();
            int uid;
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                uid = cursor.getInt(0);
                firt=firtdata[i];
                DatabaseManager.getDatabaseManager(TotalDataService.this).updateManager3g(uid,totalData(uid)-firt+firtDataMobile[i]);
                //Log.d("AAAAAA "+String.valueOf(i),String.valueOf(firtdata[i]-firt));
            }

            cursor=DatabaseManager.getDatabaseManager(TotalDataService.this).getListManager3g();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                Log.d("AAAAAA "+String.valueOf(i),String.valueOf(cursor.getDouble(1)));
            }
            // do some work with the cursor here.
        } finally {
            // this gets called even if there is an exception somewhere above
            if(cursor != null)
                cursor.close();
        }



    }
 /* public void updateManager3g(int UID,double data){
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
 */
}
