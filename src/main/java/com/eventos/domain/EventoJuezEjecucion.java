
package com.eventos.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "evento_juez_ejecucion")
public class EventoJuezEjecucion implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private double calificacion;
    
    @ManyToOne
    @JoinColumn(name = "evento_id")
    @JsonIgnore
    private Evento evento;
    
    @ManyToOne
    @JoinColumn(name = "juez_id")
    private Juez juez;
    
}
