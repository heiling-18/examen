/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.Modelo.Persistencia;

import com.mycompany.crediya.app.model.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author Heiling
 */
public class GestorArchivos {

    //  Exportar Empleados a empleados.txt
    public static boolean exportarEmpleados(List<Empleado> lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("empleados.txt"))) {
            bw.write("ID | NOMBRE | DOCUMENTO | CORREO | ROL | SALARIO");
            bw.newLine();
            bw.write("--------------------------------------------------------------------------------");
            bw.newLine();
            for (Empleado e : lista) {
                bw.write(e.getId() + " | " + e.getNombre() + " | " + e.getDocumento() + " | " + 
                         e.getCorreo() + " | " + e.getRol() + " | $" + e.getSalario());
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error al escribir empleados.txt: " + e.getMessage());
            return false;
        }
    }

    // Exportar Clientes a clientes.txt
    public static boolean exportarClientes(List<Cliente> lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("clientes.txt"))) {
            bw.write("ID | NOMBRE | DOCUMENTO | CORREO | TELEFONO");
            bw.newLine();
            bw.write("--------------------------------------------------------------------------------");
            bw.newLine();
            for (Cliente c : lista) {
                bw.write(c.getId() + " | " + c.getNombre() + " | " + c.getDocumento() + " | " + 
                         c.getCorreo() + " | " + c.getTelefono());
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error al escribir clientes.txt: " + e.getMessage());
            return false;
        }
    }

    //  Exportar Prestamos a prestamos.txt
    public static boolean exportarPrestamos(List<Prestamo> lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("prestamos.txt"))) {
            bw.write("ID | CLIENTE | EMPLEADO | MONTO | TOTAL | SALDO | CUOTAS | INICIO | VENCE | ESTADO");
            bw.newLine();
            bw.write("--------------------------------------------------------------------------------------------------------");
            bw.newLine();
            for (Prestamo p : lista) {
                bw.write(p.getId() + " | " + 
                         p.getCliente().getNombre() + " | " + 
                         p.getEmpleado().getNombre() + " | $" + 
                         p.getMonto() + " | $" + 
                         p.getMontoTotal() + " | $" + 
                         p.getSaldoPendiente() + " | " + 
                         p.getCuotas() + " | " + 
                         p.getFechaInicio() + " | " + 
                         p.getFechaVencimiento() + " | " + 
                         p.getEstado());
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error al escribir prestamos.txt: " + e.getMessage());
            return false;
        }
    }

    // Exportar Pagos a pagos.
    public static boolean exportarPagos(List<Pago> lista) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("pagos.txt"))) {
            bw.write("ID | PRESTAMO ID | FECHA PAGO | MONTO ABONADO");
            bw.newLine();
            bw.write("--------------------------------------------------------------------------------");
            bw.newLine();
            for (Pago p : lista) {
                bw.write(p.getId() + " | " + p.getPrestamoId() + " | " + p.getFechaPago() + " | $" + p.getMonto());
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error al escribir pagos.txt: " + e.getMessage());
            return false;
        }
    }
}