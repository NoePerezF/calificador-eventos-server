
package com.eventos.controller;

import com.eventos.domain.Evento;
import com.eventos.domain.EventoJuezDificultad;
import com.eventos.domain.EventoJuezEjecucion;
import com.eventos.domain.EventoJuezImpresionArtistica;
import com.eventos.domain.Juez;
import com.eventos.repository.EventoRepository;
import com.eventos.util.MensajeReponse;
import com.eventos.repository.JuezRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
        public EventoController(SimpMessagingTemplate template) {
            this.template = template;
        }
    
    @GetMapping("/api/ping")
    public String ping() throws JsonProcessingException{
      return(maper.writeValueAsString(new MensajeReponse(1,"Ok")) );
    }
    @PostMapping("/api/nuevoevento")
    public String nuevoEvento(@RequestBody Evento evento) throws JsonProcessingException{
        evento.setEstado(1);
        evento.setDificultad(new ArrayList<>());
        evento.setEjecucion(new ArrayList<>());
        evento.setImpresionArtistica(new ArrayList<>());
        repo.save(evento);
        return(maper.writeValueAsString(new MensajeReponse(1,"Evento creado con exito")) );
    }
    @PostMapping("/api/registrojuez")
    public String registroJuez(@RequestBody String str) throws JsonProcessingException{
        
        JsonNode node = maper.readTree(str);
        Evento evento = maper.convertValue(node.get("evento"), Evento.class);
        Juez juez = maper.convertValue(node.get("juez"), Juez.class);
        
        if(repoJuez.findByTipoAndNumero(juez.getTipo(), juez.getNumero()).isEmpty())
            return(maper.writeValueAsString(new MensajeReponse(2,"Error no se encuentra el Juez")) );
        juez = repoJuez.findByTipoAndNumero(juez.getTipo(), juez.getNumero()).get();
        if(repo.findById(evento.getId()).isEmpty())
            return(maper.writeValueAsString(new MensajeReponse(2,"Error no se encuentra el evento")) );
        evento = repo.findById(evento.getId()).get();
        
        if(evento != null){
            if(juez.getTipo() == 1){
                if(evento.getEjecucion().size() < NUMERO_JUECES){
                    List<EventoJuezEjecucion> jueces = evento.getEjecucion();
                    juez.setNombre("Juez " + juez.getNumero());
                    EventoJuezEjecucion e = new EventoJuezEjecucion();
                    for(EventoJuezEjecucion ej : jueces){
                        if(ej.getJuez().getId() == juez.getId()){
                            return(maper.writeValueAsString(new MensajeReponse(2,"Ya hay un juez con esta categoria y numero registrado en el evento")) );
                        }
                    }
                    e.setJuez(juez);
                    e.setEvento(evento);
                    jueces.add(e);
                    evento.setEjecucion(jueces);
                    repo.save(evento);
                    this.template.convertAndSend("/call/message",evento);
                    return(maper.writeValueAsString(juez) );
                }
                return(maper.writeValueAsString(new MensajeReponse(2,"Error numero de jueces de ejecucion compelto")) );
            }
            if(juez.getTipo() == 2){
                if(evento.getImpresionArtistica().size() < NUMERO_JUECES){
                   List<EventoJuezImpresionArtistica> jueces = evento.getImpresionArtistica();
                    juez.setNombre("Juez " + juez.getNumero());
                    EventoJuezImpresionArtistica e = new EventoJuezImpresionArtistica();
                    for(EventoJuezImpresionArtistica ej : jueces){
                        if(ej.getJuez().getId() == juez.getId()){
                            return(maper.writeValueAsString(new MensajeReponse(2,"Ya hay un juez con esta categoria y numero registrado en el evento")) );
                        }
                    }
                    e.setJuez(juez);
                    e.setEvento(evento);
                    jueces.add(e);
                    evento.setImpresionArtistica(jueces);
                    repo.save(evento);
                    this.template.convertAndSend("/call/message",evento);
                    return(maper.writeValueAsString(juez) );
                }
                return(maper.writeValueAsString(new MensajeReponse(2,"Error numero de jueces de impresion artistica compelto")) );
            }
            if(juez.getTipo() == 3){
                List<EventoJuezDificultad> jueces = evento.getDificultad();
                    juez.setNombre("Juez " + juez.getNumero());
                    EventoJuezDificultad e = new EventoJuezDificultad();
                    for(EventoJuezDificultad ej : jueces){
                        if(ej.getJuez().getId() == juez.getId()){
                            return(maper.writeValueAsString(new MensajeReponse(2,"Ya hay un juez con esta categoria y numero registrado en el evento")) );
                        }
                    }
                    e.setJuez(juez);
                    e.setEvento(evento);
                    jueces.add(e);
                    evento.setDificultad(jueces);
                    repo.save(evento);
                    this.template.convertAndSend("/call/message",evento);
                    return(maper.writeValueAsString(juez) );
                }
                return(maper.writeValueAsString(new MensajeReponse(2,"Error numero de jueces de dificultad compelto")) );
            }
        
         return(maper.writeValueAsString(new MensajeReponse(2,"Error no hay evento en curso")) );
    }
    
    @PostMapping("/api/calificar")
    public String calificar(@RequestBody String str) throws JsonProcessingException{
        
        JsonNode node = maper.readTree(str);
        Evento evento = maper.convertValue(node.get("evento"), Evento.class);
        Juez juez = maper.convertValue(node.get("juez"), Juez.class);
        Double calificacionJuez = maper.convertValue(node.get("calificacion"), Double.class);
        if(repoJuez.findByTipoAndNumero(juez.getTipo(), juez.getNumero()).isEmpty())
            return(maper.writeValueAsString(new MensajeReponse(2,"Error no se encuentra el Juez")) );
        juez = repoJuez.findByTipoAndNumero(juez.getTipo(), juez.getNumero()).get();
        if(repo.findById(evento.getId()).isEmpty())
            return(maper.writeValueAsString(new MensajeReponse(2,"Error no se encuentra el evento")) );
        evento = repo.findById(evento.getId()).get();
        try {
                if(juez.getTipo() == 1){
                    EventoJuezEjecucion aux = null;
                    List<EventoJuezEjecucion> e = evento.getEjecucion();
                    for(EventoJuezEjecucion ej : e){
                        if(ej.getJuez().getId() == juez.getId()){
                            aux = ej;
                        }
                    }
                    if(aux == null)
                        return(maper.writeValueAsString(new MensajeReponse(2,"El juez no esta registrado en el evento")) );
                    aux.setCalificacion(calificacionJuez);
                    evento.setEjecucion(e);
                    repo.save(evento);
                    this.template.convertAndSend("/call/message",evento);
                    return(maper.writeValueAsString(new MensajeReponse(1,"Calificacion enviada con exito")) );
                }
                if(juez.getTipo() == 2){
                    EventoJuezImpresionArtistica aux = null;
                    List<EventoJuezImpresionArtistica> e = evento.getImpresionArtistica();
                    for(EventoJuezImpresionArtistica ej : e){
                        if(ej.getJuez().getId() == juez.getId()){
                            aux = ej;
                        }
                    }
                    if(aux == null)
                        return(maper.writeValueAsString(new MensajeReponse(2,"El juez no esta registrado en el evento")) );
                    aux.setCalificacion(calificacionJuez);
                    evento.setImpresionArtistica(e);
                    repo.save(evento);
                    this.template.convertAndSend("/call/message",evento);
                    return(maper.writeValueAsString(new MensajeReponse(1,"Calificacion enviada con exito")) );
                }
                if(juez.getTipo() == 3){
                    EventoJuezDificultad aux = null;
                    List<EventoJuezDificultad> e = evento.getDificultad();
                    for(EventoJuezDificultad ej : e){
                        if(ej.getJuez().getId() == juez.getId()){
                            aux = ej;
                        }
                    }
                    if(aux == null)
                        return(maper.writeValueAsString(new MensajeReponse(2,"El juez no esta registrado en el evento")) );
                    aux.setCalificacion(calificacionJuez);
                    evento.setDificultad(e);
                    repo.save(evento);
                    this.template.convertAndSend("/call/message",evento);
                    return(maper.writeValueAsString(new MensajeReponse(1,"Calificacion enviada con exito")) );
                }
                //this.template.convertAndSend("/call/message",evento);
                //return(maper.writeValueAsString(new MensajeReponse(1,"Calificacion enviada con exito")) );
           
            
            
        } catch (Exception e) {
            return(maper.writeValueAsString(new MensajeReponse(2,"Error no se pudo subir la calificacion")) );
        }
        return(maper.writeValueAsString(new MensajeReponse(1,"No hay evento en curso")) );
    }
    @PostMapping("/api/terminarevento")
    public String terminarEvento(@RequestBody Evento e) throws JsonProcessingException{
        if(repo.findByEstado(2).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"El evento no esta activo")) );
        }
        if(repo.findById(e.getId()).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"No existe el evento")) );
        }
        e = repo.findById(e.getId()).get();
        e.setEstado(3);
        repo.save(e);
        return(maper.writeValueAsString(new MensajeReponse(1, "Evento terminado co nexito")));
    }
    @PostMapping("/api/cancelarevento")
    public String cancelarEvento(@RequestBody Evento e) throws JsonProcessingException{
        if(repo.findByEstado(2).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"El evento no esta activo")) );
        }
        if(repo.findById(e.getId()).isEmpty()){
            return(maper.writeValueAsString(new MensajeReponse(2,"No existe el evento")) );
        }
        e = repo.findById(e.getId()).get();
        e.setEstado(4);
        repo.save(e);
        return(maper.writeValueAsString(new MensajeReponse(1, "Evento cancelado co nexito")));
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
        repo.save(e);
        return(maper.writeValueAsString(new MensajeReponse(1,"Evento activado con exito")) );
    }
    
}
