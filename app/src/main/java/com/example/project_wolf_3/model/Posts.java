package com.example.project_wolf_3.model;

import java.io.Serializable;
import java.util.Date;

public class Posts implements Serializable {

    private Date date;
    private int isConfirmed;
    private double amount;
    private double gananciap;
    private double ganancia;
    private double price;
    private String user;
    private float valorbtc;
    public Posts(Date date, int isConfirmed, double amount, double gananciap, double ganancia, double price, String user, float valorbtc) {

        this.date = date;
        this.isConfirmed = isConfirmed;
        this.amount = amount;
        this.gananciap = gananciap;
        this.ganancia = ganancia;
        this.price = price;
        this.user = user;
        this.valorbtc = valorbtc;
    }


    public double getPrice() {
        return price;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getGananciap() {
        return gananciap;
    }

    public void setGananciap(double gananciap) {
        this.gananciap = gananciap;
    }

    public double getGanancia() {
        return ganancia;
    }

    public void setGanancia(double ganancia) {
        this.ganancia = ganancia;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getValorbtc() {
        return valorbtc;
    }

    public void setValorbtc(float valorbtc) {
        this.valorbtc = valorbtc;
    }

    public int getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(int isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}