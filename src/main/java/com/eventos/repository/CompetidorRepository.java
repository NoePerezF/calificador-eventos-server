package com.eventos.repository;

import com.eventos.domain.Competidor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CompetidorRepository extends JpaRepository<Competidor, Long>{
    List<Competidor> findByEstado(int estado);
}
