/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.controlador;

import com.mycompany.crediya.app.Modelo.Persistencia.PrestamoDAO;
import com.mycompany.crediya.app.model.Cliente;
import com.mycompany.crediya.app.model.EstadoPrestamo;
import com.mycompany.crediya.app.model.Prestamo;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 
 * @author Heiling
 */
public class ReporteControlador {

    private PrestamoDAO prestamoDAO;

    public ReporteControlador() {
        this.prestamoDAO = new PrestamoDAO();
    }

    
    public List<Prestamo> obtenerPrestamosActivos() {
        List<Prestamo> todos = prestamoDAO.listarTodos();
        return todos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.PENDIENTE)
                .collect(Collectors.toList());
    }

   
    public List<Prestamo> obtenerPrestamosVencidos() {
        List<Prestamo> todos = prestamoDAO.listarTodos();
        LocalDate hoy = LocalDate.now();

        return todos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.PENDIENTE)
                .filter(p -> p.getFechaVencimiento().isBefore(hoy))
                .collect(Collectors.toList());
    }

    
    public List<Cliente> obtenerClientesMorosos() {
        List<Prestamo> todos = prestamoDAO.listarTodos();
        LocalDate hoy = LocalDate.now();

        return todos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.PENDIENTE)
                .filter(p -> p.getFechaVencimiento().isBefore(hoy))
                .map(Prestamo::getCliente) 
                .distinct()                 
                .collect(Collectors.toList());
    }

    
    public double calcularTotalCarteraPendiente() {
        List<Prestamo> todos = prestamoDAO.listarTodos();
        return todos.stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.PENDIENTE)
                .mapToDouble(Prestamo::getSaldoPendiente)
                .sum();
    }
}