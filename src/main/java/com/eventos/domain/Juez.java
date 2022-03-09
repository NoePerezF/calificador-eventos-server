
package com.eventos.domain;

import java.io.Serializable;
import org.springframework.lang.Nullable;



public class Juez implements Serializable{

    private String id;
    private String nombre;
    private int tipo; //1 Ejecucion; 2 Impresion artistica; 3 Dificultad
    @Nullable
    private double calificacion;

    

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
    
    
    
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }
    
    
    
}
