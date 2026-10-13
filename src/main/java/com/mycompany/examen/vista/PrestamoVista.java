/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.vista;

import com.mycompany.crediya.app.controlador.ClienteControlador;
import com.mycompany.crediya.app.controlador.EmpleadoControlador;
import com.mycompany.crediya.app.controlador.PrestamoControlador;
import com.mycompany.crediya.app.model.Cliente;
import com.mycompany.crediya.app.model.Empleado;
import com.mycompany.crediya.app.model.Prestamo;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Vista modular para el CRUD completo de Prestamos.
 * Implementa captura validada y captura granular de excepciones segun su tipo.
 * 
 * @author Heiling
 */
public class PrestamoVista {

    private Scanner sc;
    private PrestamoControlador prestamoCtrl;
    private ClienteControlador clienteCtrl;
    private EmpleadoControlador empleadoCtrl;

    public PrestamoVista(Scanner sc, PrestamoControlador prestamoCtrl, ClienteControlador clienteCtrl, EmpleadoControlador empleadoCtrl) {
        this.sc = sc;
        this.prestamoCtrl = prestamoCtrl;
        this.clienteCtrl = clienteCtrl;
        this.empleadoCtrl = empleadoCtrl;
    }

    public void mostrarMenu() {
        String op = "";
        while (!op.equals("0")) {
            System.out.println();
            System.out.println("--- Gestion De Prestamos ---");
            System.out.println("1. Crear Nuevo Prestamo");
            System.out.println("2. Listar Todos los Prestamos");
            System.out.println("3. Editar Prestamo");
            System.out.println("4. Eliminar Prestamo");
            System.out.println("0. Volver al menu principal");
            System.out.print("Opcion: ");
            op = sc.nextLine().trim();

            try {
                switch (op) {
                    case "1":
                        crearNuevoPrestamo();
                        break;
                    case "2":
                        listarPrestamos();
                        break;
                    case "3":
                        editarPrestamo();
                        break;
                    case "4":
                        eliminarPrestamo();
                        break;
                    case "0":
                        break;
                    default:
                        System.out.println("Opcion no valida. Seleccione una opcion del 0 al 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error de formato numerico: " + e.getMessage());
            } catch (DateTimeParseException e) {
                System.out.println("Error de formato de fecha: Debe usar el formato estricto AAAA-MM-DD.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validacion financiera: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("Error de estado del prestamo: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Error de base de datos MySQL: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error inesperado en el sistema: " + e.getMessage());
            }
        }
    }

    private void crearNuevoPrestamo() throws SQLException, IllegalArgumentException, IllegalStateException {
        System.out.println();
        System.out.println("--- Formulario De Creacion De Prestamo ---");

        int clienteId = pedirIdCliente();
        if (clienteId == 0) return;

        int empleadoId = pedirIdEmpleado();
        if (empleadoId == 0) return;

        double monto = pedirMonto();
        double interes = pedirInteres();
        int cuotas = pedirCuotas();
        LocalDate fechaInicio = pedirFecha("Fecha de inicio (AAAA-MM-DD): ");
        LocalDate fechaVencimiento = pedirFechaVencimiento(fechaInicio);

        prestamoCtrl.crearPrestamo(clienteId, empleadoId, monto, interes, cuotas, fechaInicio, fechaVencimiento);
        System.out.println("Prestamo registrado exitosamente en el sistema.");
    }

    private void listarPrestamos() {
        List<Prestamo> lista = prestamoCtrl.listarPrestamos();
        if (lista.isEmpty()) {
            System.out.println("No hay prestamos registrados en el sistema.");
        } else {
            System.out.println();
            System.out.println("--- Listado General De Prestamos (" + lista.size() + ") ---");
            for (Prestamo p : lista) {
                System.out.println(p);
            }
        }
    }

    private void editarPrestamo() throws SQLException, IllegalArgumentException, IllegalStateException {
        System.out.println();
        System.out.println("--- Formulario De Edicion De Prestamo ---");
        int id = pedirIdPrestamo("ID del Prestamo a editar (0 para cancelar): ");
        if (id == 0) return;

        Prestamo actual = prestamoCtrl.buscarPrestamoPorId(id);
        if (actual == null) {
            throw new IllegalStateException("No existe ningun prestamo registrado con el ID " + id + ".");
        }

        System.out.println("Datos actuales del prestamo:");
        System.out.println(actual);
        System.out.println("Ingrese los nuevos parametros para actualizar:");

        int nuevoClienteId = pedirIdCliente();
        if (nuevoClienteId == 0) return;

        int nuevoEmpleadoId = pedirIdEmpleado();
        if (nuevoEmpleadoId == 0) return;

        double nuevoMonto = pedirMonto();
        double nuevoInteres = pedirInteres();
        int nuevasCuotas = pedirCuotas();
        LocalDate nuevaFechaInicio = pedirFecha("Nueva fecha de inicio (AAAA-MM-DD): ");
        LocalDate nuevaFechaVencimiento = pedirFechaVencimiento(nuevaFechaInicio);

        prestamoCtrl.actualizarPrestamo(id, nuevoClienteId, nuevoEmpleadoId, nuevoMonto, nuevoInteres, nuevasCuotas, nuevaFechaInicio, nuevaFechaVencimiento);
        System.out.println("Prestamo #" + id + " actualizado exitosamente.");
    }

    private void eliminarPrestamo() throws SQLException, IllegalStateException {
        System.out.println();
        System.out.println("--- Formulario De Eliminacion De Prestamo ---");
        int id = pedirIdPrestamo("ID del Prestamo a eliminar (0 para cancelar): ");
        if (id == 0) return;

        Prestamo actual = prestamoCtrl.buscarPrestamoPorId(id);
        if (actual == null) {
            throw new IllegalStateException("No existe ningun prestamo con el ID " + id + ".");
        }

        System.out.println("Prestamo a eliminar: #" + actual.getId() + " - Cliente: " + actual.getCliente().getNombre() + " - Saldo: $" + actual.getSaldoPendiente());
        System.out.print("Esta seguro de eliminar este prestamo y sus registros asociados? (S/N): ");
        String confirmacion = sc.nextLine().trim();

        if (confirmacion.equalsIgnoreCase("S")) {
            prestamoCtrl.eliminarPrestamo(id);
            System.out.println("Prestamo #" + id + " eliminado exitosamente del sistema.");
        } else {
            System.out.println("Operacion de eliminacion cancelada por el usuario.");
        }
    }

    private int pedirIdPrestamo(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error de formato: Ingrese un numero entero para el ID.");
            }
        }
    }

