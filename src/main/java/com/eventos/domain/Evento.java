
package com.eventos.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Evento implements Serializable{

    private String nombre;
    private List<Juez> ejecucion;
    private List<Juez> impresionArtistica;
    private List<Juez> dificultad;

    public Evento(String nombre) {
        this.nombre = nombre;
        this.ejecucion = new ArrayList<>();
        this.impresionArtistica = new ArrayList<>();
        this.dificultad = new ArrayList<>();
    }
        

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
   

    public List<Juez> getEjecucion() {
        return ejecucion;
    }

    public List<Juez> getImpresionArtistica() {
        return impresionArtistica;
    }

    public List<Juez> getDificultad() {
        return dificultad;
    }

    public void setEjecucion(List<Juez> ejecucion) {
        this.ejecucion = ejecucion;
    }

    public void setImpresionArtistica(List<Juez> impresionArtistica) {
        this.impresionArtistica = impresionArtistica;
    }

    public void setDificultad(List<Juez> dificultad) {
        this.dificultad = dificultad;
    }
    
    
    
    
}
