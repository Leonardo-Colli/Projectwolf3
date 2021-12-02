package com.example.project_wolf_3.model;

public class ComisionesModel {
    private float Cpasarela;
    private float Cbitso;
    private float Cexchange;
    private float Capp;

    public ComisionesModel(float cpasarela, float cbitso, float cexchange, float capp) {
        Cpasarela = cpasarela;
        Cbitso = cbitso;
        Cexchange = cexchange;
        Capp = capp;
    }

    public float getCpasarela() {
        return Cpasarela;
    }

    public void setCpasarela(float cpasarela) {
        Cpasarela = cpasarela;
    }

    public float getCbitso() {
        return Cbitso;
    }

    public void setCbitso(float cbitso) {
        Cbitso = cbitso;
    }

    public float getCexchange() {
        return Cexchange;
    }

    public void setCexchange(float cexchange) {
        Cexchange = cexchange;
    }

    public float getCapp() {
        return Capp;
    }

    public void setCapp(float capp) {
        Capp = capp;
    }
}