package com.app.hinh.smart3g.ui;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.app.hinh.smart3g.R;
import com.app.hinh.smart3g.adapter.ViewPagerAdapter;
import com.app.hinh.smart3g.color.ColorRandomizer;
import com.app.hinh.smart3g.database.DatabaseManager;
import com.app.hinh.smart3g.fragment.BaseFragment;
import com.app.hinh.smart3g.fragment.ConfigurationFragment;
import com.app.hinh.smart3g.fragment.ListViewFragment;
import com.app.hinh.smart3g.layout.TabsLayout;
import com.app.hinh.smart3g.model.ApplicationInforNew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.noties.scrollable.CanScrollVerticallyDelegate;
import ru.noties.scrollable.OnFlingOverListener;
import ru.noties.scrollable.OnScrollChangedListener;
import ru.noties.scrollable.ScrollableLayout;

public class MainActivity extends BaseActivity {

    private static final String ARG_LAST_SCROLL_Y = "arg.LastScrollY";

    private ScrollableLayout mScrollableLayout;
    private DatabaseManager databaseManager;
    private  List<PackageInfo> appList;
    private List<ApplicationInfo> applists;
    private List<ApplicationInforNew> installedList;
    private PackageManager packageManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View header = findViewById(R.id.header);
        final TabsLayout tabs = findView(R.id.tabs);

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


    }

    //


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


}
