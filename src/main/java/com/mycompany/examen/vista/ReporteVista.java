/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.vista;

import com.mycompany.crediya.app.controlador.ReporteControlador;
import com.mycompany.crediya.app.model.Cliente;
import com.mycompany.crediya.app.model.Prestamo;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Vista modular para el modulo de Reportes y consultas analiticas con Streams.
 * Implementa captura granular de excepciones segun su tipo.
 * 
 * @author Heiling
 */
public class ReporteVista {

    private Scanner sc;
    private ReporteControlador reporteCtrl;

    public ReporteVista(Scanner sc, ReporteControlador reporteCtrl) {
        this.sc = sc;
        this.reporteCtrl = reporteCtrl;
    }

    public void mostrarMenu() {
        String op = "";
        while (!op.equals("0")) {
            System.out.println();
            System.out.println("--- Modulo De Reportes Analiticos ---");
            System.out.println("1. Prestamos Activos (Pendientes)");
            System.out.println("2. Prestamos Vencidos");
            System.out.println("3. Clientes Morosos");
            System.out.println("4. Total Cartera Pendiente por Cobrar");
            System.out.println("0. Volver al menu principal");
            System.out.print("Opcion: ");
            op = sc.nextLine().trim();

            try {
                switch (op) {
                    case "1":
                        mostrarPrestamosActivos();
                        break;
                    case "2":
                        mostrarPrestamosVencidos();
                        break;
                    case "3":
                        mostrarClientesMorosos();
                        break;
                    case "4":
                        mostrarTotalCartera();
                        break;
                    case "0":
                        break;
                    default:
                        System.out.println("Opcion no valida. Seleccione una opcion del 0 al 4.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error de parametro en reporte: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("Error en el estado de los datos: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error inesperado al generar reporte: " + e.getMessage());
            }
        }
    }

    private void mostrarPrestamosActivos() {
        List<Prestamo> activos = reporteCtrl.obtenerPrestamosActivos();
        if (activos.isEmpty()) {
            System.out.println("No hay prestamos activos actualmente.");
        } else {
            System.out.println();
            System.out.println("--- Prestamos Activos (" + activos.size() + ") ---");
            activos.forEach(System.out::println);
        }
    }

    private void mostrarPrestamosVencidos() {
        List<Prestamo> vencidos = reporteCtrl.obtenerPrestamosVencidos();
        if (vencidos.isEmpty()) {
            System.out.println("No hay prestamos vencidos en el sistema.");
        } else {
            System.out.println();
            System.out.println("--- Prestamos Vencidos (" + vencidos.size() + ") ---");
            vencidos.forEach(System.out::println);
        }
    }

    private void mostrarClientesMorosos() {
        List<Cliente> morosos = reporteCtrl.obtenerClientesMorosos();
        if (morosos.isEmpty()) {
            System.out.println("No hay clientes en mora actualmente.");
        } else {
            System.out.println();
            System.out.println("--- Clientes Morosos (" + morosos.size() + ") ---");
            morosos.forEach(System.out::println);
        }
    }

    private void mostrarTotalCartera() {
        double total = reporteCtrl.calcularTotalCarteraPendiente();
        System.out.println();
        System.out.println("Total de cartera pendiente por cobrar: $" + total);
    }
}
