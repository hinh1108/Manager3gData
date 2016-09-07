package com.app.hinh.manager3gdata.adapter;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.hinh.manager3gdata.R;
import com.app.hinh.manager3gdata.util.BlockUtils;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends BaseAdapter {

    private Activity mInstance;
    private List<PackageInfo> mInstalledList = null;
    private ArrayList<String> mCheckedList = null;
    private LayoutInflater layoutInflater;

    public AppListAdapter(Activity instance, int item_istall_apilication, List<PackageInfo> installedList, ArrayList<String> checkedList) {
        mInstance = instance;

        mInstalledList = installedList;
        mCheckedList = checkedList;
    }

    public ArrayList<String> getCheckedList() {
        return mCheckedList;
    }

    @Override
    public int getCount() {
        return mInstalledList.size();
    }

    @Override
    public PackageInfo getItem(int arg0) {
        return mInstalledList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(mInstance).inflate(R.layout.item_istall_apilication, null);

            holder = new ViewHolder();
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.check);
            holder.textView = (TextView) convertView.findViewById(R.id.tvAppName);
            holder.imageView = (ImageView) convertView.findViewById(R.id.iconApp);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final PackageInfo packageInfo = mInstalledList.get(position);
        holder.textView.setText(packageInfo.applicationInfo.loadLabel(mInstance.getPackageManager()).toString());

        String a=packageInfo.applicationInfo.loadLabel(mInstance.getPackageManager()).toString();
        // thay doi kich thuoc 1 lan;
        final int iconSize = Math.round(32*mInstance.getResources().getDisplayMetrics().density);
        //Thiết lập Drawables (nếu có) để xuất hiện bên trái của, trên, bên phải, và bên dưới văn bản.

        //app.icon;
        Bitmap bitmap=Bitmap.createScaledBitmap(
                ((BitmapDrawable) packageInfo.applicationInfo.loadIcon(mInstance.getPackageManager())).getBitmap(), iconSize, iconSize, true);

        holder.imageView.setImageBitmap( bitmap);

        //holder.imageView.setImageResource(packageInfo.applicationInfo.icon);
        //Drawable drawable = packageInfo.applicationInfo.loadIcon(mInstance.getPackageManager());
        //holder.imageView.setBackgroundDrawable(drawable);



        
        if (contains(mCheckedList, packageInfo)) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (contains(mCheckedList, packageInfo)) {
                    remove(mCheckedList, packageInfo);
                } else {
                    mCheckedList.add(packageInfo.packageName);
                }

                BlockUtils.save(mInstance, mCheckedList);

                notifyDataSetChanged();
            }
        });

        return convertView;
    }


    private boolean contains(ArrayList<String> list, PackageInfo item) {
        if (list == null || item == null) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).equals(item.packageName)) {
                return true;
            }
        }

        return false;
    }

    private void remove(ArrayList<String> list, PackageInfo item) {
        if (list == null || item == null) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).equals(item.packageName)) {
                list.remove(i);
                break;
            }
        }
    }
    class ViewHolder {
        public CheckBox checkBox;
        public TextView textView;
        public ImageView imageView;
    }
}