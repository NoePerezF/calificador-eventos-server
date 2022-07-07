
package com.eventos.repository;

import com.eventos.domain.Evento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long>{
    Optional<Evento> findByEstado(int estado);
    
    @Query(value ="select e.nombre, r.nombre, c.nombre as Nombre, j.tipo as Tipo,"+
        "max(ca.calificacion) filter (where j.nombre  = 'Juez 1') as Juez1, "+
	"max(ca.calificacion) filter (where j.nombre  = 'Juez 2') as Juez2,"+
	"max(ca.calificacion) filter (where j.nombre  = 'Juez 3') as Juez3,"+
	"max(ca.calificacion) filter (where j.nombre  = 'Juez 4') as Juez4,"+
	"max(ca.calificacion) filter (where j.nombre  = 'Juez 5') as Juez5 "+
	"from  evento as e inner join rutina as r on e.id = r.evento_id  inner join competidor as c on r.id = c.rutina_id "+
	"inner join calificacion as ca on c.id = ca.competidor_id inner join juez as j on ca.juez_id = j.id where e.id = :eid group by j.tipo,c.nombre,r.nombre, e.nombre "+
	"order by c.nombre, j.tipo",nativeQuery=true)
    List<Object> findCustomEvento(@Param("eid")Long eid);
}
