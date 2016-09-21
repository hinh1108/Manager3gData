package com.app.hinh.smart3g.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.hinh.smart3g.R;
import com.app.hinh.smart3g.model.ApplicationInforNew;
import com.app.hinh.smart3g.util.BlockUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends BaseAdapter {

    private Context mInstance;
    private List<ApplicationInforNew> mInstalledList = null;
    private ArrayList<String> mCheckedList = null;
    private LayoutInflater layoutInflater;
    private double dataMB=0;
    private double dataKB=0;
    private double maxData=0;
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
    public ApplicationInfo getItem(int arg0) {
        return mInstalledList.get(arg0).getApplicationInfo();
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
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
            holder.drawline=(LinearLayout) convertView.findViewById(R.id.drawLine);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //LinearLayout.LayoutParams layParamsGet= (LinearLayout.LayoutParams) holder.layoutView.getLayoutParams();
        final ApplicationInforNew applicationInforNew = mInstalledList.get(position);
        LinearLayout.LayoutParams params=(LinearLayout.LayoutParams)holder.drawline.getLayoutParams();
        params.weight= (float) (applicationInforNew.getData()/maxData());
        holder.drawline.setLayoutParams(params);

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

            dataMB=round(applicationInforNew.getData()/(1024*1024),2);
            holder.tvData.setText(String.valueOf(dataMB)+" MB");

        }
        else {
            dataKB=round(applicationInforNew.getData()/(1024),2);
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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public double maxData(){
        maxData=mInstalledList.get(0).getData();
        return maxData;
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
        public LinearLayout drawline;
    }
}