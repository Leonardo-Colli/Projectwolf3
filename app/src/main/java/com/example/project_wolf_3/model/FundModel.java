package com.example.project_wolf_3.model;

import java.util.Date;

public class FundModel {

    private double roi_vol;
    private double roi_per;
    private double initial_amount;
    private double final_amount;
    private Date date;

    private FundModel() {}

    private FundModel(double roi_vol, double roi_per, double initial_amount, double final_amount, Date date){
        this.roi_vol = roi_vol;
        this.roi_per = roi_per;
        this.initial_amount = initial_amount;
        this.final_amount = final_amount;
        this.date = date;
    }

    public double getRoi_vol(){
        return roi_vol;
    };

    public void setRoi_vol(double roi_vol) {
        this.roi_vol = roi_vol;
    }

    public double getRoi_per(){
        return roi_per;
    };

    public void setRoi_per(double roi_per) {
        this.roi_per = roi_per;
    }

    public double getInitial_amount() {
        return initial_amount;
    }

    public void setInitial_amount(double initial_amount) {
        this.initial_amount = initial_amount;
    }

    public double getFinal_amount() {
        return final_amount;
    }

    public void setFinal_amount(double final_amount) {
        this.final_amount = final_amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
