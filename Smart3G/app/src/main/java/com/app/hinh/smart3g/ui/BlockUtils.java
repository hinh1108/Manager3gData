package com.app.hinh.smart3g.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dung-DamDe-DeTien on 22/08/2016.
 */
public class BlockUtils {
    public static boolean isBlockSeviceRunning(Activity instance, Class<?> serviceClass){
        return isBlockSeviceRunning(instance, serviceClass);
    }
    private static boolean isMyServiceRunning(Activity instance, Class<?> seviceClass){
        ActivityManager manager = (ActivityManager)instance.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo sevice : manager.getRunningServices(Integer.MAX_VALUE)){
            if(seviceClass.getName().equals(sevice.service.getClassName())){
                return true;
            }
        }
        return false;
    }
    public static ArrayList<String> getBlockList(Context context){
        ArrayList<String> list = new ArrayList<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set = sharedPreferences.getStringSet("applist", null);
        if(set != null){
            list = new ArrayList<>(set);
        }
        return list;
    }
    public static void save(Context context, ArrayList<String> list) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> set = new HashSet<>(list);
        editor.putStringSet("applist", set);
        editor.commit();
    }
}
