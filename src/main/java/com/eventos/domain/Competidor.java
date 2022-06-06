package com.eventos.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    
    @OneToMany(mappedBy = "competidor",cascade = CascadeType.ALL)
    private List<Calificacion> calificaciones;
    
    @ManyToOne
    @JoinColumn(name = "rutina_id")
    @JsonIgnore
    private Rutina rutina;
    
    @NotNull
    private int estado; // 1 Creado 2 Activo 3 Terminado 4 Cancelado
    
}
