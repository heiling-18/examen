/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.Modelo.Persistencia;

import com.mycompany.crediya.app.model.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Cliente.
 * Centraliza las operaciones CRUD contra la tabla clientes en MySQL.
 * 
 * @author Heiling
 */
public class ClienteDAO {

    public boolean guardar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO clientes (nombre, documento, correo, telefono) VALUES (?, ?, ?, ?)";
        try {
            Connection con = ConexionBD.MysConnection();
            Operaciones.setConnection(con);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDocumento());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getTelefono());

            int filasAfectadas = Operaciones.insertar_actualizar_borrar_BD(ps);
            return filasAfectadas > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new SQLException("El documento " + cliente.getDocumento() + " ya pertenece a otro cliente registrado.");
            }
            throw e;
        }
    }

    public boolean actualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE clientes SET nombre = ?, documento = ?, correo = ?, telefono = ? WHERE id = ?";
        try {
            Connection con = ConexionBD.MysConnection();
            Operaciones.setConnection(con);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDocumento());
            ps.setString(3, cliente.getCorreo());
            ps.setString(4, cliente.getTelefono());
            ps.setInt(5, cliente.getId());

            int filasAfectadas = Operaciones.insertar_actualizar_borrar_BD(ps);
            return filasAfectadas > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new SQLException("El documento " + cliente.getDocumento() + " ya pertenece a otro cliente.");
            }
            throw e;
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";
        try {
            Connection con = ConexionBD.MysConnection();
            Operaciones.setConnection(con);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = Operaciones.insertar_actualizar_borrar_BD(ps);
            return filasAfectadas > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new SQLException("No se puede eliminar el cliente porque tiene prestamos asociados.");
            }
            throw e;
        }
    }

    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes";
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
                    String telefono = rs.getString("telefono");

                    Cliente cliente = new Cliente(id, nombre, documento, correo, telefono);
                    lista.add(cliente);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        }
        return lista;
    }

    public Cliente buscarPorId(int id) {
        String sql = "SELECT * FROM clientes WHERE id = ?";
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
                String telefono = rs.getString("telefono");

                return new Cliente(id, nombre, documento, correo, telefono);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar cliente por ID: " + e.getMessage());
        }
        return null;
    }
}
