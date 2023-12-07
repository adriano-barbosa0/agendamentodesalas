package com.api.agendamentodesalas.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CriaSalaResponse {
    private Long id;
    private String nome;
    private String local;
}
