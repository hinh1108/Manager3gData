package com.app.hinh.manager3gdata.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BlockUtils {

    /**
     *
     *
     * @param instance
     * @param serviceClass
     */
    public static boolean isBlockServiceRunning(Activity instance, Class<?> serviceClass) {

        return isMyServiceRunning(instance, serviceClass);
    }

    private static boolean isMyServiceRunning(Activity instance, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) instance.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param context
     * @return
     */
    public static ArrayList<String> getBlockList(Context context) {
        ArrayList<String> list = new ArrayList<>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> set = sharedPreferences.getStringSet("applist", null);
        if (set != null) {
            list = new ArrayList<>(set);
        }

        return list;
    }

    /**
     *
     * @param context
     * @param list
     * @return
     */
    public static void save(Context context, ArrayList<String> list) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> set = new HashSet<>(list);
        editor.putStringSet("applist", set);
        editor.commit();
    }
}
