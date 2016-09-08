package com.app.hinh.smart3g.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Dung-DamDe-DeTien on 22/08/2016.
 */
public class TopActivityUtils {
    private static boolean DEBUG = true;

    private static String TAG = "TopActivityUtils";

    public static ComponentName getTopActivityUtils(Context context, ActivityManager activityManager){
        if(Build.VERSION.SDK_INT >=21){
            String topApp =  getTopPackageOnAndroidLPlus(context);
            String topActivity = getActivityByPackageName(context, topApp);

            if(!TextUtils.isEmpty(topApp) && !TextUtils.isEmpty(topActivity)){
                return new ComponentName(topApp, topActivity);
            }

        }else {
            List<ActivityManager.RunningTaskInfo> listTask;
            try{
                listTask  = activityManager.getRunningTasks(1);
            }catch (final Exception e){
                if(DEBUG){
                    Log.d(TAG,"Get Running tasks:", e);
                }
                return null;
            }
            if(listTask != null && listTask.size() >0){
                final ComponentName topActivityCom = listTask.get(0).topActivity;
                if(topActivityCom == null){
                    Log.d(TAG, "Top component null");
                    return null;
                }
                return topActivityCom;
            }
        }
        return null;
    }
    public static String getActivityByPackageName(Context context, String pakageName){
        if(TextUtils.isEmpty(pakageName)){
            return null;
        }
        String activitiName = null;
        PackageManager packageManager = context.getPackageManager();

        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");

        List<ResolveInfo> list;

        try{
            list = packageManager.queryIntentActivities(intent, 0);
        }catch (final Exception e){
            list = new ArrayList<>(1);
        }
        if(list != null && list.size() >0){
            for(final  ResolveInfo resolveInfo : list){
                try {
                    final String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
                    final String activity = resolveInfo.activityInfo.name;
                }catch (Exception e){

                }
            }
        }
        if(TextUtils.isEmpty(activitiName)){
            try{
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(pakageName, 0);
                Intent resovleInten = new Intent(Intent.ACTION_MAIN, null);
                resovleInten.setPackage(packageInfo.packageName);
                resovleInten.addCategory(Intent.CATEGORY_LAUNCHER);
                List<ResolveInfo> resList = context.getPackageManager().queryIntentActivities(resovleInten, 0);
                ResolveInfo info = resList.iterator().next();
                if(info != null){
                    activitiName = info.activityInfo.name;
                }
            }catch (Exception e){
                e.printStackTrace();
                activitiName = pakageName;
            }
        }
        return activitiName;
    }

