CREATE DATABASE IF NOT EXISTS crediya_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE crediya_db;

-- 1. TABLA: empleados

CREATE TABLE IF NOT EXISTS empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    documento VARCHAR(30) NOT NULL UNIQUE,
    rol VARCHAR(30) NOT NULL,
    correo VARCHAR(80) NOT NULL,
    salario DECIMAL(10,2) NOT NULL
);


-- 2. TABLA: clientes

CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    documento VARCHAR(30) NOT NULL UNIQUE,
    correo VARCHAR(80) NOT NULL,
    telefono VARCHAR(20) NOT NULL
);


-- 3. TABLA: prestamos

CREATE TABLE IF NOT EXISTS prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    empleado_id INT NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    interes DECIMAL(5,2) NOT NULL,
    cuotas INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_vencimiento DATE NOT NULL,
    monto_total DECIMAL(12,2) NOT NULL,
    valor_cuota DECIMAL(12,2) NOT NULL,
    saldo_pendiente DECIMAL(12,2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    CONSTRAINT fk_prestamo_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_prestamo_empleado FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE RESTRICT ON UPDATE CASCADE
);


-- 4. TABLA: pagos

CREATE TABLE IF NOT EXISTS pagos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    prestamo_id INT NOT NULL,
    fecha_pago DATE NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_pago_prestamo FOREIGN KEY (prestamo_id) REFERENCES prestamos(id) ON DELETE CASCADE ON UPDATE CASCADE
);
