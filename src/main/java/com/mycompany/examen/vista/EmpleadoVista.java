/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.vista;

import com.mycompany.crediya.app.controlador.EmpleadoControlador;
import com.mycompany.crediya.app.model.Empleado;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Vista modular para el CRUD completo de Empleados.
 * Implementa captura validada y captura granular de excepciones segun su tipo.
 * 
 * @author Heiling
 */
public class EmpleadoVista {

    private Scanner sc;
    private EmpleadoControlador empleadoCtrl;

    public EmpleadoVista(Scanner sc, EmpleadoControlador empleadoCtrl) {
        this.sc = sc;
        this.empleadoCtrl = empleadoCtrl;
    }

    public void mostrarMenu() {
        String op = "";
        while (!op.equals("0")) {
            System.out.println();
            System.out.println("--- Gestion De Empleados ---");
            System.out.println("1. Registrar Empleado");
            System.out.println("2. Listar Empleados");
            System.out.println("3. Editar Empleado");
            System.out.println("4. Eliminar Empleado");
            System.out.println("0. Volver al menu principal");
            System.out.print("Opcion: ");
            op = sc.nextLine().trim();

            try {
                switch (op) {
                    case "1":
                        registrarEmpleado();
                        break;
                    case "2":
                        listarEmpleados();
                        break;
                    case "3":
                        editarEmpleado();
                        break;
                    case "4":
                        eliminarEmpleado();
                        break;
                    case "0":
                        break;
                    default:
                        System.out.println("Opcion no valida. Seleccione una opcion del 0 al 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error de formato numerico: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validacion de datos: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("Error de estado del empleado: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Error de base de datos MySQL: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error inesperado en el sistema: " + e.getMessage());
            }
        }
    }

    private void registrarEmpleado() throws SQLException, IllegalArgumentException {
        System.out.println();
        System.out.println("--- Formulario De Registro De Empleado ---");

        String nombre = pedirNombre();
        String doc = pedirDocumento();
        String rol = pedirRol();
        String correo = pedirCorreo();
        double salario = pedirSalario();

        empleadoCtrl.registrarEmpleado(nombre, doc, rol, correo, salario);
        System.out.println("Empleado registrado exitosamente en el sistema.");
    }

    private void listarEmpleados() {
        List<Empleado> lista = empleadoCtrl.listarEmpleados();
        if (lista.isEmpty()) {
            System.out.println("No hay empleados registrados en la base de datos.");
        } else {
            System.out.println();
            System.out.println("--- Lista De Empleados Registrados (" + lista.size() + ") ---");
            for (Empleado e : lista) {
                System.out.println(e);
            }
        }
    }

    private void editarEmpleado() throws SQLException, IllegalArgumentException, IllegalStateException {
        System.out.println();
        System.out.println("--- Formulario De Edicion De Empleado ---");
        int id = pedirIdEmpleado("ID del Empleado a editar (0 para cancelar): ");
        if (id == 0) return;

        Empleado actual = empleadoCtrl.buscarEmpleadoPorId(id);
        if (actual == null) {
            throw new IllegalStateException("No existe ningun empleado con el ID " + id + ".");
        }

        System.out.println("Datos actuales del empleado:");
        System.out.println(actual);
        System.out.println("Ingrese los nuevos datos a continuacion:");

        String nuevoNombre = pedirNombre();
        String nuevoDoc = pedirDocumento();
        String nuevoRol = pedirRol();
        String nuevoCorreo = pedirCorreo();
        double nuevoSalario = pedirSalario();

        empleadoCtrl.actualizarEmpleado(id, nuevoNombre, nuevoDoc, nuevoRol, nuevoCorreo, nuevoSalario);
        System.out.println("Empleado #" + id + " actualizado exitosamente.");
    }

    private void eliminarEmpleado() throws SQLException, IllegalStateException {
        System.out.println();
        System.out.println("--- Formulario De Eliminacion De Empleado ---");
        int id = pedirIdEmpleado("ID del Empleado a eliminar (0 para cancelar): ");
        if (id == 0) return;

        Empleado actual = empleadoCtrl.buscarEmpleadoPorId(id);
        if (actual == null) {
            throw new IllegalStateException("No existe ningun empleado con el ID " + id + ".");
        }

        System.out.println("Empleado a eliminar: " + actual.getNombre() + " (Doc: " + actual.getDocumento() + " | Cargo: " + actual.getRol() + ")");
        System.out.print("Esta seguro de eliminar este registro permanentemente? (S/N): ");
        String confirmacion = sc.nextLine().trim();

        if (confirmacion.equalsIgnoreCase("S")) {
            empleadoCtrl.eliminarEmpleado(id);
            System.out.println("Empleado #" + id + " eliminado exitosamente del sistema.");
        } else {
            System.out.println("Operacion de eliminacion cancelada por el usuario.");
        }
    }

    private int pedirIdEmpleado(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Error de formato: El ID debe ser un numero entero.");
            }
        }
    }

    private String pedirNombre() {
        while (true) {
            System.out.print("Nombre completo: ");
            String nombre = sc.nextLine().trim();
            if (nombre.matches("^[a-zA-Z ]+$") && !nombre.isEmpty()) {
                return nombre;
            }
            System.out.println("Error de validacion: El nombre solo puede contener letras. Intente de nuevo.");
        }
    }

    private String pedirDocumento() {
        while (true) {
            System.out.print("Documento (Cedula): ");
            String doc = sc.nextLine().trim();
            if (doc.matches("\\d+")) {
                return doc;
            }
            System.out.println("Error de validacion: El documento debe contener solo numeros. Intente de nuevo.");
        }
    }

    private String pedirRol() {
        while (true) {
            System.out.print("Rol / Cargo: ");
            String rol = sc.nextLine().trim();
            if (rol.matches("^[a-zA-Z ]+$") && !rol.isEmpty()) {
                return rol;
            }
            System.out.println("Error de validacion: El cargo solo puede contener letras. Intente de nuevo.");
        }
    }

    private String pedirCorreo() {
        while (true) {
            System.out.print("Correo electronico: ");
            String correo = sc.nextLine().trim();
            if (correo.contains("@") && correo.contains(".")) {
                return correo;
            }
            System.out.println("Error de validacion: Ingrese un correo valido con @ y un punto (ej: usuario@correo.com). Intente de nuevo.");
        }
    }

    private double pedirSalario() {
        while (true) {
            try {
                System.out.print("Salario mensual: ");
                double salario = Double.parseDouble(sc.nextLine().trim());
                if (salario > 0) {
                    return salario;
                }
                System.out.println("Error de validacion: El salario debe ser mayor a 0. Intente de nuevo.");
            } catch (NumberFormatException e) {
                System.out.println("Error de formato numerico: Ingrese un valor numerico valido para el salario.");
            }
        }
    }
}
