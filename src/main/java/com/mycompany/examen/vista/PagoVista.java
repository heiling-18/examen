/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.vista;

import com.mycompany.crediya.app.controlador.PagoControlador;
import com.mycompany.crediya.app.controlador.PrestamoControlador;
import com.mycompany.crediya.app.model.EstadoPrestamo;
import com.mycompany.crediya.app.model.Pago;
import com.mycompany.crediya.app.model.Prestamo;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Vista modular para el CRUD completo de Pagos y Abonos.
 * Implementa captura validada y captura granular de excepciones segun su tipo.
 * 
 * @author Heiling
 */
public class PagoVista {

    private Scanner sc;
    private PagoControlador pagoCtrl;
    private PrestamoControlador prestamoCtrl;

    public PagoVista(Scanner sc, PagoControlador pagoCtrl, PrestamoControlador prestamoCtrl) {
        this.sc = sc;
        this.pagoCtrl = pagoCtrl;
        this.prestamoCtrl = prestamoCtrl;
    }

    public void mostrarMenu() {
        String op = "";
        while (!op.equals("0")) {
            System.out.println();
            System.out.println("--- Gestion De Pagos Y Abonos ---");
            System.out.println("1. Registrar Abono a Prestamo");
            System.out.println("2. Ver Historico de Pagos de un Prestamo");
            System.out.println("3. Modificar Monto de un Abono");
            System.out.println("4. Anular / Eliminar Abono");
            System.out.println("0. Volver al menu principal");
            System.out.print("Opcion: ");
            op = sc.nextLine().trim();

            try {
                switch (op) {
                    case "1":
                        registrarAbono();
                        break;
                    case "2":
                        verHistoricoPagos();
                        break;
                    case "3":
                        modificarAbono();
                        break;
                    case "4":
                        anularAbono();
                        break;
                    case "0":
                        break;
                    default:
                        System.out.println("Opcion no valida. Seleccione una opcion del 0 al 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error de formato numerico: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validacion de pago: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("Error de estado del credito: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Error de base de datos MySQL: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error inesperado en el sistema: " + e.getMessage());
            }
        }
    }

    private void registrarAbono() throws SQLException, IllegalArgumentException, IllegalStateException {
        System.out.println();
        System.out.println("--- Formulario De Registro De Abono ---");

        int prestamoId = pedirIdPrestamoValido();
        if (prestamoId == 0) return;

        Prestamo prestamoEncontrado = prestamoCtrl.buscarPrestamoPorId(prestamoId);
        double monto = pedirMontoAbono(prestamoEncontrado.getSaldoPendiente());

        String resultado = pagoCtrl.registrarAbono(prestamoId, monto);
        System.out.println(resultado);
    }

    private void verHistoricoPagos() {
        System.out.println();
        System.out.println("--- Consulta De Historico De Pagos ---");
        int prestamoId = 0;
        try {
            System.out.print("ID del Prestamo a consultar: ");
            prestamoId = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Error de formato numerico: El ID debe ser un numero.");
            return;
        }

        List<Pago> historial = pagoCtrl.listarHistorialPagos(prestamoId);
        if (historial.isEmpty()) {
            System.out.println("No se registran abonos para el prestamo #" + prestamoId);
        } else {
            System.out.println();
            System.out.println("--- Historico De Abonos (Prestamo #" + prestamoId + " - Total: " + historial.size() + ") ---");
            for (Pago p : historial) {
                System.out.println(p);
            }
        }
    }

    private void modificarAbono() throws SQLException, IllegalArgumentException, IllegalStateException {
        System.out.println();
        System.out.println("--- Formulario De Modificacion De Abono ---");
        int pagoId = pedirId("ID del Pago/Abono a modificar (0 para cancelar): ");
        if (pagoId == 0) return;

        Pago pagoActual = pagoCtrl.buscarPagoPorId(pagoId);
        if (pagoActual == null) {
            throw new IllegalStateException("No existe ningun registro de pago con el ID " + pagoId + ".");
        }

        System.out.println("Datos actuales del abono:");
        System.out.println(pagoActual);

        double nuevoMonto = 0;
        while (true) {
            try {
                System.out.print("Nuevo monto para el abono ($): ");
                nuevoMonto = Double.parseDouble(sc.nextLine().trim());
                if (nuevoMonto > 0) break;
                System.out.println("Error de validacion: El nuevo monto debe ser mayor a 0.");
            } catch (NumberFormatException e) {
                System.out.println("Error de formato numerico: Ingrese un valor decimal valido.");
            }
        }

        String resultado = pagoCtrl.modificarMontoAbono(pagoId, nuevoMonto);
        System.out.println(resultado);
    }

    private void anularAbono() throws SQLException, IllegalStateException {
        System.out.println();
        System.out.println("--- Formulario De Anulacion / Eliminacion De Abono ---");
        int pagoId = pedirId("ID del Pago/Abono a anular (0 para cancelar): ");
        if (pagoId == 0) return;

        Pago pagoActual = pagoCtrl.buscarPagoPorId(pagoId);
        if (pagoActual == null) {
            throw new IllegalStateException("No existe ningun registro de pago con el ID " + pagoId + ".");
        }

        System.out.println("Abono a anular: #" + pagoActual.getId() + " - Prestamo asociado: #" + pagoActual.getPrestamoId() + " - Monto: $" + pagoActual.getMonto());
        System.out.print("Esta seguro de anular este abono? El dinero volvera a sumarse al saldo pendiente (S/N): ");
        String confirmacion = sc.nextLine().trim();

        if (confirmacion.equalsIgnoreCase("S")) {
            String resultado = pagoCtrl.anularAbono(pagoId);
            System.out.println(resultado);
        } else {
            System.out.println("Operacion de anulacion cancelada por el usuario.");
        }
    }

    private int pedirId(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error de formato: El ID debe ser un numero entero.");
            }
        }
    }

    private int pedirIdPrestamoValido() {
        while (true) {
            try {
                System.out.print("ID del Prestamo al que desea abonar (0 para cancelar): ");
                int prestamoId = Integer.parseInt(sc.nextLine().trim());
                if (prestamoId == 0) return 0;

                Prestamo prestamoEncontrado = prestamoCtrl.buscarPrestamoPorId(prestamoId);
                if (prestamoEncontrado == null) {
                    System.out.println("Error de validacion: No existe ningun prestamo con el ID " + prestamoId + ". Intente de nuevo.");
                    continue;
                }
                if (prestamoEncontrado.getEstado() == EstadoPrestamo.PAGADO) {
                    System.out.println("Aviso de estado: Este prestamo ya fue cancelado en su totalidad. No requiere abonos.");
                    continue;
                }

                System.out.println("Prestamo seleccionado. Saldo pendiente actual: $" + prestamoEncontrado.getSaldoPendiente());
                return prestamoId;
            } catch (NumberFormatException e) {
                System.out.println("Error de formato numerico: El ID debe ser un numero.");
            }
        }
    }

    private double pedirMontoAbono(double saldoPendiente) {
        while (true) {
            try {
                System.out.print("Monto del abono ($): ");
                double monto = Double.parseDouble(sc.nextLine().trim());
                if (monto <= 0) {
                    System.out.println("Error de validacion: El abono debe ser mayor a 0.");
                    continue;
                }
                if (monto > saldoPendiente) {
                    System.out.println("Error de validacion: El abono no puede ser mayor al saldo pendiente ($" + saldoPendiente + ").");
                    continue;
                }
                return monto;
            } catch (NumberFormatException e) {
                System.out.println("Error de formato numerico: Ingrese un monto valido.");
            }
        }
    }
}
