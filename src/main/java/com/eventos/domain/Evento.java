
package com.eventos.domain;

import com.sun.istack.NotNull;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
@Table(name = "evento")
public class Evento implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;

    @NotNull
    private String nombre;
    
    @NotNull
    private Date fecha;
    
    @NotNull
    private int estado; // 1 Creado 2 Activo 3 Terminado 4 Cancelado
    
    @OneToMany(mappedBy = "evento",cascade = CascadeType.ALL)
    private List<EventoJuezEjecucion> ejecucion;
    
    @OneToMany(mappedBy = "evento",cascade = CascadeType.ALL)
    private List<EventoJuezImpresionArtistica> impresionArtistica;
    
    @OneToMany(mappedBy = "evento",cascade = CascadeType.ALL)
    private List<EventoJuezDificultad> dificultad; 
  
    
}
