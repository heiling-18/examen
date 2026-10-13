/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.examen.controlador;

/**
 * 
 * @author Heiling
 */
public class PrestamoNoEncontradoException extends Exception {

    public PrestamoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public PrestamoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
