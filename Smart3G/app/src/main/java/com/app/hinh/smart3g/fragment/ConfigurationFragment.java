package com.app.hinh.smart3g.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.hinh.smart3g.R;

import java.util.Calendar;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 28.03.2015.
 */
public class ConfigurationFragment extends BaseFragment {

    public static final String TAG = "tag.ConfigurationFragment";
    private static final String FRICTION_PATTERN = "Current: %sF";
    private TextView tvFritDate;
    private TextView tvLastDate;
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
        View view =inflater.inflate(R.layout.setting_3g, parent, false);
       /* tvFritDate=(TextView)view.findViewById(R.id.firtDate);
        tvLastDate=(TextView)view.findViewById(R.id.lastdate);
        tvLastDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);

            }
        });
        tvFritDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });*/
        return view;
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

    public void showDatePickerDialog(final View view) {
        //b1: thiet lap gia tri khi hien thi datepickerdialog
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        //b2: xay dung date
        DatePickerDialog pickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //tra ve ngay thang nam khi ng dung nhan button set
                if (view.getId()==tvFritDate.getId()){
                    tvFritDate.setText(String.valueOf(day)+"-"+String.valueOf(month)+"-"+String.valueOf(year));

                }
                else
                    tvLastDate.setText(String.valueOf(day)+"-"+String.valueOf(month)+"-"+String.valueOf(year));




            }
        }, year, month, day);
        pickerDialog.show();
    }


}
