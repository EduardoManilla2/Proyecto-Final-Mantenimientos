package com.example.prueba002;

public class Actividad {
    private int idManteAct;
    private String nombre;
    private int cumplimiento;

    public Actividad(int idManteAct, String nombre, int cumplimiento) {
        this.idManteAct = idManteAct;
        this.nombre = nombre;
        this.cumplimiento = cumplimiento;
    }

    public int getIdManteAct() { return idManteAct; }
    public String getNombre() { return nombre; }
    public int getCumplimiento() { return cumplimiento; }
    public void setCumplimiento(int cumplimiento) { this.cumplimiento = cumplimiento; }
}
