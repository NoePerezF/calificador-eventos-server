
package com.eventos.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompetidorVo {
    String evento;
    String orden;
    String tipoJuez;
    String juez1;
    String juez2;
    String juez3;
    String juez4;
    String juez5;
}
