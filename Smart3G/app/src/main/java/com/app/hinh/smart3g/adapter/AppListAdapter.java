package com.app.hinh.smart3g.adapter;

import android.content.Context;
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

import com.app.hinh.smart3g.R;
import com.app.hinh.smart3g.model.ApplicationInforNew;
import com.app.hinh.smart3g.ui.BlockUtils;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends BaseAdapter {

    private Context mInstance;
    private List<ApplicationInforNew> mInstalledList = null;
    private ArrayList<String> mCheckedList = null;
    private LayoutInflater layoutInflater;
    private double dataMB=0;
    private double dataKB=0;
    public AppListAdapter(Context instance, int item_istall_apilication, List<ApplicationInforNew> installedList, ArrayList<String> checkedList) {
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
            holder.tvData=(TextView) convertView.findViewById((R.id.tvData));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ApplicationInforNew applicationInforNew = mInstalledList.get(position);
        holder.textView.setText(applicationInforNew.getApplicationInfo().loadLabel(mInstance.getPackageManager()).toString());

        String a=applicationInforNew.getApplicationInfo().loadLabel(mInstance.getPackageManager()).toString();
        // thay doi kich thuoc 1 lan;
        final int iconSize = Math.round(32*mInstance.getResources().getDisplayMetrics().density);
        //Thiết lập Drawables (nếu có) để xuất hiện bên trái của, trên, bên phải, và bên dưới văn bản.

        //app.icon;
        Bitmap bitmap=Bitmap.createScaledBitmap(
                ((BitmapDrawable) applicationInforNew.getApplicationInfo().loadIcon(mInstance.getPackageManager())).getBitmap(), iconSize, iconSize, true);

        holder.imageView.setImageBitmap( bitmap);
        if (applicationInforNew.getData()/(1024*1024)>=1){
            dataMB=Math.round((applicationInforNew.getData()*100)/(1024*1024))/100;
            holder.tvData.setText(String.valueOf(dataMB)+" MB");

        }
        else {
            dataKB=Math.round(applicationInforNew.getData()/(1024));
            holder.tvData.setText(String.valueOf(dataKB)+" KB");

        }

        //holder.imageView.setImageResource(packageInfo.applicationInfo.icon);
        //Drawable drawable = packageInfo.applicationInfo.loadIcon(mInstance.getPackageManager());
        //holder.imageView.setBackgroundDrawable(drawable);



        
        if (contains(mCheckedList, applicationInforNew)) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
/*
       convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (contains(mCheckedList, packageInfoApp)) {
                    remove(mCheckedList, packageInfoApp);
                } else {
                    mCheckedList.add(packageInfoApp.getPackageInfo().packageName);
                }

                BlockUtils.save(mInstance, mCheckedList);

                notifyDataSetChanged();
            }
        });
*/

        return convertView;
    }

    public void updateCheckedList(ApplicationInforNew applicationInforNew){
        if (contains(mCheckedList, applicationInforNew)) {
            remove(mCheckedList, applicationInforNew);
        } else {
            mCheckedList.add(applicationInforNew.getApplicationInfo().packageName);
        }

        BlockUtils.save(mInstance, mCheckedList);

        notifyDataSetChanged();
    }

    private boolean contains(ArrayList<String> list, ApplicationInforNew item) {
        if (list == null || item == null) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).equals(item.getApplicationInfo().packageName)) {
                return true;
            }
        }

        return false;
    }

    private void remove(ArrayList<String> list, ApplicationInforNew item) {
        if (list == null || item == null) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).equals(item.getApplicationInfo().packageName)) {
                list.remove(i);
                break;
            }
        }
    }
    class ViewHolder {
        public CheckBox checkBox;
        public TextView textView;
        public ImageView imageView;
        public TextView tvData;
    }
}