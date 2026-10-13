/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.vista;

import com.mycompany.crediya.app.controlador.ClienteControlador;
import com.mycompany.crediya.app.model.Cliente;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

/**
 * Vista modular para el CRUD completo de Clientes.
 * Implementa captura validada y captura granular de excepciones segun su tipo.
 * 
 * @author Heiling
 */
public class ClienteVista {

    private Scanner sc;
    private ClienteControlador clienteCtrl;

    public ClienteVista(Scanner sc, ClienteControlador clienteCtrl) {
        this.sc = sc;
        this.clienteCtrl = clienteCtrl;
    }

    public void mostrarMenu() {
        String op = "";
        while (!op.equals("0")) {
            System.out.println();
            System.out.println("--- Gestion De Clientes ---");
            System.out.println("1. Registrar Cliente");
            System.out.println("2. Listar Clientes");
            System.out.println("3. Editar Cliente");
            System.out.println("4. Eliminar Cliente");
            System.out.println("0. Volver al menu principal");
            System.out.print("Opcion: ");
            op = sc.nextLine().trim();

            try {
                switch (op) {
                    case "1":
                        registrarCliente();
                        break;
                    case "2":
                        listarClientes();
                        break;
                    case "3":
                        editarCliente();
                        break;
                    case "4":
                        eliminarCliente();
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
                System.out.println("Error de estado del cliente: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Error de base de datos MySQL: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error inesperado en el sistema: " + e.getMessage());
            }
        }
    }

    private void registrarCliente() throws SQLException, IllegalArgumentException {
        System.out.println();
        System.out.println("--- Formulario De Registro De Cliente ---");

        String nombre = pedirNombre();
        String doc = pedirDocumento();
        String correo = pedirCorreo();
        String tel = pedirTelefono();

        clienteCtrl.registrarCliente(nombre, doc, correo, tel);
        System.out.println("Cliente registrado exitosamente en el sistema.");
    }

    private void listarClientes() {
        List<Cliente> lista = clienteCtrl.listarClientes();
        if (lista.isEmpty()) {
            System.out.println("No hay clientes registrados en la base de datos.");
        } else {
            System.out.println();
            System.out.println("--- Lista De Clientes Registrados (" + lista.size() + ") ---");
            for (Cliente c : lista) {
                System.out.println(c);
            }
        }
    }

    private void editarCliente() throws SQLException, IllegalArgumentException, IllegalStateException {
        System.out.println();
        System.out.println("--- Formulario De Edicion De Cliente ---");
        int id = pedirIdCliente("ID del Cliente a editar (0 para cancelar): ");
        if (id == 0) return;

        Cliente actual = clienteCtrl.buscarClientePorId(id);
        if (actual == null) {
            throw new IllegalStateException("No existe ningun cliente con el ID " + id + ".");
        }

        System.out.println("Datos actuales del cliente:");
        System.out.println(actual);
        System.out.println("Ingrese los nuevos datos a continuacion:");

        String nuevoNombre = pedirNombre();
        String nuevoDoc = pedirDocumento();
        String nuevoCorreo = pedirCorreo();
        String nuevoTel = pedirTelefono();

        clienteCtrl.actualizarCliente(id, nuevoNombre, nuevoDoc, nuevoCorreo, nuevoTel);
        System.out.println("Cliente #" + id + " actualizado exitosamente.");
    }

    private void eliminarCliente() throws SQLException, IllegalStateException {
        System.out.println();
        System.out.println("--- Formulario De Eliminacion De Cliente ---");
        int id = pedirIdCliente("ID del Cliente a eliminar (0 para cancelar): ");
        if (id == 0) return;

        Cliente actual = clienteCtrl.buscarClientePorId(id);
        if (actual == null) {
            throw new IllegalStateException("No existe ningun cliente con el ID " + id + ".");
        }

        System.out.println("Cliente a eliminar: " + actual.getNombre() + " (Doc: " + actual.getDocumento() + ")");
        System.out.print("Esta seguro de eliminar este registro permanentemente? (S/N): ");
        String confirmacion = sc.nextLine().trim();

        if (confirmacion.equalsIgnoreCase("S")) {
            clienteCtrl.eliminarCliente(id);
            System.out.println("Cliente #" + id + " eliminado exitosamente del sistema.");
        } else {
            System.out.println("Operacion de eliminacion cancelada por el usuario.");
        }
    }

    private int pedirIdCliente(String mensaje) {
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

    private String pedirTelefono() {
        while (true) {
            System.out.print("Telefono de contacto: ");
            String tel = sc.nextLine().trim();
            if (tel.matches("\\d+")) {
                return tel;
            }
            System.out.println("Error de validacion: El telefono debe contener solo numeros. Intente de nuevo.");
        }
    }
}
