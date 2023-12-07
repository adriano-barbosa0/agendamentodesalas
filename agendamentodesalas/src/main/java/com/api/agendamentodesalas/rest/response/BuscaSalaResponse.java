package com.api.agendamentodesalas.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BuscaSalaResponse {
    private Long id;
    private String nome;
    private String local;
}
