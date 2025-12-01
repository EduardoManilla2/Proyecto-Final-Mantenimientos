package com.example.prueba002;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Mantenimiento implements Serializable {

    @SerializedName("idMantenimiento")
    public String idMantenimiento;

    @SerializedName("nombreEquipo")
    public String nombreEquipo;

    @SerializedName("nombreLabo")
    public String nombreLabo;


    @SerializedName("estatus")
    public String estatus;

    @SerializedName("fechaPrograma")
    public String fechaPrograma;

    @SerializedName("fechaRealiza")
    public String fechaRealiza;

    @SerializedName("fechaProx")
    public String fechaProx;

    @SerializedName("observaciones")
    public String observaciones;

    @SerializedName("nombreFrecuencia")
    public String nombreFrecuencia;


    @SerializedName("actividad")
    public String actividad;

    @SerializedName("programadoPor")
    public String programandoPor;

    @SerializedName("tecnicoAsignado")
    public String tecnicoAsignado;





    public Mantenimiento(String tecnicoAsignado, String programandoPor, String actividad, String nombreFrecuencia, String observaciones, String fechaProx, String fechaRealiza, String fechaPrograma, String estatus, String nombreLabo, String nombreEquipo, String idMantenimiento) {
        this.tecnicoAsignado = tecnicoAsignado;
        this.programandoPor = programandoPor;
        this.actividad = actividad;
        this.nombreFrecuencia = nombreFrecuencia;
        this.observaciones = observaciones;
        this.fechaProx = fechaProx;
        this.fechaRealiza = fechaRealiza;
        this.fechaPrograma = fechaPrograma;
        this.estatus = estatus;
        this.nombreLabo = nombreLabo;
        this.nombreEquipo = nombreEquipo;
        this.idMantenimiento = idMantenimiento;
    }

    public String getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(String idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getNombreLabo() {
        return nombreLabo;
    }

    public void setNombreLabo(String nombreLabo) {
        this.nombreLabo = nombreLabo;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getFechaPrograma() {
        return fechaPrograma;
    }

    public void setFechaPrograma(String fechaPrograma) {
        this.fechaPrograma = fechaPrograma;
    }

    public String getFechaRealiza() {
        return fechaRealiza;
    }

    public void setFechaRealiza(String fechaRealiza) {
        this.fechaRealiza = fechaRealiza;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getNombreFrecuencia() {
        return nombreFrecuencia;
    }

    public void setNombreFrecuencia(String nombreFrecuencia) {
        this.nombreFrecuencia = nombreFrecuencia;
    }

    public String getActividad() {
        return actividad;
    }

    public void setActividad(String actividad) {
        this.actividad = actividad;
    }

    public String getProgramandoPor() {
        return programandoPor;
    }

    public void setProgramandoPor(String programandoPor) {
        this.programandoPor = programandoPor;
    }

    public String getTecnicoAsignado() {
        return tecnicoAsignado;
    }

    public void setTecnicoAsignado(String tecnicoAsignado) {
        this.tecnicoAsignado = tecnicoAsignado;
    }

    public String getFechaProx() {
        return fechaProx;
    }

    public void setFechaProx(String fechaProx) {
        this.fechaProx = fechaProx;
    }
}