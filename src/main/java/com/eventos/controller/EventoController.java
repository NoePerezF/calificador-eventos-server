
package com.eventos.controller;

import com.eventos.domain.Calificacion;
import com.eventos.domain.Competidor;
import com.eventos.domain.CompetidorVo;
import com.eventos.domain.Evento;
import com.eventos.domain.EventoVo;

import com.eventos.domain.Juez;
import com.eventos.domain.Rutina;
import com.eventos.repository.CalificacionRepository;
import com.eventos.repository.CompetidorRepository;
import com.eventos.repository.EventoRepository;
import com.eventos.util.MensajeReponse;
import com.eventos.repository.JuezRepository;
import com.eventos.repository.RutinaRepository;
import com.eventos.util.RegistroResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class EventoController {
    private final int NUMERO_JUECES = 5;
    private ObjectMapper maper = new ObjectMapper();
    private SimpMessagingTemplate template;
    
    @Autowired
    private EventoRepository repo;
    
    @Autowired
    private JuezRepository repoJuez;
    
    @Autowired
    private CalificacionRepository repoCalificacion;
    
    @Autowired
    private CompetidorRepository repoCompetidos;
    
    @Autowired
    private RutinaRepository repoRutina;
    
    
    @Autowired
        public EventoController(SimpMessagingTemplate template) {
            this.template = template;
        }
        
    @Autowired
    DataSource dataSource;
    
     @Value("classpath:reports/reporte_event.jasper")
    private Resource res;
    
    @GetMapping("/api/ping")
    public String ping() throws JsonProcessingException{
      return(maper.writeValueAsString(new MensajeReponse(1,"Ok")) );
    }
    @PostMapping("/api/nuevoevento")
    public String nuevoEvento(@RequestBody Evento evento) throws JsonProcessingException{
        evento.setEstado(1);
        evento.setRutinas(new ArrayList<>());
        try {
            repo.save(evento);
        } catch (Exception e) {
            return(maper.writeValueAsString(new MensajeReponse(2,"Error al registrar evento")) );
        }
        
        return(maper.writeValueAsString(new MensajeReponse(1,"Evento creado con exito")) );
    }
    @PostMapping("/api/nuevarutina")
    public String nuevaRutina(@RequestBody Rutina rutina) throws JsonProcessingException{
        if(repo.findById(rutina.getEvento().getId()).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"No existe el evento")) );
        }
        rutina.setEvento(repo.findById(rutina.getEvento().getId()).get());
        rutina.setEstado(1);
        rutina.setCompetidores(new ArrayList<>());
        try {
            repoRutina.save(rutina);
        } catch (Exception e) {
            return(maper.writeValueAsString(new MensajeReponse(2,"Error al crear la rutina")) );
        }
        return(maper.writeValueAsString(new MensajeReponse(1,"Rutina registrada con exito")) );
    }
    @PostMapping("/api/nuevocompetidor")
    public String nuevoCompetidor(@RequestBody Competidor competidor) throws JsonProcessingException{
        if(repoRutina.findById(competidor.getRutina().getId()).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"No existe la rutina")) );
        }
        competidor.setRutina(repoRutina.findById(competidor.getRutina().getId()).get());
        competidor.setEstado(1);
        competidor.setCalificaciones(new ArrayList<>());
        try {
            repoCompetidos.save(competidor);
        } catch (Exception e) {
            return(maper.writeValueAsString(new MensajeReponse(2,"Error al registrar competidor")) );
        }
        return(maper.writeValueAsString(new MensajeReponse(1,"Competidor registrado con exito")) );
    }
    @PostMapping("/api/registrojuez")
    public String registroJuez(@RequestBody Juez juez) throws JsonProcessingException, InterruptedException{
        
        if(repoJuez.findByTipoAndNumero(juez.getTipo(), juez.getNumero()).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"No existe el juez")) );
        }
        juez = repoJuez.findByTipoAndNumero(juez.getTipo(), juez.getNumero()).get();
        if(repoCompetidos.findByEstado(2).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"No hay competidor activo")) );
        }
        
        Competidor competidor = repoCompetidos.findByEstado(2).get(0);
        List<Calificacion> calificaciones = competidor.getCalificaciones();
        for(Calificacion c : calificaciones){
            if(c.getJuez().getId() == juez.getId()){
               return(maper.writeValueAsString(new MensajeReponse(2,"Ya se registro este numero de juez")) ); 
            }
        }
        Calificacion calificacion = new Calificacion();
        calificacion.setCompetidor(competidor);
        calificacion.setJuez(juez);
         Rutina rutina = null;
        try {
            Evento evento = repo.findByEstado(2).get();
         List<Rutina> rutinas = evento.getRutinas();
        
         for(Rutina r : rutinas){
             if(r.getEstado() == 2){
                 rutina = r;
                 break;
             }
         }
            calificacion = repoCalificacion.save(calificacion);
            
            template.convertAndSend("/call/message", evento);
        } catch (Exception e) {
            return(maper.writeValueAsString(new MensajeReponse(2,"Error al registrar Juez")) ); 
        }
        
        
        
        RegistroResponse res = new RegistroResponse();
        res.setCalificacion(calificacion);
        res.setCompetidor(competidor);
        res.setRutina(rutina);
        return maper.writeValueAsString(res);
    }
    
    @PostMapping("/api/calificar")
    public String calificar(@RequestBody Calificacion calificacion) throws JsonProcessingException{
        
        try {
            Evento evento = repo.findByEstado(2).get();
            Calificacion aux = repoCalificacion.findById(calificacion.getId()).get();
            aux.setCalificacion(calificacion.getCalificacion());
           repoCalificacion.save(aux);
           template.convertAndSend("/call/message", evento);
        } catch (Exception e) {
            return(maper.writeValueAsString(new MensajeReponse(2,"Error no se pudo subir la calificacion")) );
        }
        return(maper.writeValueAsString(new MensajeReponse(1,"Calificaciom emviada con exito")) );
    }
    
    @GetMapping("/api/eventos")
    public String getEventos() throws JsonProcessingException{
        return(maper.writeValueAsString(repo.findAll()));
    }
    
    @GetMapping("/api/eventoactivo")
    public String getEventoActivo() throws JsonProcessingException{
        if(repo.findByEstado(2).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"No hay evento activo")) );
        }
        return(maper.writeValueAsString(repo.findByEstado(2).get()));
    }
    @PostMapping("/api/activarevento")
    public String activarEvento(@RequestBody Evento e) throws JsonProcessingException{
        if(repo.findByEstado(2).isPresent()){
            return(maper.writeValueAsString(new MensajeReponse(2,"Ya hay un evento activo")) );
        }
        if(repo.findById(e.getId()).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"No existe el evento")) );
        }
        e = repo.findById(e.getId()).get();
        e.setEstado(2);
        List<Rutina> rutinas = e.getRutinas();
        repo.save(e);
        rutinas.get(0).setEstado(2);
        List<Competidor> competidores = rutinas.get(0).getCompetidores();
        competidores.get(0).setEstado(2);
        repoRutina.save(rutinas.get(0));
        repoCompetidos.save(competidores.get(0));
        return(maper.writeValueAsString(new MensajeReponse(1,"Evento activado con exito")) );
    }
    @GetMapping("/api/siguiente")
    public String siguiente() throws JsonProcessingException{
         if(repo.findByEstado(2).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"No hay evento activo")) );
        }
         Evento evento = repo.findByEstado(2).get();
         List<Rutina> rutinas = evento.getRutinas();
         Rutina rutina = null;
         for(Rutina r : rutinas){
             if(r.getEstado() == 2){
                 rutina = r;
                 break;
             }
         }
         List<Competidor> competidores = rutina.getCompetidores();
         Competidor competidor = null;
         for(Competidor c : competidores){
             if(c.getEstado() == 2){
                 c.setEstado(3);
                 repoCompetidos.save(c);
             }
             
         }
         for(Competidor c : competidores){
             if(c.getEstado() == 1){
                 c.setEstado(2);
                 competidor = c;
                 repoCompetidos.save(c);
                 break;
             }
         }
         if(competidor != null){
             return maper.writeValueAsString(competidor);
         }
         rutina = null;
         for(Rutina r : rutinas){
             
             if(r.getEstado() == 2){
                 r.setEstado(3);
                 repoRutina.save(r);
             }
         }
         for(Rutina r : rutinas){
             if(r.getEstado() == 1){
                 r.setEstado(2);
                 rutina = r;
                 repoRutina.save(r);
                 break;
             }
         }
         if(rutina != null){
             competidor = rutina.getCompetidores().get(0);
             competidor.setEstado(2);
             return maper.writeValueAsString(repoCompetidos.save(competidor));
         }
         evento.setEstado(3);
         repo.save(evento);
         return(maper.writeValueAsString(new MensajeReponse(2,"Ya no hay rutinas en el evento, se termino automaticamente")) );
         
    }
    @PostMapping("/api/activarcompetidor")
    public String activarCompetidor(@RequestBody Competidor competidor) throws JsonProcessingException{
        if(repoCompetidos.findById(competidor.getId()).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"No existe el competidor")) );
        }
        if(!repoCompetidos.findByEstado(2).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"Ya hay un competidor siendo calificado actualmente")) );
        }
        competidor = repoCompetidos.findById(competidor.getId()).get();
        try {
            competidor.setEstado(2);
            repoCompetidos.save(competidor);
        } catch (Exception e) {
            return(maper.writeValueAsString(new MensajeReponse(2,"Error al activar competidor")) );
        }
        return(maper.writeValueAsString(new MensajeReponse(1,"Competidor activado con exito")) );
    }
    @PostMapping("/api/evento")
    public String getEvento(@RequestBody Evento evento) throws JsonProcessingException{
        if(repo.findById(evento.getId()).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"Error no existe el evento")) );
        }
        return maper.writeValueAsString(repo.findById(evento.getId()).get());
    }
    @PostMapping("/api/terminarevento")
    public String terminarEvento(@RequestBody Evento evento) throws JsonProcessingException{
        evento = repo.findById(evento.getId()).get();
        List<Rutina> rutinas = evento.getRutinas();
        evento.setEstado(3);
        repo.save(evento);
        for(Rutina r : rutinas){
            List<Competidor> competidores = r.getCompetidores();
            r.setEstado(3);
            repoRutina.save(r);
            for(Competidor c : competidores){
                c.setEstado(3);
                repoCompetidos.save(c);
            }
        } 
        return(maper.writeValueAsString(new MensajeReponse(1,"Evento terminado co nexito")) );
    }
    @GetMapping(value = "/api/generar-reporte/{id}",produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generarReporte(@PathVariable("id") Long id) throws IOException, JRException, SQLException{
        if(!repo.findById(id).isPresent()){
            return(null);
          
        }
        Evento e = repo.findById(id).get();  
        List<CompetidorVo> listVo = new ArrayList<>();
        for(Rutina r : e.getRutinas()){
            for(Competidor c : r.getCompetidores()){
                CompetidorVo cvo = new CompetidorVo();
                cvo.setEvento(r.getNombre());
                cvo.setOrden(c.getNombre());
                CompetidorVo cvo2 = new CompetidorVo();
                cvo2.setEvento(r.getNombre());
                cvo2.setOrden(c.getNombre());
                CompetidorVo cvo3 = new CompetidorVo();
                cvo3.setEvento(r.getNombre());
                cvo3.setOrden(c.getNombre());
                cvo.setTipoJuez("EJEC");
                cvo2.setTipoJuez("IMP ART");
                cvo3.setTipoJuez("DIF");
                for(Calificacion ca : c.getCalificaciones()){
                    switch (ca.getJuez().getTipo()) {
                        case 1:
                            switch (ca.getJuez().getNumero()) {
                        case 1:
                            cvo.setJuez1(String.valueOf(ca.getCalificacion()));
                            break;
                        case 2:
                            cvo.setJuez2(String.valueOf(ca.getCalificacion()));
                            break;
                        case 3:
                            cvo.setJuez3(String.valueOf(ca.getCalificacion()));
                            break;
                        case 4:
                            cvo.setJuez4(String.valueOf(ca.getCalificacion()));
                            break;
                        case 5:
                            cvo.setJuez5(String.valueOf(ca.getCalificacion()));
                            break;
                    }
                            break;
                        case 2:
                            switch (ca.getJuez().getNumero()) {
                        case 1:
                            cvo2.setJuez1(String.valueOf(ca.getCalificacion()));
                            break;
                        case 2:
                            cvo2.setJuez2(String.valueOf(ca.getCalificacion()));
                            break;
                        case 3:
                            cvo2.setJuez3(String.valueOf(ca.getCalificacion()));
                            break;
                        case 4:
                            cvo2.setJuez4(String.valueOf(ca.getCalificacion()));
                            break;
                        case 5:
                            cvo2.setJuez5(String.valueOf(ca.getCalificacion()));
                            break;
                    }
                            break;
                        case 3:
                            switch (ca.getJuez().getNumero()) {
                        case 1:
                            cvo3.setJuez1(String.valueOf(ca.getCalificacion()));
                            break;
                        case 2:
                            cvo3.setJuez2(String.valueOf(ca.getCalificacion()));
                            break;
                        case 3:
                            cvo3.setJuez3(String.valueOf(ca.getCalificacion()));
                            break;
                        case 4:
                            cvo3.setJuez4(String.valueOf(ca.getCalificacion()));
                            break;
                        case 5:
                            cvo3.setJuez5(String.valueOf(ca.getCalificacion()));
                            break;
                    }
                            break;
                    }
                    
                    
                }
                listVo.add(cvo);
                listVo.add(cvo2);
                listVo.add(cvo3);
            }
        }
        EventoVo evo = new EventoVo();
        evo.setEvento(e.getNombre());
        evo.setCompetidores(listVo);
        List<EventoVo> lista = new ArrayList<>();
        lista.add(evo);
       JRDataSource ds = new JRBeanCollectionDataSource(lista);
       
       
        InputStream reportStream = res.getInputStream();
        Map<String, Object> map = new HashMap<>();
        map.put("datasource", ds);
       
        JasperPrint print = JasperFillManager.fillReport(reportStream,map,ds);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "output.pdf";
        //headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        headers.add("Content-Disposition", "inline; filename=" + filename);
        return new ResponseEntity<>(JasperExportManager.exportReportToPdf(print), headers, HttpStatus.OK);        
    }
    @DeleteMapping("/api/delete-evento/{id}")
    public ResponseEntity<?> deleteEvento(@PathVariable("id") Long id){
        repo.delete(repo.findById(id).get());
        return new ResponseEntity<>("",HttpStatus.OK);
    }
    @GetMapping("/seleccionar-rutina/{id1}/{id2}")
    public ResponseEntity<?> selectRutina(@PathVariable("id1") Long id1, @PathVariable("id2") Long id2){
        Rutina r1 = repoRutina.findById(id1).get();
        for(Competidor c : r1.getCompetidores()){
            c.setEstado(1);
        }
        r1.setEstado(1);
        repoRutina.save(r1);
        
        r1 = repoRutina.findById(id2).get();
        r1.getCompetidores().get(0).setEstado(2);
        r1.setEstado(2);
        repoRutina.save(r1);
        
        return new ResponseEntity<>("",HttpStatus.OK);
    }
    
    @GetMapping("/seleccionar-competidor/{id1}/{id2}")
    public ResponseEntity<?> selectCompetidor(@PathVariable("id1") Long id1, @PathVariable("id2") Long id2){
        Competidor c = repoCompetidos.findById(id1).get();
        c.setEstado(1);
        repoCompetidos.save(c);
        
        c = repoCompetidos.findById(id2).get();
        c.setEstado(2);
        repoCompetidos.save(c);
        
        return new ResponseEntity<>("",HttpStatus.OK);
    }
}
