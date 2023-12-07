package com.api.agendamentodesalas.converter;

import com.api.agendamentodesalas.entity.SalaEntity;
import com.api.agendamentodesalas.rest.request.CriaSalaRequest;
import com.api.agendamentodesalas.rest.response.BuscaSalaResponse;
import com.api.agendamentodesalas.rest.response.CriaSalaResponse;
import org.springframework.http.ResponseEntity;

public class SalaConverter {
    public static BuscaSalaResponse fromSalaEntityToBuscaSalaResponse(SalaEntity salaEntity) {
        BuscaSalaResponse buscaSalaResponse = BuscaSalaResponse.builder()
                .id(salaEntity.getId())
                .nome(salaEntity.getNome())
                .local(salaEntity.getLocal()).build();
        return buscaSalaResponse;
    }

    public static SalaEntity fromCriaSalaRequestToSalaEntity(CriaSalaRequest criaSalaRequest) {
        return SalaEntity.builder()
                .nome(criaSalaRequest.getNome())
                .local(criaSalaRequest.getLocal())
                .build();
    }

    public static CriaSalaResponse fromSalaEntityToCriaSalaResponse(SalaEntity salaEntity) {
        return CriaSalaResponse.builder()
                .id(salaEntity.getId())
                .local(salaEntity.getLocal())
                .nome(salaEntity.getNome())
                .build();
    }
}
