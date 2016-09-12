package com.app.hinh.smart3g.model;

import android.content.pm.ApplicationInfo;

/**
 * Created by hinh1 on 9/7/2016.
 */
public class ApplicationInforNew extends ApplicationInfo implements Comparable<ApplicationInforNew>{
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

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int compareTo(ApplicationInforNew applicationInforNew) {
        if (this.getApplicationInfo().uid==applicationInforNew.getApplicationInfo().uid)
            return 0;
        else {
            if (this.getData() > applicationInforNew.getData())
                return 1;
            else
                return -1;
        }
    }
}
