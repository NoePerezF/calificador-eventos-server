
package com.eventos.repository;

import com.eventos.domain.Juez;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JuezRepository extends JpaRepository<Juez, Long>{
    Optional<Juez> findByTipoAndNumero(int tipo,int numero);
}