    private int pedirIdCliente() {
        while (true) {
            try {
                System.out.print("ID del Cliente solicitante (0 para cancelar): ");
                int clienteId = Integer.parseInt(sc.nextLine().trim());
                if (clienteId == 0) return 0;

                Cliente cliente = clienteCtrl.buscarClientePorId(clienteId);
                if (cliente != null) {
                    System.out.println("Cliente seleccionado: " + cliente.getNombre());
                    return clienteId;
                }
                System.out.println("Error de validacion: No existe ningun cliente con el ID " + clienteId + ". Intente de nuevo.");
            } catch (NumberFormatException e) {
                System.out.println("Error de formato: Ingrese un numero entero.");
            }
        }
    }

    private int pedirIdEmpleado() {
        while (true) {
            try {
                System.out.print("ID del Empleado que autoriza (0 para cancelar): ");
                int empleadoId = Integer.parseInt(sc.nextLine().trim());
                if (empleadoId == 0) return 0;

                Empleado empleado = empleadoCtrl.buscarEmpleadoPorId(empleadoId);
                if (empleado != null) {
                    System.out.println("Empleado seleccionado: " + empleado.getNombre());
                    return empleadoId;
                }
                System.out.println("Error de validacion: No existe ningun empleado con el ID " + empleadoId + ". Intente de nuevo.");
            } catch (NumberFormatException e) {
                System.out.println("Error de formato: Ingrese un numero entero.");
            }
        }
    }

    private double pedirMonto() {
        while (true) {
            try {
                System.out.print("Monto a prestar ($): ");
                double monto = Double.parseDouble(sc.nextLine().trim());
                if (monto > 0) return monto;
                System.out.println("Error de validacion: El monto debe ser mayor a 0.");
            } catch (NumberFormatException e) {
                System.out.println("Error de formato numerico: Ingrese un valor numerico valido.");
            }
        }
    }

    private double pedirInteres() {
        while (true) {
            try {
                System.out.print("Porcentaje de interes (0 a 100%): ");
                double interes = Double.parseDouble(sc.nextLine().trim());
                if (interes >= 0 && interes <= 100) return interes;
                System.out.println("Error de validacion: La tasa de interes debe estar entre 0% y 100%.");
            } catch (NumberFormatException e) {
                System.out.println("Error de formato numerico: Ingrese un valor numerico valido.");
            }
        }
    }

    private int pedirCuotas() {
        while (true) {
            try {
                System.out.print("Numero de cuotas mensuales: ");
                int cuotas = Integer.parseInt(sc.nextLine().trim());
                if (cuotas > 0) return cuotas;
                System.out.println("Error de validacion: Debe ser al menos 1 cuota mensual.");
            } catch (NumberFormatException e) {
                System.out.println("Error de formato: Ingrese un numero entero de cuotas.");
            }
        }
    }

    private LocalDate pedirFecha(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return LocalDate.parse(sc.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Error de formato de fecha: Use AAAA-MM-DD (ejemplo: 2026-10-08).");
            }
        }
    }

    private LocalDate pedirFechaVencimiento(LocalDate fechaInicio) {
        while (true) {
            try {
                System.out.print("Fecha de vencimiento (AAAA-MM-DD): ");
                LocalDate fechaVencimiento = LocalDate.parse(sc.nextLine().trim());
                if (!fechaVencimiento.isBefore(fechaInicio)) {
                    return fechaVencimiento;
                }
                System.out.println("Error de validacion: La fecha de vencimiento no puede ser anterior a la fecha de inicio.");
            } catch (DateTimeParseException e) {
                System.out.println("Error de formato de fecha: Use AAAA-MM-DD (ejemplo: 2026-12-08).");
            }
        }
    }
}
