package com.example.project_wolf_3.model;

import java.io.Serializable;

public class InvModel implements Serializable {
    private double amount;
    private double gananciap;
    private double ganancia;
    private double price;

    public InvModel(double amount, double gananciap, double ganancia, double price) {
        this.amount = amount;
        this.gananciap = gananciap;
        this.ganancia = ganancia;
        this.price = price;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
