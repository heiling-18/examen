/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.Modelo.Persistencia;

import com.mycompany.crediya.app.model.Pago;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Pago / Abono.
 * Centraliza las operaciones CRUD contra la tabla pagos en MySQL.
 * 
 * @author Heiling
 */
public class PagoDAO {

    public boolean guardar(Pago pago) throws SQLException {
        String sql = "INSERT INTO pagos (prestamo_id, fecha_pago, monto) VALUES (?, ?, ?)";
        Connection con = ConexionBD.MysConnection();
        Operaciones.setConnection(con);

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, pago.getPrestamoId());
        ps.setDate(2, java.sql.Date.valueOf(pago.getFechaPago()));
        ps.setDouble(3, pago.getMonto());

        int filasAfectadas = Operaciones.insertar_actualizar_borrar_BD(ps);
        return filasAfectadas > 0;
    }

    public boolean actualizar(Pago pago) throws SQLException {
        String sql = "UPDATE pagos SET monto = ?, fecha_pago = ? WHERE id = ?";
        Connection con = ConexionBD.MysConnection();
        Operaciones.setConnection(con);

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setDouble(1, pago.getMonto());
        ps.setDate(2, java.sql.Date.valueOf(pago.getFechaPago()));
        ps.setInt(3, pago.getId());

        int filasAfectadas = Operaciones.insertar_actualizar_borrar_BD(ps);
        return filasAfectadas > 0;
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM pagos WHERE id = ?";
        Connection con = ConexionBD.MysConnection();
        Operaciones.setConnection(con);

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);

        int filasAfectadas = Operaciones.insertar_actualizar_borrar_BD(ps);
        return filasAfectadas > 0;
    }

    public Pago buscarPorId(int id) {
        String sql = "SELECT * FROM pagos WHERE id = ?";
        try {
            Connection con = ConexionBD.MysConnection();
            Operaciones.setConnection(con);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = Operaciones.consultar_BD(ps);

            if (rs != null && rs.next()) {
                int prestamoId = rs.getInt("prestamo_id");
                LocalDate fechaPago = rs.getDate("fecha_pago").toLocalDate();
                double monto = rs.getDouble("monto");

                return new Pago(id, prestamoId, fechaPago, monto);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar pago por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Pago> listarPorPrestamo(int prestamoId) {
        List<Pago> lista = new ArrayList<>();
        String sql = "SELECT * FROM pagos WHERE prestamo_id = ?";
        try {
            Connection con = ConexionBD.MysConnection();
            Operaciones.setConnection(con);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, prestamoId);

            ResultSet rs = Operaciones.consultar_BD(ps);

            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    LocalDate fechaPago = rs.getDate("fecha_pago").toLocalDate();
                    double monto = rs.getDouble("monto");

                    Pago pago = new Pago(id, prestamoId, fechaPago, monto);
                    lista.add(pago);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar pagos: " + e.getMessage());
        }
        return lista;
    }
}