package com.eventos.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Competidor implements Serializable{
    
    @Id
    private Long id;
    
    private String nombre;
    
    private List<Calificacion> calificaciones;
    
    @ManyToOne
    @JoinColumn(name = "rutina_id")
    private Rutina rutina;
}
