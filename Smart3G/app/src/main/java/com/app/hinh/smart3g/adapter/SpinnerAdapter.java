package com.app.hinh.smart3g.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.hinh.smart3g.model.ItemSpinner;

import java.util.ArrayList;

/**
 * Created by hinh1 on 9/17/2016.
 */
public class SpinnerAdapter extends ArrayAdapter<ItemSpinner>{
    private ArrayList<ItemSpinner> itemSpinners;
    private Context mInstance;

    public SpinnerAdapter(Context context, int resource, ArrayList<ItemSpinner> objects) {
        super(context,resource,objects);
        this.itemSpinners=objects;
        this.mInstance=context;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            TextView label = new TextView(mInstance);
            label.setText(itemSpinners.get(position).toString());
            convertView=label;

        }
        else
            convertView.getTag();

        return convertView;
    }


}
