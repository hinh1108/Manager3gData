package com.app.hinh.smart3g.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.hinh.smart3g.R;
import com.app.hinh.smart3g.adapter.ViewPagerAdapter;
import com.app.hinh.smart3g.database.DatabaseManager;
import com.app.hinh.smart3g.fragment.BaseFragment;
import com.app.hinh.smart3g.fragment.ListViewFragment;
import com.app.hinh.smart3g.model.ApplicationInforNew;
import com.app.hinh.smart3g.model.ItemSpinner;
import com.app.hinh.smart3g.service.CoreSevice;
import com.app.hinh.smart3g.util.BlockUtils;
import com.app.hinh.smart3g.util.TopActivityUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import ru.noties.scrollable.CanScrollVerticallyDelegate;
import ru.noties.scrollable.OnFlingOverListener;
import ru.noties.scrollable.OnScrollChangedListener;
import ru.noties.scrollable.ScrollableLayout;

public class MainActivity extends BaseActivity {

    private static final String ARG_LAST_SCROLL_Y = "arg.LastScrollY";
    private static final int REQUEST_SETTING = 1;
    private ScrollableLayout mScrollableLayout;
    private DatabaseManager databaseManager;
    private List<ApplicationInfo> applists;
    private List<ApplicationInforNew> installedList;
    private PackageManager packageManager = null;
    private CheckBox startBlock;
    private TextView totalData;
    //private TextView limitdata;
    private ArrayList<ItemSpinner> itemList;
    private ArrayList<String> itemString;
    private Spinner settingdate;
    private ImageView app1,app2,app3;
    private TextView countApp;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemString=new ArrayList<String>();
        itemList=new ArrayList<ItemSpinner>();
        app1=(ImageView)findViewById(R.id.app1);
        app2=(ImageView)findViewById(R.id.app2);
        app3=(ImageView)findViewById(R.id.app3);
        countApp=(TextView)findViewById(R.id.countApp);
        final View header = findViewById(R.id.header);
        startBlock = (CheckBox) findViewById(R.id.toggel);
        totalData = (TextView) findViewById(R.id.totalData);
        //limitdata=(TextView)findViewById(R.id.limitdata);
        mScrollableLayout = findView(R.id.scrollable_layout);
        packageManager = getPackageManager();
        settingdate=(Spinner)findViewById(R.id.settingdate);
        //list app
        databaseManager = new DatabaseManager(MainActivity.this);
        applists = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
        installedList = new ArrayList<ApplicationInforNew>();

        updateApps();

        countApp.setText(String.valueOf(installedList.size()));
        app1.setImageBitmap(updateTopApp(0));
        app2.setImageBitmap(updateTopApp(1));
        app3.setImageBitmap(updateTopApp(2));


        Cursor cursor = databaseManager.getListManager3g();
        Log.d("SIZE", String.valueOf(cursor.getCount()));

