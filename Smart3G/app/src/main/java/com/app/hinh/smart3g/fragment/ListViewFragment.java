package com.app.hinh.smart3g.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.app.hinh.smart3g.R;
import com.app.hinh.smart3g.adapter.AppListAdapter;
import com.app.hinh.smart3g.model.PackageInfoApp;
import com.app.hinh.smart3g.util.BlockUtils;

import java.util.List;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 29.03.2015.
 */
public class ListViewFragment extends BaseFragment {

    public static final String TAG = "tag.ListViewFragment";
    private AppListAdapter mAdapter = null;
    private Context context;
    private List<PackageInfoApp> installedList;
    public static ListViewFragment newInstance(int color,Context context,List<PackageInfoApp> installedList) {
        final Bundle bundle = new Bundle();
        bundle.putInt(ARG_COLOR, color);

        final ListViewFragment fragment = new ListViewFragment(context,installedList);
        fragment.setArguments(bundle);
        return fragment;
    }
    @SuppressLint("ValidFragment")
    public ListViewFragment(Context context,List<PackageInfoApp> installedList) {
        this.installedList=installedList;
        this.context=context;
    }

    public ListViewFragment() {
    }

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle sis) {

        final View view = inflater.inflate(R.layout.fragment_list_view, parent, false);

        mListView = findView(view, R.id.list_view);
        //list app




        mAdapter=new AppListAdapter(context,R.layout.item_istall_apilication, installedList, BlockUtils.getBlockList(context.getApplicationContext()));

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.updateCheckedList(installedList.get(position));
            }
        });

        return view;
    }

    @Override
    public CharSequence getTitle(Resources r) {
        return  r.getString(R.string.fragment_app);
    }

    @Override
    public String getSelfTag() {
        return TAG;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return mListView != null && mListView.canScrollVertically(direction);
    }

    @Override
    public void onFlingOver(int y, long duration) {
        if (mListView != null) {
            mListView.smoothScrollBy(y, (int) duration);
        }
    }
}
