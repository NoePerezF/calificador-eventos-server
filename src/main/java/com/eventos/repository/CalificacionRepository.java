package com.eventos.repository;

import com.eventos.domain.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CalificacionRepository extends JpaRepository<Calificacion, Long>{
    
}
