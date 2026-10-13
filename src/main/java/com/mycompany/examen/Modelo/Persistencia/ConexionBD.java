/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.Modelo.Persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestor de conexion a la base de datos MySQL aplicando el patron creacional Singleton.
 * Garantiza una unica conexion activa y reutilizable durante todo el ciclo de vida
 * de la aplicacion, evitando la saturacion de conexiones en el servidor MySQL.
 * 
 * @author Heiling
 */
public abstract class ConexionBD {
    private static String url = "";
    private static String user = "";
    private static String password = "";
    public static Connection con = null;

    public static Connection MysConnection() throws SQLException {
        url = "jdbc:mysql://localhost:3306/crediya_db";
        user = "root";
        password = "Heiling2009@";
        return getConnection(url, user, password);
    }

    private static Connection getConnection(String url, String user, String password) {
        try {
            // Patron Singleton: si la conexion no existe o se cerro, se crea; de lo contrario, se reutiliza
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException ex) {
            System.out.println("Error al conectar a la base de datos: " + ex.getMessage());
        }
        return con;
    }
}