        final ViewPager viewPager = findView(R.id.view_pager);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getResources(), getFragments());
        viewPager.setAdapter(adapter);


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


                header.setTranslationY(y / 2);
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
        if (BlockUtils.isBlockServiceRunning(MainActivity.this, CoreSevice.class)) {
            startBlock.setChecked(true);
        } else {
            startBlock.setChecked(false);
        }
        startBlock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b == true) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, CoreSevice.class);
                    MainActivity.this.startService(intent);
                    if (!TopActivityUtils.isStatAccessPermissionSet(MainActivity.this)) {
                        showDialog();
                    } else {
                        Intent intent1 = new Intent();
                        intent1.setClass(MainActivity.this, CoreSevice.class);
                        MainActivity.this.startService(intent1);
                    }

                } else if (b == false) {
                    Intent intent1 = new Intent();
                    intent1.setClass(MainActivity.this, CoreSevice.class);
                    MainActivity.this.stopService(intent1);

                }
            }

        });
        itemList.add(setDays(0));
        itemList.add(setDays(1));
        itemList.add(setDays(2));
        spinnerAdapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,itemString);
        settingdate.setAdapter(spinnerAdapter);
        //totalData.setText(String.valueOf(databaseManager.totalLimitedDays(itemList.get(settingdate.getSelectedItemPosition()).getStartDays(),itemList.get(settingdate.getSelectedItemPosition()).getEndDays())));
       settingdate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               totalData.setText(String.valueOf(round(databaseManager.totalLimitedDays(itemList.get(i).getStartDays(),itemList.get(i).getEndDays())/(1024*1024*1024),2))+" GB");

           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });
        //limit data
        if(databaseManager.getLimitData().getCount()==0){
            databaseManager.insertTableLimit("true", (double) 1);
            databaseManager.getLimitData().close();
        }
        Cursor cursor1=databaseManager.getLimitData();
        cursor1.moveToFirst();
        //limitdata.setText(String.valueOf(cursor1.getDouble(1))+" GB");
        //setMobileDataEnabled(MainActivity.this,false);

    }

    public  void updateApps(){

        TreeSet<ApplicationInforNew> applicationInforNewTreeSet = new TreeSet<ApplicationInforNew>(new Comparator<ApplicationInforNew>() {
            @Override
            public int compare(ApplicationInforNew a1, ApplicationInforNew a2) {
                return a1.compareTo(a2);
            }
        });
        double data = 0;
        ApplicationInforNew applicationInforNew;
        for (ApplicationInfo applicationInfo : applists) {


            if (!databaseManager.constain(applicationInfo.uid)) {
                data = 0;
                applicationInforNew = new ApplicationInforNew(applicationInfo, data);
                databaseManager.insertManager3g(applicationInfo.uid, data);
                //installedList.add(applicationInforNew);
                applicationInforNewTreeSet.add(applicationInforNew);

            } else {
                data = databaseManager.dataUID(applicationInfo.uid);
                applicationInforNew = new ApplicationInforNew(applicationInfo, data);
                //installedList.add(applicationInforNew);
                applicationInforNewTreeSet.add(applicationInforNew);

            }
        }
        Log.d("size tree set", String.valueOf(applicationInforNewTreeSet.size()));

        sortAppData(applicationInforNewTreeSet);
    }
    public Bitmap updateTopApp(int i){
        //app.icon;
        Bitmap bitmap=Bitmap.createScaledBitmap(
                ((BitmapDrawable) installedList.get(i).getApplicationInfo().loadIcon(MainActivity.this.getPackageManager())).getBitmap(), 30, 30, true);
        return bitmap;
    }
    //round data
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
   /* private void setMobileDataEnabled(Context context, boolean enabled) {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass;
        try {
            conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }*/
    //
    public ItemSpinner setDays(int item) {
        ItemSpinner itemSpinner;
        String begining = "", end = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        Calendar c = Calendar.getInstance(); // Get Calendar Instance
        c.set(Calendar.MONTH,c.get(Calendar.MONTH)-item);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        begining = sdf.format(c.getTime());

        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        end = sdf.format(c.getTime());
        itemSpinner=new ItemSpinner(begining,end);
        itemString.add(itemSpinner.toString());
        Log.d("Limit days", begining + " đến " + end);
        return itemSpinner;
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("NOTE")
                .setMessage("Android 5.0 trở lên không cho phép quyền truy cập ứng dụng mời bạn vào cài đặt thiết lập lại quyền")
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
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
        final List<BaseFragment> list = new ArrayList<>();


        ListViewFragment listViewFragment
                = (ListViewFragment) manager.findFragmentByTag(ListViewFragment.TAG);
        if (listViewFragment == null) {
            listViewFragment = ListViewFragment.newInstance(MainActivity.this, installedList);
        }


        Collections.addAll(list, listViewFragment);

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

    public void sortAppData(TreeSet<ApplicationInforNew> applicationInforNewTreeSet) {
        while (!applicationInforNewTreeSet.isEmpty()) {
            installedList.add(applicationInforNewTreeSet.pollLast());
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemSettingWifi:
                Toast.makeText(MainActivity.this,"Wifi",Toast.LENGTH_LONG).show();
                break;
            case R.id.itemSeacher:
                Toast.makeText(MainActivity.this,"Seacher",Toast.LENGTH_LONG).show();
                break;
            case R.id.itemAbout:
                Toast.makeText(MainActivity.this,"About",Toast.LENGTH_LONG).show();
                break;
            case R.id.itemHelp:
                Toast.makeText(MainActivity.this,"Help",Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
