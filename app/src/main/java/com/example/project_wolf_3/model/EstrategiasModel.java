package com.example.project_wolf_3.model;

public class EstrategiasModel {
    private String user;
    private float stoploss;

    public EstrategiasModel(String user, float stoploss) {
        this.user = user;
        this.stoploss = stoploss;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public float getStoploss() {
        return stoploss;
    }

    public void setStoploss(float stoploss) {
        this.stoploss = stoploss;
    }
}
