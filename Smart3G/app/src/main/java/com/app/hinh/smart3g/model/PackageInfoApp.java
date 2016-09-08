package com.app.hinh.smart3g.model;

import android.content.pm.PackageInfo;

/**
 * Created by hinh1 on 9/7/2016.
 */
public class PackageInfoApp extends PackageInfo{
    private double data=0;
    private PackageInfo packageInfo;
    public PackageInfoApp(PackageInfo packageInfo, double data) {
        super();
        this.data=data;
        this.packageInfo=packageInfo;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }
}
