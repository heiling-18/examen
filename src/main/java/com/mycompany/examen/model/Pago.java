/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.model;

import java.time.LocalDate;

/**
 *
 * @author Heiling
 */
public class Pago {
    private int id;
    private int prestamoId;
    private LocalDate fechaPago;
    private double monto;

        public Pago(int id, int prestamoId, LocalDate fechaPago, double monto) {
        this.id = id;
        this.prestamoId = prestamoId;
        this.fechaPago = fechaPago;
        this.monto = monto; 
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrestamoId() {
        return prestamoId;
    }

    public void setPrestamoId(int prestamoId) {
        this.prestamoId = prestamoId;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

       @Override
    public String toString() {
        return "Pago #" + id + 
               " | Prestamo #" + prestamoId + 
               " | Fecha: " + fechaPago + 
               " | Monto Abonado: $" + monto;
    }
    
    
}
