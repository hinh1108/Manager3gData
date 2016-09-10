package com.app.hinh.smart3g.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.app.hinh.smart3g.R;
import com.app.hinh.smart3g.adapter.ViewPagerAdapter;
import com.app.hinh.smart3g.color.ColorRandomizer;
import com.app.hinh.smart3g.database.DatabaseManager;
import com.app.hinh.smart3g.fragment.BaseFragment;
import com.app.hinh.smart3g.fragment.ConfigurationFragment;
import com.app.hinh.smart3g.fragment.ListViewFragment;
import com.app.hinh.smart3g.layout.TabsLayout;
import com.app.hinh.smart3g.model.ApplicationInforNew;
import com.app.hinh.smart3g.service.CoreSevice;
import com.app.hinh.smart3g.util.BlockUtils;
import com.app.hinh.smart3g.util.TopActivityUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.noties.scrollable.CanScrollVerticallyDelegate;
import ru.noties.scrollable.OnFlingOverListener;
import ru.noties.scrollable.OnScrollChangedListener;
import ru.noties.scrollable.ScrollableLayout;

public class MainActivity extends BaseActivity  {

    private static final String ARG_LAST_SCROLL_Y = "arg.LastScrollY";
    private static final int REQUEST_SETTING = 1;
    private ScrollableLayout mScrollableLayout;
    private DatabaseManager databaseManager;
    private  List<PackageInfo> appList;
    private List<ApplicationInfo> applists;
    private List<ApplicationInforNew> installedList;
    private PackageManager packageManager = null;
    private CheckBox startBlock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View header = findViewById(R.id.header);
        final TabsLayout tabs = findView(R.id.tabs);
        startBlock = (CheckBox)findViewById(R.id.toggel);
        mScrollableLayout = findView(R.id.scrollable_layout);
        mScrollableLayout.setDraggableView(tabs);
        packageManager = getPackageManager();
        //list app
        databaseManager=new DatabaseManager(MainActivity.this);
        applists = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        installedList = new ArrayList<ApplicationInforNew>();
        double data=0;
        ApplicationInforNew applicationInforNew;
        for (ApplicationInfo applicationInfo : applists) {


                if (!databaseManager.constain(applicationInfo.uid)) {
                    data=0;
                    applicationInforNew=new ApplicationInforNew(applicationInfo,data);
                    databaseManager.insertManager3g(applicationInfo.uid, data);
                    installedList.add(applicationInforNew);
                }
                else {
                    data=databaseManager.dataUID(applicationInfo.uid);
                    applicationInforNew=new ApplicationInforNew(applicationInfo,data);
                    installedList.add(applicationInforNew);

            }
        }

        Cursor cursor = databaseManager.getListManager3g();
        Log.d("SIZE", String.valueOf(cursor.getCount()));

        final ViewPager viewPager = findView(R.id.view_pager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getResources(), getFragments());
        viewPager.setAdapter(adapter);

        tabs.setViewPager(viewPager);

        mScrollableLayout.setCanScrollVerticallyDelegate(new CanScrollVerticallyDelegate() {
            @Override
            public boolean canScrollVertically(int direction) {
                return adapter.canScrollVertically(viewPager.getCurrentItem(), direction);
            }
        });
        mScrollableLayout.setOnFlingOverListener(new OnFlingOverListener() {
            @Override
            public void onFlingOver(int y, long duration) {
                adapter.getItem(viewPager.getCurrentItem()).onFlingOver(y, duration);
            }
        });

        mScrollableLayout.setOnScrollChangedListener(new OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int y, int oldY, int maxY) {

                final float tabsTranslationY;
                if (y < maxY) {
                    tabsTranslationY = .0F;
                } else {
                    tabsTranslationY = y - maxY;
                }

                tabs.setTranslationY(tabsTranslationY);

                header.setTranslationY(y/2 );
            }
        });

        if (savedInstanceState != null) {
            final int y = savedInstanceState.getInt(ARG_LAST_SCROLL_Y);
            mScrollableLayout.post(new Runnable() {
                @Override
                public void run() {
                    mScrollableLayout.scrollTo(0, y);
                }
            });
        }
        if(BlockUtils.isBlockServiceRunning(MainActivity.this, CoreSevice.class)){
            startBlock.setChecked(true);
        }else{
            startBlock.setChecked(false);
        }
        startBlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(b == true){
                   Intent intent = new Intent();
                   intent.setClass(MainActivity.this, CoreSevice.class);
                   MainActivity.this.startService(intent);
                   if(!TopActivityUtils.isStatAccessPermissionSet(MainActivity.this)){
                        showDialog();
                   }else {
                       Intent intent1 = new Intent();
                       intent1.setClass(MainActivity.this, CoreSevice.class);
                       MainActivity.this.startService(intent1);
                   }

               }else if(b == false) {
                   Intent intent1 = new Intent();
                   intent1.setClass(MainActivity.this, CoreSevice.class);
                   MainActivity.this.stopService(intent1);

               }
            }

        });

    }

    //

    private void showDialog(){
        new AlertDialog.Builder(this)
                .setTitle("NOTE")
                .setMessage("Android 5.0 trở lên không cho phép quyền truy cập ứng dụng mời bạn vào cài đặt thiết lập lại quyền")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        try {
                            startActivityForResult(new Intent("android.settings.USAGE_ACCESS_SETTINGS"), REQUEST_SETTING);
                        } catch (Exception e) {
                            dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).show();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_LAST_SCROLL_Y, mScrollableLayout.getScrollY());
        super.onSaveInstanceState(outState);
    }

    private List<BaseFragment> getFragments() {

        final FragmentManager manager = getSupportFragmentManager();
        final ColorRandomizer colorRandomizer = new ColorRandomizer(getResources().getIntArray(R.array.fragment_colors));
        final List<BaseFragment> list = new ArrayList<>();


        ListViewFragment listViewFragment
                = (ListViewFragment) manager.findFragmentByTag(ListViewFragment.TAG);
        if (listViewFragment == null) {
            listViewFragment = ListViewFragment.newInstance(colorRandomizer.next(),MainActivity.this,installedList);
        }

        ConfigurationFragment configurationFragment
                = (ConfigurationFragment) manager.findFragmentByTag(ConfigurationFragment.TAG);
        if (configurationFragment == null) {
            configurationFragment = ConfigurationFragment.newInstance(colorRandomizer.next());
        }



        Collections.addAll(list, listViewFragment, configurationFragment);

        return list;
    }

    //check app
    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return applist;
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
