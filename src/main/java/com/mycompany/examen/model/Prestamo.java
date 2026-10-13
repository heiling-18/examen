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
        public class Prestamo {
            private int id;
            private Cliente cliente;
            private Empleado empleado;
            private double monto;
            private double interes;
            private int cuotas;
            private LocalDate fechaInicio;
            private LocalDate fechaVencimiento;
            private double montoTotal;
            private double valorCuota;
            private double saldoPendiente;
            private EstadoPrestamo estado;

            // Constructor 1: Para cuando leemos de la base de datos con todos los campos ya calculados
            public Prestamo(int id, Cliente cliente, Empleado empleado, double monto, double interes, 
                            int cuotas, LocalDate fechaInicio, LocalDate fechaVencimiento, 
                            double montoTotal, double valorCuota, double saldoPendiente, EstadoPrestamo estado) {
                this.id = id;
                this.cliente = cliente;
                this.empleado = empleado;
                this.monto = monto;
                this.interes = interes;
                this.cuotas = cuotas;
                this.fechaInicio = fechaInicio;
                this.fechaVencimiento = fechaVencimiento;
                this.montoTotal = montoTotal;
                this.valorCuota = valorCuota;
                this.saldoPendiente = saldoPendiente;
                this.estado = estado;
            }

            // Constructor 2: Para cuando creas un préstamo NUEVO desde consola 
            public Prestamo(int id, Cliente cliente, Empleado empleado, double monto, double interes, 
                            int cuotas, LocalDate fechaInicio, EstadoPrestamo estado) {
                this.id = id;
                this.cliente = cliente;
                this.empleado = empleado;
                this.monto = monto;
                this.interes = interes;
                this.cuotas = cuotas;
                this.fechaInicio = fechaInicio;
                this.estado = estado;

                // 
                this.montoTotal = calcularMontoTotal();
                this.valorCuota = calcularCuotaMensual();
                this.saldoPendiente = this.montoTotal; 
                this.fechaVencimiento = fechaInicio.plusMonths(cuotas); 
            }

            // Métodos
            public double calcularMontoTotal() {
                return monto + (monto * (interes / 100.0));
            }

            public double calcularCuotaMensual() {
                return calcularMontoTotal() / cuotas;
            }

            // Getters y Setters
            public int getId()
            { return id; }
            public void setId(int id) 
            { this.id = id; }

            public Cliente getCliente() 
            { return cliente; }
            public void setCliente(Cliente cliente)
            { this.cliente = cliente; }

            public Empleado getEmpleado() 
            { return empleado; }
            public void setEmpleado(Empleado empleado) 
            { this.empleado = empleado; }

            public double getMonto()
            { return monto; }
            public void setMonto(double monto) 
            { this.monto = monto; }

            public double getInteres() 
            { return interes; }
            public void setInteres(double interes)
            { this.interes = interes; }

            public int getCuotas() 
            { return cuotas; }
            public void setCuotas(int cuotas)
            { this.cuotas = cuotas; }

            public LocalDate getFechaInicio() 
            { return fechaInicio; }
            public void setFechaInicio(LocalDate fechaInicio)
            { this.fechaInicio = fechaInicio; }

            public LocalDate getFechaVencimiento() 
            { return fechaVencimiento; }
            public void setFechaVencimiento(LocalDate fechaVencimiento)
            { this.fechaVencimiento = fechaVencimiento; }

            public double getMontoTotal() 
            { return montoTotal; }
            public void setMontoTotal(double montoTotal)
            { this.montoTotal = montoTotal; }

            public double getValorCuota() 
            { return valorCuota; }
            public void setValorCuota(double valorCuota)
            { this.valorCuota = valorCuota; }

            public double getSaldoPendiente()
            { return saldoPendiente; }
            public void setSaldoPendiente(double saldoPendiente) 
            { this.saldoPendiente = saldoPendiente; }

            public EstadoPrestamo getEstado() 
            { return estado; }
            public void setEstado(EstadoPrestamo estado) 
            { this.estado = estado; }

            @Override
            public String toString() {
                return "Prestamo #" + id + 
                       " | Cliente: " + cliente.getNombre() + 
                       " | Monto: $" + monto + 
                       " | Total a Pagar: $" + montoTotal + 
                       " | Saldo Pendiente: $" + saldoPendiente + 
                       " | Cuota: $" + valorCuota + "/mes (" + cuotas + " cuotas)" + 
                       " | Vence: " + fechaVencimiento + 
                       " | Estado: " + estado;
            }
        }