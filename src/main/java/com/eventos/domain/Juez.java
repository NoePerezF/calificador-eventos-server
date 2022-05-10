
package com.eventos.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "juez")
public class Juez implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;
    
    @NotNull
    private String nombre;
    
    @NotNull
    private int tipo; //1 Ejecucion; 2 Impresion artistica; 3 Dificultad
    
    @NotNull
    private int numero;
    
    @OneToMany(mappedBy = "juez")
    @JsonIgnore
    List<EventoJuezEjecucion> ejecucion;
    
    @OneToMany(mappedBy = "juez")
    @JsonIgnore
    List<EventoJuezImpresionArtistica> impresionArtistica;
    
    @OneToMany(mappedBy = "juez")
    @JsonIgnore
    List<EventoJuezDificultad> dificultas;
    
   
    
    
    
}