    @SuppressLint("NewApi")
    public static boolean isStatAccessPermissionSet(Context context){
        if(Build.VERSION.SDK_INT >= 21){
            try {
                PackageManager packageMgr = context.getPackageManager();
                ApplicationInfo info = packageMgr.getApplicationInfo(context.getPackageName(), 0);

                AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

                int mode = appOps.checkOpNoThrow("android:get_usage_stats", info.uid, info.packageName);
                return mode == AppOpsManager.MODE_ALLOWED;
            } catch(Exception e) {
                //ignore
            }
            return  false;
        }else {
            return true;
        }
    }
    private static String getTopPackageOnAndroidLPlus(Context context) {

        if (Build.VERSION.SDK_INT >= 22) {

            // 从Android5.1的某个小版本开始，getRunningAppProcesses()只返回自己的进程
            // 所以>= Android5.1(22)的时候，直接使用UsageStats方案获取，但是某些手机可能移除了UsageStats这套逻辑（如LG、魅族），使用这种方案就获取失败了，这个时候，再去试试getRunningAppProcesses()，能返回就用，返回为null就算了

            String pkgName = getTopPackageByUsageStats(context);
            if (!TextUtils.isEmpty(pkgName)) {
                return pkgName;
            }

            pkgName = getTopPackageByRunningAppProcesses(context);
            if (!TextUtils.isEmpty(pkgName)) {
                return pkgName;
            }
        } else {

            //在小于Android5.1时，原则上来讲，Google是允许使用getRunningAppProcesses()返回正在执行进程列表的，但是不排除某些手机改了这个api（如miui7某些版本，用的是Android5.0.2，很有可能用了Android6的部分代码），这个时候，再去试试UsageStats方案，能返回就用，返回null就算了
            String pkgName = getTopPackageByRunningAppProcesses(context);
            if (!TextUtils.isEmpty(pkgName)) {
                return pkgName;
            }

            pkgName = getTopPackageByUsageStats(context);
            if (!TextUtils.isEmpty(pkgName)) {
                return pkgName;
            }
        }

        return "";
    }
    private static String getTopPackageByRunningAppProcesses(Context c){
        if (c == null) {
            throw new IllegalArgumentException("context can not be null when call getTopPackageByRunningAppProcesses(...)");
        }
        ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
        if (appList != null) {
            for (ActivityManager.RunningAppProcessInfo running : appList) {

                if (running.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (running.pkgList != null && running.pkgList.length > 0) {
                        if (DEBUG) {
                            Log.d(TAG, "getTopPackageByRunningAppProcesses() -> " + running.pkgList[0]);
                        }
                        return running.pkgList[0];
                    }
                }
            }
        }
        if (DEBUG) {
            Log.d(TAG, "getTopPackageByRunningAppProcesses() -> null");
        }
        return "";
    }
    private static List getUsageStatsList(Context context){
        try{
            Class clazz = Class.forName("android.app.usage.UsageStatsManager");
            Object receiver = context.getSystemService("usagestats");
            Method method = clazz.getMethod("queryUsageStats", new Class[]{Integer.TYPE, Long.TYPE, Long.TYPE});
            long currentTime = System.currentTimeMillis();

            List localList = (List) method.invoke(receiver, new Object[]{Integer.valueOf(0), currentTime - 30 * 60 * 1000, currentTime});

            return localList;
        }catch (Exception e){

        }
        return null;
    }
    private static String sLastTopApp;
    public static String getTopPackageByUsageStats(Context context){
        if(context ==null){
            throw new IllegalArgumentException("context can not be null when call getTopPackageByUsageStats(...)");
        }
        List dataList = getUsageStatsList(context);

        if (dataList == null || dataList.isEmpty()) {
            if (DEBUG) {
                Log.d(TAG, "return sLastTopApp:" + sLastTopApp);
            }
            return sLastTopApp;
        }

        try {
            Class clz = Class.forName("android.app.usage.UsageStats");
            if (clz != null) {
                final Method med = clz.getMethod("getLastTimeUsed");
                if (med != null) {
                    Object latestObj = Collections.max(dataList, new Comparator() {
                        @Override
                        public int compare(Object lhs, Object rhs) {
                            try {
                                Long stampL = (Long) med.invoke(lhs);
                                Long stampR = (Long) med.invoke(rhs);
                                // 细节：查看Collections.max源码可知，compare(lhs, rhs)返回非负数时, lhs为较大值。
                                return stampL >= stampR ? 1 : -1;
                            } catch (IllegalAccessException e) {
                                // ignore
                            } catch (InvocationTargetException e) {
                                // ignore
                            }
                            return 0;
                        }
                    });

                    Object result = invokeMethod("android.app.usage.UsageStats", "getPackageName", latestObj);
                    if (result != null) {
                        if (DEBUG) {
                            Log.d(TAG, "getTopPackageByUsageStats() -> " + result.toString());
                        }
                        if (!TextUtils.isEmpty(result.toString())) {
                            sLastTopApp = result.toString();
                        }
                        return sLastTopApp;
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            // ignore
        } catch (ClassNotFoundException e1) {
            // ignore
        }

        return "";
    }
    private static Object invokeMethod(String className, String methodName, Object param1){

        try {
            Class clz = Class.forName(className);
            if (clz != null) {
                final Method med = clz.getMethod(methodName);
                if (med != null) {
                    return med.invoke(param1);
                }
            }
        } catch (NoSuchMethodException e) {
            // ignore
        } catch (ClassNotFoundException e1) {
            // ignore
        } catch (IllegalAccessException e) {
            // ignore
        } catch (InvocationTargetException e) {
            // ignore
        }

        return null;
    }
}
