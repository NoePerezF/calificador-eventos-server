
package com.eventos.util;

import com.eventos.domain.Calificacion;
import com.eventos.domain.Competidor;
import com.eventos.domain.Rutina;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistroResponse implements Serializable{
    private Rutina rutina;
    private Competidor competidor;
    private Calificacion calificacion;
}
