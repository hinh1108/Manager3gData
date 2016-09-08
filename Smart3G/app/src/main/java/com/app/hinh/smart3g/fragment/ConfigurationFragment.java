package com.app.hinh.smart3g.fragment;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.app.hinh.smart3g.R;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 28.03.2015.
 */
public class ConfigurationFragment extends BaseFragment {

    public static final String TAG = "tag.ConfigurationFragment";
    private static final String FRICTION_PATTERN = "Current: %sF";

    public static ConfigurationFragment newInstance(int color) {
        final Bundle bundle = new Bundle();
        bundle.putInt(ARG_COLOR, color);

        final ConfigurationFragment fragment = new ConfigurationFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private ScrollView mScrollView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle sis) {
        return inflater.inflate(R.layout.setting_3g, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle sis) {
        super.onViewCreated(view, sis);
        mScrollView=(ScrollView)findView(view,R.id.setting3g);


    }

    @Override
    public boolean canScrollVertically(int direction) {
        return mScrollView != null && mScrollView.canScrollVertically(direction);
    }

    @Override
    public CharSequence getTitle(Resources r) {
        return r.getString(R.string.fragment_setting3g);
    }

    @Override
    public String getSelfTag() {
        return TAG;
    }

    @Override
    public void onFlingOver(int y, long duration) {
        if (mScrollView != null) {
            mScrollView.smoothScrollBy(0, y);
        }
    }



}
