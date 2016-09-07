package com.app.hinh.manager3gdata.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;

/**
 * Created by Dung-DamDe-DeTien on 29/07/2016.
 */
public class ApplicationItem {
    private long tx = 0; //tra lai so byte duoc truyen boi UID ke tu khi khoi dong thiet bi;
    private long rx = 0; //tra lai so byte nhan dk boi UID ke tu khi khoi dong thiet bi;

    private long wifi_tx = 0;//tra lai so byte duoc truyen boi UID ke tu khi khoi dong thiet bi qua wifi
    private long wifi_rx = 0;//tra lai so byte nhan dk boi UID ke tu khi khoi dong thiet bi qua wifi;

    private long mobil_tx = 0;//tra lai so byte duoc truyen boi UID ke tu khi khoi dong thiet bi qua mang di dong;
    private long mobil_rx = 0;//tra lai so byte nhan dk boi UID ke tu khi khoi dong thiet bi qua mang di dong;

    private long current_tx = 0;
    private long current_rx = 0;

    private ApplicationInfo app;

    private boolean isMobil = false;

    public ApplicationItem(ApplicationInfo _app) {
        this.app = _app;
        update();
    }
    public void update(){
        long delta_tx = TrafficStats.getUidTxBytes(app.uid) - tx;
        long delta_rx = TrafficStats.getUidRxBytes(app.uid) - rx;

        tx = TrafficStats.getUidTxBytes(app.uid);
        rx = TrafficStats.getUidRxBytes(app.uid);

        current_tx += delta_tx;
        current_rx += delta_rx;

        if(isMobil == true){
            mobil_tx += delta_tx;
            mobil_rx += delta_rx;

        }else{
            wifi_tx += delta_tx;
            wifi_rx += delta_rx;
        }
    }
    public static ApplicationItem create(ApplicationInfo _app){
        long _tx = TrafficStats.getUidTxBytes(_app.uid);
        long _rx = TrafficStats.getUidRxBytes(_app.uid);
        if((_tx + _rx) > 0) return new ApplicationItem(_app);
        return null;
    }
    public int getTotalUsageKb(){return Math.round(tx + rx)/1024;}

    public String getApplicationLabel(PackageManager _packageManager) {
        return _packageManager.getApplicationLabel(app).toString();
    }
    public Drawable getIcon(PackageManager _packageManager) {
        return _packageManager.getApplicationIcon(app);
    }
    public void setMobilTraffic(boolean _isMobil) {
        isMobil = _isMobil;
    }
}
