
package com.eventos.util;

import java.io.Serializable;


public class MensajeReponse implements Serializable{
    
    private int status;
    private String mensaje;

    public MensajeReponse(int status, String mensaje) {
        this.status = status;
        this.mensaje = mensaje;
    }

    
    
    public int getStatus() {
        return status;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    
    
}
