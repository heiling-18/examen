/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.controlador;

import com.mycompany.crediya.app.Modelo.Persistencia.ClienteDAO;
import com.mycompany.crediya.app.Modelo.Persistencia.EmpleadoDAO;
import com.mycompany.crediya.app.Modelo.Persistencia.PrestamoDAO;
import com.mycompany.crediya.app.model.Cliente;
import com.mycompany.crediya.app.model.Empleado;
import com.mycompany.crediya.app.model.EstadoPrestamo;
import com.mycompany.crediya.app.model.Prestamo;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para la gestion de Prestamos.
 * Aplica reglas de negocio y lanza excepciones especificas segun el tipo de error.
 * 
 * @author Heiling
 */
public class PrestamoControlador {

    private PrestamoDAO prestamoDAO;
    private ClienteDAO clienteDAO;
    private EmpleadoDAO empleadoDAO;

    public PrestamoControlador() {
        this.prestamoDAO = new PrestamoDAO();
        this.clienteDAO = new ClienteDAO();
        this.empleadoDAO = new EmpleadoDAO();
    }

    public void crearPrestamo(int clienteId, int empleadoId, double monto, double interes, int cuotas, LocalDate fechaInicio, LocalDate fechaVencimiento) 
            throws SQLException, IllegalArgumentException, IllegalStateException {
        
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a prestar debe ser un valor mayor a $0.");
        }
        if (interes < 0 || interes > 100) {
            throw new IllegalArgumentException("La tasa de interes debe ubicarse entre 0% y 100%.");
        }
        if (cuotas <= 0) {
            throw new IllegalArgumentException("El numero de cuotas mensuales debe ser de al menos 1.");
        }
        if (fechaVencimiento.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a la fecha de inicio.");
        }

        Cliente cliente = clienteDAO.buscarPorId(clienteId);
        if (cliente == null) {
            throw new IllegalStateException("No existe ningun cliente registrado con el ID " + clienteId + ".");
        }

        Empleado empleado = empleadoDAO.buscarPorId(empleadoId);
        if (empleado == null) {
            throw new IllegalStateException("No existe ningun empleado registrado con el ID " + empleadoId + ".");
        }

        double montoTotal = monto + (monto * (interes / 100.0));
        double valorCuota = montoTotal / cuotas;
        double saldoPendiente = montoTotal;

        Prestamo nuevoPrestamo = new Prestamo(0, cliente, empleado, monto, interes, cuotas, 
                                             fechaInicio, fechaVencimiento, montoTotal, 
                                             valorCuota, saldoPendiente, EstadoPrestamo.PENDIENTE);

        boolean exito = prestamoDAO.guardar(nuevoPrestamo);
        if (!exito) {
            throw new SQLException("Ocurrio un problema al guardar el prestamo en la base de datos.");
        }
    }

    public void actualizarPrestamo(int id, int clienteId, int empleadoId, double monto, double interes, int cuotas, LocalDate fechaInicio, LocalDate fechaVencimiento)
            throws SQLException, IllegalArgumentException, IllegalStateException {
        
        Prestamo existente = prestamoDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalStateException("No existe ningun prestamo registrado con el ID " + id + ".");
        }

        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a $0.");
        }
        if (interes < 0 || interes > 100) {
            throw new IllegalArgumentException("La tasa de interes debe estar entre 0% y 100%.");
        }
        if (cuotas <= 0) {
            throw new IllegalArgumentException("Las cuotas deben ser al menos 1.");
        }
        if (fechaVencimiento.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a la fecha de inicio.");
        }

        Cliente cliente = clienteDAO.buscarPorId(clienteId);
        if (cliente == null) {
            throw new IllegalStateException("No existe el cliente con ID " + clienteId + ".");
        }

        Empleado empleado = empleadoDAO.buscarPorId(empleadoId);
        if (empleado == null) {
            throw new IllegalStateException("No existe el empleado con ID " + empleadoId + ".");
        }

        double montoTotal = monto + (monto * (interes / 100.0));
        double valorCuota = montoTotal / cuotas;
        
        // Calculo de abonos previos ya realizados
        double abonosRealizados = existente.getMontoTotal() - existente.getSaldoPendiente();
        if (montoTotal < abonosRealizados) {
            throw new IllegalArgumentException("El nuevo monto total ($" + montoTotal + ") no puede ser menor a los abonos ya realizados ($" + abonosRealizados + ").");
        }
        double saldoPendiente = montoTotal - abonosRealizados;
        EstadoPrestamo estado = (saldoPendiente <= 0.001) ? EstadoPrestamo.PAGADO : EstadoPrestamo.PENDIENTE;

        Prestamo prestamoActualizado = new Prestamo(id, cliente, empleado, monto, interes, cuotas,
                                                  fechaInicio, fechaVencimiento, montoTotal,
                                                  valorCuota, saldoPendiente, estado);

        boolean exito = prestamoDAO.actualizar(prestamoActualizado);
        if (!exito) {
            throw new SQLException("No se pudo actualizar el prestamo en la base de datos.");
        }
    }

    public void eliminarPrestamo(int id) throws SQLException, IllegalStateException {
        Prestamo existente = prestamoDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalStateException("No existe ningun prestamo registrado con el ID " + id + ".");
        }

        boolean exito = prestamoDAO.eliminar(id);
        if (!exito) {
            throw new SQLException("No se pudo eliminar el prestamo en la base de datos.");
        }
    }

    public List<Prestamo> listarPrestamos() {
        return prestamoDAO.listarTodos();
    }

    public Prestamo buscarPrestamoPorId(int id) {
        return prestamoDAO.buscarPorId(id);
    }
}