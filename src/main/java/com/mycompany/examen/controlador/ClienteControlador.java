/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.controlador;

import com.mycompany.crediya.app.Modelo.Persistencia.ClienteDAO;
import com.mycompany.crediya.app.model.Cliente;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para la gestion de Clientes.
 * Aplica reglas de negocio y lanza excepciones especificas segun el tipo de error.
 * 
 * @author Heiling
 */
public class ClienteControlador {

    private ClienteDAO clienteDAO;

    public ClienteControlador() {
        this.clienteDAO = new ClienteDAO();
    }

    public void registrarCliente(String nombre, String documento, String correo, String telefono) throws SQLException, IllegalArgumentException {
        if (nombre == null || nombre.trim().isEmpty() || !nombre.matches("^[a-zA-Z ]+$")) {
            throw new IllegalArgumentException("El nombre solo puede contener letras y no puede estar vacio.");
        }
        if (documento == null || !documento.matches("\\d+")) {
            throw new IllegalArgumentException("El documento debe contener unicamente numeros.");
        }
        if (telefono == null || !telefono.matches("\\d+")) {
            throw new IllegalArgumentException("El telefono debe contener unicamente numeros.");
        }
        if (correo == null || !correo.contains("@") || !correo.contains(".")) {
            throw new IllegalArgumentException("El correo electronico debe contener un formato valido con @ y punto.");
        }

        Cliente nuevoCliente = new Cliente(0, nombre, documento, correo, telefono);
        boolean exito = clienteDAO.guardar(nuevoCliente);
        if (!exito) {
            throw new SQLException("No se pudo registrar el cliente en la base de datos.");
        }
    }

    public void actualizarCliente(int id, String nombre, String documento, String correo, String telefono) throws SQLException, IllegalArgumentException, IllegalStateException {
        Cliente existente = clienteDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalStateException("No existe ningun cliente registrado con el ID " + id + ".");
        }

        if (nombre == null || nombre.trim().isEmpty() || !nombre.matches("^[a-zA-Z ]+$")) {
            throw new IllegalArgumentException("El nombre solo puede contener letras y no puede estar vacio.");
        }
        if (documento == null || !documento.matches("\\d+")) {
            throw new IllegalArgumentException("El documento debe contener unicamente numeros.");
        }
        if (telefono == null || !telefono.matches("\\d+")) {
            throw new IllegalArgumentException("El telefono debe contener unicamente numeros.");
        }
        if (correo == null || !correo.contains("@") || !correo.contains(".")) {
            throw new IllegalArgumentException("El correo electronico debe contener un formato valido con @ y punto.");
        }

        Cliente clienteActualizado = new Cliente(id, nombre, documento, correo, telefono);
        boolean exito = clienteDAO.actualizar(clienteActualizado);
        if (!exito) {
            throw new SQLException("No se pudo actualizar el cliente en la base de datos.");
        }
    }

    public void eliminarCliente(int id) throws SQLException, IllegalStateException {
        Cliente existente = clienteDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalStateException("No existe ningun cliente registrado con el ID " + id + ".");
        }

        boolean exito = clienteDAO.eliminar(id);
        if (!exito) {
            throw new SQLException("No se pudo eliminar el cliente de la base de datos.");
        }
    }

    public List<Cliente> listarClientes() {
        return clienteDAO.listarTodos();
    }

    public Cliente buscarClientePorId(int id) {
        return clienteDAO.buscarPorId(id);
    }
}