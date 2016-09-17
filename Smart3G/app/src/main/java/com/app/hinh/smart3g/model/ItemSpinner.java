package com.app.hinh.smart3g.model;

/**
 * Created by hinh1 on 9/17/2016.
 */
public class ItemSpinner {
    private String startDays;
    private String endDays;

    public ItemSpinner(String startDays, String endDays) {
        this.startDays = startDays;
        this.endDays = endDays;
    }

    public String getStartDays() {
        return startDays;
    }

    public void setStartDays(String startDays) {
        this.startDays = startDays;
    }

    public String getEndDays() {
        return endDays;
    }

    public void setEndDays(String endDays) {
        this.endDays = endDays;
    }

    @Override
    public String toString() {
        return startDays +" -> "+ endDays ;
    }
}
