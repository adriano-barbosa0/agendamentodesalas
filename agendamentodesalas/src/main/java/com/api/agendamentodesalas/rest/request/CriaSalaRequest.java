package com.api.agendamentodesalas.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CriaSalaRequest {
    @NotBlank
    private String nome;
    @NotBlank
    private String local;
}
