/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.controlador;

import com.mycompany.crediya.app.Modelo.Persistencia.PagoDAO;
import com.mycompany.crediya.app.Modelo.Persistencia.PrestamoDAO;
import com.mycompany.crediya.app.model.EstadoPrestamo;
import com.mycompany.crediya.app.model.Pago;
import com.mycompany.crediya.app.model.Prestamo;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para la gestion de Pagos y Abonos.
 * Aplica reglas financieras estrictas y lanza excepciones especificas segun el tipo de error.
 * 
 * @author Heiling
 */
public class PagoControlador {

    private PagoDAO pagoDAO;
    private PrestamoDAO prestamoDAO;

    public PagoControlador() {
        this.pagoDAO = new PagoDAO();
        this.prestamoDAO = new PrestamoDAO();
    }

    public String registrarAbono(int prestamoId, double montoAbono) 
            throws SQLException, IllegalArgumentException, IllegalStateException {
        
        if (montoAbono <= 0) {
            throw new IllegalArgumentException("El monto del abono debe ser mayor a $0.");
        }

        Prestamo prestamo = prestamoDAO.buscarPorId(prestamoId);
        if (prestamo == null) {
            throw new IllegalStateException("No existe ningun prestamo registrado con el ID " + prestamoId + ".");
        }

        if (prestamo.getEstado() == EstadoPrestamo.PAGADO) {
            throw new IllegalStateException("Este prestamo ya se encuentra completamente PAGADO. No admite mas abonos.");
        }

        if (montoAbono > prestamo.getSaldoPendiente()) {
            throw new IllegalArgumentException("El abono ($" + montoAbono + ") supera el saldo pendiente actual ($" + prestamo.getSaldoPendiente() + ").");
        }

        double nuevoSaldo = prestamo.getSaldoPendiente() - montoAbono;
        EstadoPrestamo nuevoEstado = (nuevoSaldo <= 0.001) ? EstadoPrestamo.PAGADO : EstadoPrestamo.PENDIENTE;

        Pago nuevoPago = new Pago(0, prestamoId, LocalDate.now(), montoAbono);
        boolean pagoGuardado = pagoDAO.guardar(nuevoPago);
        if (!pagoGuardado) {
            throw new SQLException("No se pudo registrar el pago en la base de datos.");
        }

        boolean saldoActualizado = prestamoDAO.actualizarSaldoYEstado(prestamoId, nuevoSaldo, nuevoEstado);
        if (!saldoActualizado) {
            throw new SQLException("Error critico al actualizar el saldo del prestamo en la base de datos.");
        }

        String respuesta = "Abono registrado con exito. Monto abonado: $" + montoAbono + 
                           ". Nuevo saldo pendiente: $" + nuevoSaldo + ".";
        if (nuevoEstado == EstadoPrestamo.PAGADO) {
            respuesta += " FELICITACIONES! El prestamo ha sido PAGADO EN SU TOTALIDAD.";
        }
        return respuesta;
    }

    public String modificarMontoAbono(int pagoId, double nuevoMonto) 
            throws SQLException, IllegalArgumentException, IllegalStateException {
        
        if (nuevoMonto <= 0) {
            throw new IllegalArgumentException("El nuevo monto del abono debe ser mayor a $0.");
        }

        Pago pago = pagoDAO.buscarPorId(pagoId);
        if (pago == null) {
            throw new IllegalStateException("No existe ningun registro de pago con el ID " + pagoId + ".");
        }

        Prestamo prestamo = prestamoDAO.buscarPorId(pago.getPrestamoId());
        if (prestamo == null) {
            throw new IllegalStateException("No se encontro el prestamo asociado al pago.");
        }

        double diferencia = nuevoMonto - pago.getMonto();
        double nuevoSaldo = prestamo.getSaldoPendiente() - diferencia;

        if (nuevoSaldo < 0) {
            throw new IllegalArgumentException("El ajuste haria que el saldo sea negativo ($" + nuevoSaldo + ").");
        }

        EstadoPrestamo nuevoEstado = (nuevoSaldo <= 0.001) ? EstadoPrestamo.PAGADO : EstadoPrestamo.PENDIENTE;

        pago.setMonto(nuevoMonto);
        boolean pagoActualizado = pagoDAO.actualizar(pago);
        if (!pagoActualizado) {
            throw new SQLException("No se pudo actualizar el pago en la base de datos.");
        }

        boolean saldoActualizado = prestamoDAO.actualizarSaldoYEstado(prestamo.getId(), nuevoSaldo, nuevoEstado);
        if (!saldoActualizado) {
            throw new SQLException("No se pudo actualizar el saldo del prestamo.");
        }

        return "Abono #" + pagoId + " modificado con exito. Nuevo monto: $" + nuevoMonto + 
               ". Nuevo saldo pendiente: $" + nuevoSaldo + " (" + nuevoEstado + ").";
    }

    public String anularAbono(int pagoId) throws SQLException, IllegalStateException {
        Pago pago = pagoDAO.buscarPorId(pagoId);
        if (pago == null) {
            throw new IllegalStateException("No existe ningun pago registrado con el ID " + pagoId + ".");
        }

        Prestamo prestamo = prestamoDAO.buscarPorId(pago.getPrestamoId());
        if (prestamo == null) {
            throw new IllegalStateException("No se encontro el prestamo asociado al abono.");
        }

        // Al anular el abono, el monto se restituye a la deuda
        double nuevoSaldo = prestamo.getSaldoPendiente() + pago.getMonto();
        EstadoPrestamo nuevoEstado = EstadoPrestamo.PENDIENTE;

        boolean eliminado = pagoDAO.eliminar(pagoId);
        if (!eliminado) {
            throw new SQLException("No se pudo eliminar el abono de la base de datos.");
        }

        boolean saldoActualizado = prestamoDAO.actualizarSaldoYEstado(prestamo.getId(), nuevoSaldo, nuevoEstado);
        if (!saldoActualizado) {
            throw new SQLException("Error al restituir el saldo del prestamo.");
        }

        return "Abono #" + pagoId + " por valor de $" + pago.getMonto() + 
               " ha sido anulado. Saldo restaurado del prestamo: $" + nuevoSaldo + ".";
    }

    public List<Pago> listarHistorialPagos(int prestamoId) {
        return pagoDAO.listarPorPrestamo(prestamoId);
    }

    public Pago buscarPagoPorId(int id) {
        return pagoDAO.buscarPorId(id);
    }
}