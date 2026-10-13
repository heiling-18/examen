/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.Modelo.Persistencia;

import com.mycompany.crediya.app.model.Cliente;
import com.mycompany.crediya.app.model.Empleado;
import com.mycompany.crediya.app.model.EstadoPrestamo;
import com.mycompany.crediya.app.model.Prestamo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la entidad Prestamo.
 * Centraliza las operaciones CRUD contra la tabla prestamos en MySQL.
 * 
 * @author Heiling
 */
public class PrestamoDAO {

    public boolean guardar(Prestamo prestamo) throws SQLException {
        String sql = "INSERT INTO prestamos (cliente_id, empleado_id, monto, interes, cuotas, " +
                     "fecha_inicio, fecha_vencimiento, monto_total, valor_cuota, saldo_pendiente, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = ConexionBD.MysConnection();
        Operaciones.setConnection(con);

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, prestamo.getCliente().getId());
        ps.setInt(2, prestamo.getEmpleado().getId());
        ps.setDouble(3, prestamo.getMonto());
        ps.setDouble(4, prestamo.getInteres());
        ps.setInt(5, prestamo.getCuotas());
        ps.setDate(6, java.sql.Date.valueOf(prestamo.getFechaInicio()));
        ps.setDate(7, java.sql.Date.valueOf(prestamo.getFechaVencimiento()));
        ps.setDouble(8, prestamo.getMontoTotal());
        ps.setDouble(9, prestamo.getValorCuota());
        ps.setDouble(10, prestamo.getSaldoPendiente());
        ps.setString(11, prestamo.getEstado().name());

        int filasAfectadas = Operaciones.insertar_actualizar_borrar_BD(ps);
        return filasAfectadas > 0;
    }

    public boolean actualizar(Prestamo prestamo) throws SQLException {
        String sql = "UPDATE prestamos SET cliente_id = ?, empleado_id = ?, monto = ?, interes = ?, " +
                     "cuotas = ?, fecha_inicio = ?, fecha_vencimiento = ?, monto_total = ?, " +
                     "valor_cuota = ?, saldo_pendiente = ?, estado = ? WHERE id = ?";
        Connection con = ConexionBD.MysConnection();
        Operaciones.setConnection(con);

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, prestamo.getCliente().getId());
        ps.setInt(2, prestamo.getEmpleado().getId());
        ps.setDouble(3, prestamo.getMonto());
        ps.setDouble(4, prestamo.getInteres());
        ps.setInt(5, prestamo.getCuotas());
        ps.setDate(6, java.sql.Date.valueOf(prestamo.getFechaInicio()));
        ps.setDate(7, java.sql.Date.valueOf(prestamo.getFechaVencimiento()));
        ps.setDouble(8, prestamo.getMontoTotal());
        ps.setDouble(9, prestamo.getValorCuota());
        ps.setDouble(10, prestamo.getSaldoPendiente());
        ps.setString(11, prestamo.getEstado().name());
        ps.setInt(12, prestamo.getId());

        int filasAfectadas = Operaciones.insertar_actualizar_borrar_BD(ps);
        return filasAfectadas > 0;
    }

    public boolean actualizarSaldoYEstado(int idPrestamo, double nuevoSaldo, EstadoPrestamo nuevoEstado) throws SQLException {
        String sql = "UPDATE prestamos SET saldo_pendiente = ?, estado = ? WHERE id = ?";
        Connection con = ConexionBD.MysConnection();
        Operaciones.setConnection(con);

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setDouble(1, nuevoSaldo);
        ps.setString(2, nuevoEstado.name());
        ps.setInt(3, idPrestamo);

        int filasAfectadas = Operaciones.insertar_actualizar_borrar_BD(ps);
        return filasAfectadas > 0;
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        try {
            Connection con = ConexionBD.MysConnection();
            Operaciones.setConnection(con);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int filasAfectadas = Operaciones.insertar_actualizar_borrar_BD(ps);
            return filasAfectadas > 0;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new SQLException("No se puede eliminar el prestamo porque tiene pagos asociados.");
            }
            throw e;
        }
    }

    public Prestamo buscarPorId(int id) {
        String sql = "SELECT * FROM prestamos WHERE id = ?";
        try {
            Connection con = ConexionBD.MysConnection();
            Operaciones.setConnection(con);

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = Operaciones.consultar_BD(ps);

            if (rs != null && rs.next()) {
                Cliente cliente = new ClienteDAO().buscarPorId(rs.getInt("cliente_id"));
                Empleado empleado = new EmpleadoDAO().buscarPorId(rs.getInt("empleado_id"));

                double monto = rs.getDouble("monto");
                double interes = rs.getDouble("interes");
                int cuotas = rs.getInt("cuotas");
                LocalDate fechaInicio = rs.getDate("fecha_inicio").toLocalDate();
                LocalDate fechaVencimiento = rs.getDate("fecha_vencimiento").toLocalDate();
                double montoTotal = rs.getDouble("monto_total");
                double valorCuota = rs.getDouble("valor_cuota");
                double saldoPendiente = rs.getDouble("saldo_pendiente");
                EstadoPrestamo estado = EstadoPrestamo.valueOf(rs.getString("estado"));

                return new Prestamo(id, cliente, empleado, monto, interes, cuotas, 
                                    fechaInicio, fechaVencimiento, montoTotal, 
                                    valorCuota, saldoPendiente, estado);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar prestamo: " + e.getMessage());
        }
        return null;
    }

    public List<Prestamo> listarTodos() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT * FROM prestamos";
        try {
            Connection con = ConexionBD.MysConnection();
            Operaciones.setConnection(con);

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = Operaciones.consultar_BD(ps);

            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Cliente cliente = new ClienteDAO().buscarPorId(rs.getInt("cliente_id"));
                    Empleado empleado = new EmpleadoDAO().buscarPorId(rs.getInt("empleado_id"));

                    double monto = rs.getDouble("monto");
                    double interes = rs.getDouble("interes");
                    int cuotas = rs.getInt("cuotas");
                    LocalDate fechaInicio = rs.getDate("fecha_inicio").toLocalDate();
                    LocalDate fechaVencimiento = rs.getDate("fecha_vencimiento").toLocalDate();
                    double montoTotal = rs.getDouble("monto_total");
                    double valorCuota = rs.getDouble("valor_cuota");
                    double saldoPendiente = rs.getDouble("saldo_pendiente");
                    EstadoPrestamo estado = EstadoPrestamo.valueOf(rs.getString("estado"));

                    Prestamo prestamo = new Prestamo(id, cliente, empleado, monto, interes, cuotas, 
                                                     fechaInicio, fechaVencimiento, montoTotal, 
                                                     valorCuota, saldoPendiente, estado);
                    lista.add(prestamo);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar prestamos: " + e.getMessage());
        }
        return lista;
    }
}
