package com.app.hinh.smart3g.model;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;

/**
 * Created by hinh1 on 9/7/2016.
 */
public class ApplicationInforNew extends PackageInfo{
    private double data=0;
    private ApplicationInfo applicationInfo;
    public ApplicationInforNew(ApplicationInfo applicationInfo, double data) {
        super();
        this.data=data;
        this.applicationInfo=applicationInfo;
    }

    public ApplicationInfo getApplicationInfo() {
        return this.applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }
}
