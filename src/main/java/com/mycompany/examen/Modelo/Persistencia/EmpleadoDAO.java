/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.Modelo.Persistencia;

import com.mycompany.crediya.app.model.Empleado;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Empleado.
 * Centraliza las operaciones CRUD contra la tabla empleados en MySQL.
 * 
 * @author Heiling
 */
public class EmpleadoDAO {

    public boolean guardar(Empleado empleado) throws SQLException {
        String sql = "INSERT INTO empleados (nombre, documento, correo, rol, salario) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection con = ConexionBD.MysConnection();
            Operaciones.setConnection(con);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getDocumento());
            ps.setString(3, empleado.getCorreo());
            ps.setString(4, empleado.getRol());
            ps.setDouble(5, empleado.getSalario());

            int filasAfectadas = Operaciones.insertar_actualizar_borrar_BD(ps);
            return filasAfectadas > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new SQLException("El documento " + empleado.getDocumento() + " ya pertenece a otro empleado registrado.");
            }
            throw e;
        }
    }

    public boolean actualizar(Empleado empleado) throws SQLException {
        String sql = "UPDATE empleados SET nombre = ?, documento = ?, correo = ?, rol = ?, salario = ? WHERE id = ?";
        try {
            Connection con = ConexionBD.MysConnection();
            Operaciones.setConnection(con);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, empleado.getNombre());
            ps.setString(2, empleado.getDocumento());
            ps.setString(3, empleado.getCorreo());
            ps.setString(4, empleado.getRol());
            ps.setDouble(5, empleado.getSalario());
            ps.setInt(6, empleado.getId());

            int filasAfectadas = Operaciones.insertar_actualizar_borrar_BD(ps);
            return filasAfectadas > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new SQLException("El documento " + empleado.getDocumento() + " ya pertenece a otro empleado.");
            }
            throw e;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM empleados WHERE id = ?";
        try {
            Connection con = ConexionBD.MysConnection();
            Operaciones.setConnection(con);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = Operaciones.insertar_actualizar_borrar_BD(ps);
            return filasAfectadas > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new SQLException("No se puede eliminar el empleado porque tiene prestamos autorizados a su cargo.");
            }
            throw e;
        }
    }

    public List<Empleado> listarTodos() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleados";
        try {
            Connection con = ConexionBD.MysConnection();
            Operaciones.setConnection(con);

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = Operaciones.consultar_BD(ps);

            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nombre = rs.getString("nombre");
                    String documento = rs.getString("documento");
                    String correo = rs.getString("correo");
                    String rol = rs.getString("rol");
                    double salario = rs.getDouble("salario");

                    Empleado empleado = new Empleado(id, nombre, documento, correo, rol, salario);
                    lista.add(empleado);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar empleados: " + e.getMessage());
        }
        return lista;
    }

    public Empleado buscarPorId(int id) {
        String sql = "SELECT * FROM empleados WHERE id = ?";
        try {
            Connection con = ConexionBD.MysConnection();
            Operaciones.setConnection(con);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = Operaciones.consultar_BD(ps);

            if (rs != null && rs.next()) {
                String nombre = rs.getString("nombre");
                String documento = rs.getString("documento");
                String correo = rs.getString("correo");
                String rol = rs.getString("rol");
                double salario = rs.getDouble("salario");

                return new Empleado(id, nombre, documento, correo, rol, salario);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar empleado por ID: " + e.getMessage());
        }
        return null;
    }
}