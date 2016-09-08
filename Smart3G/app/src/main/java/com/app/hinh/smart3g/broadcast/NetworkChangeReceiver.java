package com.app.hinh.smart3g.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.app.hinh.smart3g.service.TotalDataService;

/**
 * Created by hinh1 on 8/24/2016.
 */
public class NetworkChangeReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isConnected =
                mobile != null && mobile.isConnectedOrConnecting();
        Intent intent1 = new Intent(context,TotalDataService.class);

        if (isConnected) {

            context.startService(intent1);

            Log.d("Network Available ", "YES");
        } else {
            context.stopService(intent1);
            Log.d("Network Available ", "NO");
        }

    }

}
