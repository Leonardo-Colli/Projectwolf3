package com.example.project_wolf_3.model;

import java.util.Date;

public class TransactionModel {

    private String objective;
    private double initial_amount;
    private double final_amount;
    private Date date;

    private TransactionModel() {}

    private TransactionModel(String objective, double initial_amount, double final_amount, Date date){
        this.objective = objective;
        this.initial_amount = initial_amount;
        this.final_amount = final_amount;
        this.date = date;
    }

    public String getObjective(){
        return objective;
    };

    public void setObjective(){
        this.objective = objective;
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
