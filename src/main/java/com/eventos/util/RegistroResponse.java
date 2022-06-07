package com.eventos.util;

import com.eventos.domain.Competidor;
import com.eventos.domain.Juez;
import com.eventos.domain.Rutina;
import java.io.Serializable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegistroResponse implements Serializable{
    
    Juez juez;
    Competidor competidor;
    Rutina rutina;
    
}
