
package com.eventos.controller;

import com.eventos.domain.Calificacion;
import com.eventos.domain.Competidor;
import com.eventos.domain.Evento;

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
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    private ApplicationContext applicationContext;
    
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
    @GetMapping(value = "/api/generar-reporte/{id}")
    public byte[] generarReporte(@PathVariable("id") Long id) throws IOException, JRException{
        if(!repo.findById(id).isPresent()){
            return(null);
          
        }
        Evento e = repo.findById(id).get();  
        List<Evento> lista = new ArrayList<>();
        lista.add(e);
        InputStream reportStream = res.getInputStream();
        Map<String, Object> map = new HashMap<>();
        map.put("E_ID",id.intValue());
        JasperPrint print = JasperFillManager.fillReport(reportStream,map,  DataSourceUtils.getConnection((DataSource)applicationContext.getBean("dataSource")));
        return JasperExportManager.exportReportToPdf(print);        
    }
    
}
