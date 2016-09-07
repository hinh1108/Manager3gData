package com.app.hinh.manager3gdata.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.app.hinh.manager3gdata.ui.BlockUtils;
import com.app.hinh.manager3gdata.util.Logger;
import com.app.hinh.manager3gdata.util.TopActivityUtils;
import com.app.hinh.manager3gdata.util.WarringActivity;

import java.util.ArrayList;

/**
 * Created by Dung-DamDe-DeTien on 22/08/2016.
 */
public class CoreSevice extends Service {
    private static final int delayMillis = 1000;

    private Context mCotex;
    private ActivityManager mActivityManganer;

    private Handler mHandler;
    private ArrayList<String> mBlockList = null;

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            com.app.hinh.manager3gdata.util.Logger.getLogger().d("block service is running...");
            mBlockList = BlockUtils.getBlockList(getApplicationContext());
            String packageName = null;
            ComponentName componentName = TopActivityUtils.getTopActivityUtils(mCotex, mActivityManganer);
            if (componentName != null) {
                packageName = componentName.getPackageName();
                Logger.getLogger().i("packageName:" + packageName);
            } else {
                Logger.getLogger().e("getTopActivity Error!");
            }

            if (mBlockList != null && mBlockList.contains(packageName)&& is3g() ) {

                Intent intent = new Intent(getApplicationContext(), WarringActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            mHandler.postDelayed(mRunnable, delayMillis);
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onCreate() {
        super.onCreate();

        mCotex = this;
        mActivityManganer = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, delayMillis);

        Logger.getLogger().i("onCreate");
    }
    public void onDestroy() {

        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();

        Logger.getLogger().i("onDestroy");
    }

    public boolean is3g(){
        ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

        boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();

        if(is3g){
            return true;
        }else {
            return false;
        }
    }
}
