
package com.eventos.controller;

import com.eventos.domain.Evento;
import com.eventos.domain.Juez;
import com.eventos.util.MensajeReponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
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
    private Evento evento = null;
    private SimpMessagingTemplate template;
    
    @GetMapping("/api/ping")
    public String ping() throws JsonProcessingException{
      return(maper.writeValueAsString(new MensajeReponse(1,"Ok")) );
    }
    @PostMapping("/api/nuevoevento")
    public String nuevoEvento(@RequestBody Evento ev) throws JsonProcessingException{
        evento = ev;
        evento.setDificultad(new ArrayList<>());
        evento.setEjecucion(new ArrayList<>());
        evento.setImpresionArtistica(new ArrayList<>());
        return(maper.writeValueAsString(new MensajeReponse(1,"Evento creado con exito")) );
    }
    @PostMapping("/api/registrojuez")
    public String registroJuez(@RequestBody Juez juez) throws JsonProcessingException{
        if(evento != null){
            if(juez.getTipo() == 1){
                if(evento.getEjecucion().size() < NUMERO_JUECES){
                    List<Juez> jueces = evento.getEjecucion();
                    juez.setId(""+(evento.getEjecucion().size()+1));
                    juez.setNombre("Juez" +(evento.getEjecucion().size()+1));
                    jueces.add(juez);
                    this.template.convertAndSend("/call/message",this.evento);
                    return(maper.writeValueAsString(juez) );
                }
                return(maper.writeValueAsString(new MensajeReponse(2,"Error numero de jueces de ejecucion compelto")) );
            }
            if(juez.getTipo() == 2){
                if(evento.getImpresionArtistica().size() < NUMERO_JUECES){
                    List<Juez> jueces = evento.getImpresionArtistica();
                    juez.setId(""+(evento.getEjecucion().size()+1));
                    juez.setNombre("Juez" +(evento.getEjecucion().size()+1));
                    jueces.add(juez);
                    this.template.convertAndSend("/call/message",this.evento);
                    return(maper.writeValueAsString(juez) );
                }
                return(maper.writeValueAsString(new MensajeReponse(2,"Error numero de jueces de impresion artistica compelto")) );
            }
            if(juez.getTipo() == 3){
                if(evento.getDificultad().size() < NUMERO_JUECES){
                    List<Juez> jueces = evento.getDificultad();
                    juez.setId(""+(evento.getEjecucion().size()+1));
                    juez.setNombre("Juez" +(evento.getEjecucion().size()+1));
                    jueces.add(juez);
                    this.template.convertAndSend("/call/message",this.evento);
                    return(maper.writeValueAsString(juez) );
                }
                return(maper.writeValueAsString(new MensajeReponse(2,"Error numero de jueces de dificultad compelto")) );
            }
        }
        
         return(maper.writeValueAsString(new MensajeReponse(2,"Error no hay evento en curso")) );
    }
    
    @PostMapping("/api/calificar")
    public String calificar(@RequestBody Juez juez) throws JsonProcessingException{
        try {
            boolean flag = false;
            Juez juezAux;
            if(juez.getTipo() == 1){
                List<Juez> lista = new ArrayList<>();
                for(Juez j : evento.getEjecucion() ){
                    lista.add(j);
                    if(j.getId().compareTo(juez.getId()) == 0){
                        lista.remove(j);
                        juezAux = j;
                        juezAux.setCalificacion(juez.getCalificacion());
                        lista.add(juezAux);
                        flag = true;
                    }
                }
                if(!flag){
                    return(maper.writeValueAsString(new MensajeReponse(2,"Error no se pudo subir la calificacion")) );
                }
                evento.setEjecucion(lista);
                this.template.convertAndSend("/call/message",this.evento);
                return(maper.writeValueAsString(new MensajeReponse(1,"Calificacion enviada con exito")) );
         
            }
            if(juez.getTipo() == 2){
                List<Juez> lista = new ArrayList<>();
                for(Juez j : evento.getImpresionArtistica()){
                    lista.add(j);
                    if(j.getId().compareTo(juez.getId()) == 0){
                        lista.remove(j);
                        juezAux = j;
                        juezAux.setCalificacion(juez.getCalificacion());
                        lista.add(juezAux);
                        flag = true;
                    }
                }
                if(!flag){
                    return(maper.writeValueAsString(new MensajeReponse(2,"Error no se pudo subir la calificacion")) );
                }
                evento.setImpresionArtistica(lista);
                this.template.convertAndSend("/call/message",this.evento);
                return(maper.writeValueAsString(new MensajeReponse(1,"Calificacion enviada con exito")) );
         
            }
            if(juez.getTipo() == 3){
                List<Juez> lista = new ArrayList<>();
                for(Juez j : evento.getDificultad()){
                    lista.add(j);
                    if(j.getId().compareTo(juez.getId()) == 0){
                        lista.remove(j);
                        juezAux = j;
                        juezAux.setCalificacion(juez.getCalificacion());
                        lista.add(juezAux);
                        flag = true;
                    }
                }
                if(!flag){
                    return(maper.writeValueAsString(new MensajeReponse(2,"Error no se pudo subir la calificacion")) );
                }
                evento.setDificultad(lista);
                this.template.convertAndSend("/call/message",this.evento);
                return(maper.writeValueAsString(new MensajeReponse(1,"Calificacion enviada con exito")) );
         
            }
        } catch (Exception e) {
            return(maper.writeValueAsString(new MensajeReponse(2,"Error no se pudo subir la calificacion")) );
        }
        
        return(maper.writeValueAsString(new MensajeReponse(2,"Error no se pudo subir la calificacion")) );
    }
    @GetMapping("/api/terminarevento")
    public String terminarEvento() throws JsonProcessingException{
        this.evento = null;
        return(maper.writeValueAsString(new MensajeReponse(1, "Evento terminado co nexito")));
    }
    
    
    
}
