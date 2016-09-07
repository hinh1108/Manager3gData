package com.app.hinh.manager3gdata.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.hinh.manager3gdata.R;
import com.app.hinh.manager3gdata.adapter.AppListAdapter;
import com.app.hinh.manager3gdata.database.DatabaseManager;
import com.app.hinh.manager3gdata.service.CoreSevice;
import com.app.hinh.manager3gdata.util.BlockUtils;
import com.app.hinh.manager3gdata.util.HeaderView;
import com.app.hinh.manager3gdata.util.TopActivityUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener  {


    @Bind(R.id.toolbar_header_view)
    protected HeaderView toolbarHeaderView;

    @Bind(R.id.float_header_view)
    protected HeaderView floatHeaderView;

    @Bind(R.id.appbar)
    protected AppBarLayout appBarLayout;

    @Bind(R.id.toolbar)
    protected Toolbar toolbar;

    private boolean isHideToolbarView = false;

    private TextView tvSupported;
    private TextView tvDataUsageWifi;
    private TextView tvDataUsageMobile;
    private TextView tvDataTotal;
    private ListView lvApplication;
    private long dataUsageTotalLast = 0;
    private AppListAdapter mAdapter = null;
    private static final int REQUEST_SETTING = 1;
    private Button startBlock;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        databaseManager = new DatabaseManager(MainActivity.this);
        initUi();

        startBlock = (Button) findViewById(R.id.btOnOff);
        if (BlockUtils.isBlockServiceRunning(this, CoreSevice.class)) {
            startBlock.setText("TurnOn");
        } else {
            startBlock.setText("TurnOff");
        }
        List<PackageInfo> appList = getPackageManager().getInstalledPackages(0);
        List<PackageInfo> installedList = new ArrayList<PackageInfo>();
        lvApplication = (ListView) findViewById(R.id.lvInstallApplication);
        for (PackageInfo packageInfo : appList) {

            if (!isSystemPackage(packageInfo) && !getApplicationInfo().packageName.equals(packageInfo.packageName)) {
                installedList.add(packageInfo);
                if (!databaseManager.constain(packageInfo.applicationInfo.uid)) {
                    databaseManager.insertManager3g(packageInfo.applicationInfo.uid, 0);

                }
            }
        }
        Cursor cursor = databaseManager.getListManager3g();
        Log.d("SIZE", String.valueOf(cursor.getCount()));
        mAdapter = new AppListAdapter(MainActivity.this, R.layout.item_istall_apilication, installedList, BlockUtils.getBlockList(getApplicationContext()));
        lvApplication.setAdapter(mAdapter);


    }


    //
    private boolean isSystemPackage(PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    private void initUi() {
        appBarLayout.addOnOffsetChangedListener(this);

        //toolbarHeaderView.bindTo("Larry Page", "Last seen today at 7.00PM");
        //floatHeaderView.bindTo("Larry Page", "Last seen today at 7.00PM");
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        if (percentage == 1f && isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;

        } else if (percentage < 1f && !isHideToolbarView) {
            toolbarHeaderView.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }
    public void onClicked(View view){
       /* if (BlockUtils.isBlockServiceRunning(this,
                CoreSevice.class)) {
            Intent intent = new Intent();
            intent.setClass(this, CoreSevice.class);
            this.stopService(intent);
            startBlock.setText("TurnOff");
        } else {

            if (!TopActivityUtils.isStatAccessPermissionSet(this)) {*/
                showDialog();
            //} else {
                Intent intent = new Intent();
                intent.setClass(this, CoreSevice.class);
                this.startService(intent);
                //startBlock.setText("TurnOn");
       /*     }
        }*/
    }
    private void showDialog(){
        new AlertDialog.Builder(this).setTitle("NOTE")
                .setMessage(" Android 5.0 trở lên không cho phép quyền truy cập ứng dụng mời bạn vào cài đặt thiết lập lại quyền")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            startActivityForResult(new Intent("android.settings.USAGE_ACCESS_SETTINGS"), REQUEST_SETTING);
                        } catch (Exception e) {
                            dialogInterface.dismiss();
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SETTING) {
            if (TopActivityUtils.isStatAccessPermissionSet(this)) {
                //成功开启了权限

                Intent intent = new Intent();
                intent.setClass(this, CoreSevice.class);
                this.startService(intent);
                startBlock.setText("TurnOn");
                Toast.makeText(this, "bat dau blockApp", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Bạn chưa cho phép chúng tôi ko thể làm việc", Toast.LENGTH_LONG).show();
            }
        }
    }
}
