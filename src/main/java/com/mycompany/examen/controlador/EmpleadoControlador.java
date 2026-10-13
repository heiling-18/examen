/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.controlador;

import com.mycompany.crediya.app.Modelo.Persistencia.EmpleadoDAO;
import com.mycompany.crediya.app.model.Empleado;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador para la gestion de Empleados.
 * Aplica reglas de negocio y lanza excepciones especificas segun el tipo de error.
 * 
 * @author Heiling
 */
public class EmpleadoControlador {

    private EmpleadoDAO empleadoDAO;

    public EmpleadoControlador() {
        this.empleadoDAO = new EmpleadoDAO();
    }

    public void registrarEmpleado(String nombre, String documento, String rol, String correo, double salario) throws SQLException, IllegalArgumentException {
        if (nombre == null || nombre.trim().isEmpty() || !nombre.matches("^[a-zA-Z ]+$")) {
            throw new IllegalArgumentException("El nombre del empleado solo puede contener letras y no puede estar vacio.");
        }
        if (documento == null || !documento.matches("\\d+")) {
            throw new IllegalArgumentException("El documento debe contener unicamente numeros.");
        }
        if (rol == null || rol.trim().isEmpty() || !rol.matches("^[a-zA-Z ]+$")) {
            throw new IllegalArgumentException("El cargo o rol solo puede contener letras y no puede estar vacio.");
        }
        if (salario <= 0) {
            throw new IllegalArgumentException("El salario mensual debe ser un valor mayor a $0.");
        }
        if (correo == null || !correo.contains("@") || !correo.contains(".")) {
            throw new IllegalArgumentException("El formato del correo electronico debe contener @ y punto.");
        }

        Empleado nuevoEmpleado = new Empleado(0, nombre, documento, correo, rol, salario);
        boolean exito = empleadoDAO.guardar(nuevoEmpleado);
        if (!exito) {
            throw new SQLException("No se pudo registrar el empleado en la base de datos.");
        }
    }

    public void actualizarEmpleado(int id, String nombre, String documento, String rol, String correo, double salario) throws SQLException, IllegalArgumentException, IllegalStateException {
        Empleado existente = empleadoDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalStateException("No existe ningun empleado registrado con el ID " + id + ".");
        }

        if (nombre == null || nombre.trim().isEmpty() || !nombre.matches("^[a-zA-Z ]+$")) {
            throw new IllegalArgumentException("El nombre del empleado solo puede contener letras y no puede estar vacio.");
        }
        if (documento == null || !documento.matches("\\d+")) {
            throw new IllegalArgumentException("El documento debe contener unicamente numeros.");
        }
        if (rol == null || rol.trim().isEmpty() || !rol.matches("^[a-zA-Z ]+$")) {
            throw new IllegalArgumentException("El cargo o rol solo puede contener letras y no puede estar vacio.");
        }
        if (salario <= 0) {
            throw new IllegalArgumentException("El salario mensual debe ser un valor mayor a $0.");
        }
        if (correo == null || !correo.contains("@") || !correo.contains(".")) {
            throw new IllegalArgumentException("El formato del correo electronico debe contener @ y punto.");
        }

        Empleado empleadoActualizado = new Empleado(id, nombre, documento, correo, rol, salario);
        boolean exito = empleadoDAO.actualizar(empleadoActualizado);
        if (!exito) {
            throw new SQLException("No se pudo actualizar el empleado en la base de datos.");
        }
    }

    public void eliminarEmpleado(int id) throws SQLException, IllegalStateException {
        Empleado existente = empleadoDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalStateException("No existe ningun empleado registrado con el ID " + id + ".");
        }

        boolean exito = empleadoDAO.eliminar(id);
        if (!exito) {
            throw new SQLException("No se pudo eliminar el empleado de la base de datos.");
        }
    }

    public List<Empleado> listarEmpleados() {
        return empleadoDAO.listarTodos();
    }

    public Empleado buscarEmpleadoPorId(int id) {
        return empleadoDAO.buscarPorId(id);
    }
}