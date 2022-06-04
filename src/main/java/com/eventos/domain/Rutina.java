package com.eventos.domain;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rutina implements Serializable{
    
    @Id
    private Long id;
    
    @OneToMany(mappedBy = "rutina")
    private List<Competidor> competidores;
}
