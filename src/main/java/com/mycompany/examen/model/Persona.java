/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.crediya.app.model;

/**
 *
 * @author Heiling
 */
public abstract class Persona {
    private int id;
    private String nombre;
    private String documento;
    private String correo;

    public Persona(int id, String nombre, String documento, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.correo = correo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    @Override
    public String toString() {
        return "ID: " + id + 
               " | Nombre: " + nombre + 
               " | Documento: " + documento + 
               " | Correo: " + correo;
    }
}
    
    
    
 


