package com.mycompany.examen.controlador;

import com.mycompany.examen.Modelo.Persistencia.ConexionBD;
import com.mycompany.examen.model.Pago;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * 
 * @author Heiling
 */
public class GestorPagos {

    public GestorPagos() {
    }

    public boolean registrarPago(int prestamoId, double monto, LocalDate fechaPago) 
            throws PrestamoNoEncontradoException, SQLException, IllegalArgumentException, IllegalStateException {

        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a pagar debe ser mayor a $0.");
        }
        if (fechaPago == null) {
            throw new IllegalArgumentException("La fecha de pago no puede ser nula.");
        }

        Connection con = null;
        PreparedStatement psVerificar = null;
        PreparedStatement psInsertarPago = null;
        PreparedStatement psActualizarPrestamo = null;
        ResultSet rs = null;

        try {
            con = ConexionBD.MysConnection();

            // 1. Veificar si el prestamo existe en la base de datosr
            String sqlVerificar = "SELECT id, saldo_pendiente, estado FROM prestamos WHERE id = ?";
            psVerificar = con.prepareStatement(sqlVerificar);
            psVerificar.setInt(1, prestamoId);
            rs = psVerificar.executeQuery();

            if (!rs.next()) {
                throw new PrestamoNoEncontradoException(
                    "Error: El prestamo con ID " + prestamoId + " no existe en la base de datos."
                );
            }

            double saldoActual = rs.getDouble("saldo_pendiente");
            String estadoActual = rs.getString("estado");

            if ("CANCELADO".equalsIgnoreCase(estadoActual) || "PAGADO".equalsIgnoreCase(estadoActual)) {
                throw new IllegalStateException(
                    "El prestamo #" + prestamoId + " ya se encuentra " + estadoActual + " en su totalidad."
                );
            }

            if (monto > (saldoActual + 0.001)) {
                throw new IllegalArgumentException(
                    "El monto ingresado (" + monto + ") supera el saldo pendiente actual (" + saldoActual + ")."
                );
            }

            // Calcular nuevo saldo y determinar nuevo estado
            double nuevoSaldo = Math.max(0.0, saldoActual - monto);
            String nuevoEstado = (nuevoSaldo <= 0.001) ? "CANCELADO" : estadoActual;

            // Registrar el pago en la tabla pagos
            String sqlInsertar = "INSERT INTO pagos (prestamo_id, monto, fecha_pago) VALUES (?, ?, ?)";
            psInsertarPago = con.prepareStatement(sqlInsertar);
            psInsertarPago.setInt(1, prestamoId);
            psInsertarPago.setDouble(2, monto);
            psInsertarPago.setDate(3, Date.valueOf(fechaPago));
            psInsertarPago.executeUpdate();

            // Actualizar el saldo restante y estado del prestamo
            String sqlActualizar = "UPDATE prestamos SET saldo_pendiente = ?, estado = ? WHERE id = ?";
            psActualizarPrestamo = con.prepareStatement(sqlActualizar);
            psActualizarPrestamo.setDouble(1, nuevoSaldo);
            psActualizarPrestamo.setString(2, nuevoEstado);
            psActualizarPrestamo.setInt(3, prestamoId);
            psActualizarPrestamo.executeUpdate();

            return true;

        } catch (PrestamoNoEncontradoException e) {
            throw e;
        } catch (SQLException e) {
            throw e;
        } finally {
            
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar ResultSet: " + e.getMessage());
                }
            }
            if (psVerificar != null) {
                try {
                    psVerificar.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar PreparedStatement psVerificar: " + e.getMessage());
                }
            }
            if (psInsertarPago != null) {
                try {
                    psInsertarPago.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar PreparedStatement psInsertarPago: " + e.getMessage());
                }
            }
            if (psActualizarPrestamo != null) {
                try {
                    psActualizarPrestamo.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar PreparedStatement psActualizarPrestamo: " + e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar Connection: " + e.getMessage());
                }
            }
        }
    }

    public List<Pago> consultarHistorialPagos(int prestamoId) 
            throws PrestamoNoEncontradoException, SQLException {

        List<Pago> historial = new ArrayList<>();
        Connection con = null;
        PreparedStatement psVerificar = null;
        PreparedStatement psConsultar = null;
        ResultSet rsVerificar = null;
        ResultSet rsConsultar = null;

        try {
            con = ConexionBD.MysConnection();

            // Verificar si el prestamo existe
            String sqlVerificar = "SELECT id FROM prestamos WHERE id = ?";
            psVerificar = con.prepareStatement(sqlVerificar);
            psVerificar.setInt(1, prestamoId);
            rsVerificar = psVerificar.executeQuery();

            if (!rsVerificar.next()) {
                throw new PrestamoNoEncontradoException(
                    "Error: El prestamo con ID " + prestamoId + " no existe en la base de datos."
                );
            }

            // Consultar pagos en orden cronologico
            String sqlPagos = "SELECT id, prestamo_id, monto, fecha_pago FROM pagos WHERE prestamo_id = ? ORDER BY fecha_pago ASC, id ASC";
            psConsultar = con.prepareStatement(sqlPagos);
            psConsultar.setInt(1, prestamoId);
            rsConsultar = psConsultar.executeQuery();

            while (rsConsultar.next()) {
                int id = rsConsultar.getInt("id");
                int idPrestamo = rsConsultar.getInt("prestamo_id");
                double monto = rsConsultar.getDouble("monto");
                Date sqlDate = rsConsultar.getDate("fecha_pago");
                LocalDate fechaPago = (sqlDate != null) ? sqlDate.toLocalDate() : null;

                Pago pago = new Pago(id, idPrestamo, fechaPago, monto);
                historial.add(pago);
            }

            return historial;

        } catch (PrestamoNoEncontradoException e) {
            throw e;
        } catch (SQLException e) {
            throw e;
        } finally {
            if (rsVerificar != null) {
                try {
                    rsVerificar.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar rsVerificar: " + e.getMessage());
                }
            }
            if (rsConsultar != null) {
                try {
                    rsConsultar.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar rsConsultar: " + e.getMessage());
                }
            }
            if (psVerificar != null) {
                try {
                    psVerificar.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar psVerificar: " + e.getMessage());
                }
            }
            if (psConsultar != null) {
                try {
                    psConsultar.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar psConsultar: " + e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar Connection: " + e.getMessage());
                }
            }
        }
    }

   
    public List<Pago> filtrarPagosPorRangoFechas(List<Pago> pagos, LocalDate fechaInicio, LocalDate fechaFin) {
        if (pagos == null || fechaInicio == null || fechaFin == null) {
            return new ArrayList<>();
        }
        return pagos.stream()
                .filter(p -> p.getFechaPago() != null &&
                             !p.getFechaPago().isBefore(fechaInicio) &&
                             !p.getFechaPago().isAfter(fechaFin))
                .collect(Collectors.toList());
    }

    
    public List<Pago> filtrarPagosUltimoMes(List<Pago> pagos) {
        if (pagos == null) {
            return new ArrayList<>();
        }
        LocalDate hoy = LocalDate.now();
        LocalDate haceUnMes = hoy.minusMonths(1);
        return pagos.stream()
                .filter(p -> p.getFechaPago() != null &&
                             !p.getFechaPago().isBefore(haceUnMes) &&
                             !p.getFechaPago().isAfter(hoy))
                .collect(Collectors.toList());
    }
    
    
    public void solicitarYRegistrarPago(Scanner scanner) {
        System.out.println();
        System.out.println("Registro De Pago");

        try {
            System.out.print("Ingrese el ID del prestamo: ");
            int prestamoId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Ingrese el monto a pagar: ");
            double monto = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Ingrese la fecha de pago (AAAA-MM-DD): ");
            String fechaStr = scanner.nextLine().trim();
            LocalDate fechaPago;
            if (fechaStr.isEmpty()) {
                fechaPago = LocalDate.now();
                System.out.println("Se utilizara la fecha actual: " + fechaPago);
            } else {
                fechaPago = LocalDate.parse(fechaStr);
            }

            boolean exito = registrarPago(prestamoId, monto, fechaPago);
            if (exito) {
                System.out.println();
                System.out.println("Pago registrado exitosamente para el prestamo #" + prestamoId);
            }

        } catch (NumberFormatException e) {
            System.out.println("Error de formato: Ingrese un valor numerico valido.");
        } catch (DateTimeParseException e) {
            System.out.println("Error de formato de fecha: Use el formato AAAA-MM-DD (ej: 2026-10-13).");
        } catch (PrestamoNoEncontradoException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error de validacion: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Error de estado del prestamo: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de conexion o base de datos: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }
    
    public void mostrarHistorialEnConsola(int prestamoId) {
        System.out.println();
        System.out.println("Historial Cronologico De Pagos");
        try {
            List<Pago> historial = consultarHistorialPagos(prestamoId);
            if (historial.isEmpty()) {
                System.out.println("No se encontraron pagos registrados para el prestamo");
            } else {
                System.out.println("ID PAGO | PRESTAMO ID | FECHA PAGO  | MONTO ABONADO");
                System.out.println("-----------------------------------------------------");
                for (Pago p : historial) {
                    System.out.println(String.format("#%-6d | #%-10d | %-11s | $%,.2f",
                            p.getId(), p.getPrestamoId(), p.getFechaPago(), p.getMonto()));
                }
                System.out.println("Total de pagos registrados: " + historial.size());
            }
        } catch (PrestamoNoEncontradoException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    public void mostrarHistorialUltimoMes(int prestamoId) {
        System.out.println();
        System.out.println("Historial Filtrado: Ultimo Mes Prestamo ");
        try {
            List<Pago> historial = consultarHistorialPagos(prestamoId);
            List<Pago> filtrados = filtrarPagosUltimoMes(historial);

            if (filtrados.isEmpty()) {
                System.out.println("No se encontraron pagos realizados dentro del ultimo mes.");
            } else {
                System.out.println("ID PAGO | PRESTAMO ID | FECHA PAGO  | MONTO ABONADO");
                System.out.println("-----------------------------------------------------");
                for (Pago p : filtrados) {
                    System.out.println(String.format("#%-6d | #%-10d | %-11s | $%,.2f",
                            p.getId(), p.getPrestamoId(), p.getFechaPago(), p.getMonto()));
                }
                System.out.println("Total de pagos en el ultimo mes: " + filtrados.size());
            }
        } catch (PrestamoNoEncontradoException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    public void mostrarHistorialPorRangoFechas(int prestamoId, LocalDate inicio, LocalDate fin) {
        System.out.println();
        System.out.println("Historial Filtrado: Rango " + inicio + " a " + fin + " ");
        try {
            List<Pago> historial = consultarHistorialPagos(prestamoId);
            List<Pago> filtrados = filtrarPagosPorRangoFechas(historial, inicio, fin);

            if (filtrados.isEmpty()) {
                System.out.println("No se encontraron pagos dentro del rango indicado.");
            } else {
                System.out.println("ID PAGO | PRESTAMO ID | FECHA PAGO  | MONTO ABONADO");
                System.out.println("-----------------------------------------------------");
                for (Pago p : filtrados) {
                    System.out.println(String.format("#%-6d | #%-10d | %-11s | $%,.2f",
                            p.getId(), p.getPrestamoId(), p.getFechaPago(), p.getMonto()));
                }
                System.out.println("Total de pagos en el rango: " + filtrados.size());
            }
        } catch (PrestamoNoEncontradoException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }


    public void ejecutarMenu(Scanner scanner) {
        String opcion = "";
        while (!opcion.equals("0")) {
            System.out.println();
            System.out.println("Gestor de pagos(examen)");
            System.out.println("1. Registrar nuevo pago a prestamo existente");
            System.out.println("2. Consultar historial cronologico de pagos");
            System.out.println("3. Filtrar pagos del ultimo mes");
            System.out.println("4. Filtrar pagos por rango de fechas");
            System.out.println("0. Regresar");
            System.out.print("Seleccione una opcion: ");
            opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    solicitarYRegistrarPago(scanner);
                    break;
                case "2":
                    try {
                        System.out.print("Ingrese ID del prestamo a consultar: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        mostrarHistorialEnConsola(id);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: El ID debe ser un numero.");
                    }
                    break;
                case "3":
                    try {
                        System.out.print("Ingrese ID del prestamo: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        mostrarHistorialUltimoMes(id);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: El ID debe ser un numero.");
                    }
                    break;
                case "4":
                    try {
                        System.out.print("Ingrese ID del prestamo: ");
                        int id = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Fecha inicio (AAAA-MM-DD): ");
                        LocalDate ini = LocalDate.parse(scanner.nextLine().trim());
                        System.out.print("Fecha fin (AAAA-MM-DD): ");
                        LocalDate fin = LocalDate.parse(scanner.nextLine().trim());
                        mostrarHistorialPorRangoFechas(id, ini, fin);
                    } catch (NumberFormatException e) {
                        System.out.println("Error: El ID debe ser un numero.");
                    } catch (DateTimeParseException e) {
                        System.out.println("Error de formato de fecha: Debe ser AAAA-MM-DD.");
                    }
                    break;
                case "0":
                    System.out.println("Saliendo del modulo Gestor de Pagos...");
                    break;
                default:
                    System.out.println("Opcion no valida. Seleccione una opcion del 0 al 4.");
            }
        }
    }
}
