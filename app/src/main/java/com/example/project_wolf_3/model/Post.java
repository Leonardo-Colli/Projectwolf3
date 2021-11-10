package com.example.project_wolf_3.model;

import com.google.gson.annotations.SerializedName;

public class Post {

    private String name;
    private String user;
    private Float price;
    private Float amount;
    private int installments;
    private String transactionid;
    private int isConfirmed;
    private String firebaseSavingId;

    public Post(String name, String user, Float price, Float amount, int installments, String transactionid, int isConfirmed, String firebaseSavingId) {
        this.name = name;
        this.user = user;
        this.price = price;
        this.amount = amount;
        this.installments = installments;
        this.transactionid = transactionid;
        this.isConfirmed = isConfirmed;
        this.firebaseSavingId = firebaseSavingId;
    }

    public String getFirebaseSavingId() {
        return firebaseSavingId;
    }

    public void setFirebaseSavingId(String firebaseSavingId) {
        this.firebaseSavingId = firebaseSavingId;
    }

    public int getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(int isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public int getInstallments() {
        return installments;
    }

    public void setInstallments(int installments) {
        this.installments = installments;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }
}
