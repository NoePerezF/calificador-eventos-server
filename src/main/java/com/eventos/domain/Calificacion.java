package com.eventos.domain;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Calificacion implements Serializable{
    
    @Id
    private Long id;
    
    private Double calificacion;
    
    private Juez juez;
    
}
