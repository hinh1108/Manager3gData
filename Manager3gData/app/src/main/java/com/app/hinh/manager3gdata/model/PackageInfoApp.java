package com.app.hinh.manager3gdata.model;

import android.content.pm.PackageInfo;

/**
 * Created by hinh1 on 9/7/2016.
 */
public class PackageInfoApp extends PackageInfo{
    private double data=0;
    public PackageInfoApp(double data) {
        super();
        this.data=data;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }
}
