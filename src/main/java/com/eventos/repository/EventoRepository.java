
package com.eventos.repository;

import com.eventos.domain.Evento;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long>{
    Optional<Evento> findByEstado(int estado);
}
