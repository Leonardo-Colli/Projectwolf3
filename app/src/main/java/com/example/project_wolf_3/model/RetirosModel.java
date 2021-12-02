package com.example.project_wolf_3.model;

import java.util.Date;
import java.util.zip.DataFormatException;

public class RetirosModel {
    private String user;
    private String nombre;
    private String correo;
    private String banco;
    private String clave;
    private double monto;
    private double total;
    private Date fecha;
    private int isConfirmed;

    public RetirosModel(String user, String nombre, String correo, String banco, String clave, double monto, double total, Date fecha, int isConfirmed) {
        this.user = user;
        this.nombre = nombre;
        this.correo = correo;
        this.banco = banco;
        this.clave = clave;
        this.monto = monto;
        this.total = total;
        this.fecha = fecha;
        this.isConfirmed = isConfirmed;
    }

    public int getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(int isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}