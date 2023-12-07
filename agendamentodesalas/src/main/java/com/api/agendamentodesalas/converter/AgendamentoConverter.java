package com.api.agendamentodesalas.converter;

import com.api.agendamentodesalas.entity.AgendamentoEntity;
import com.api.agendamentodesalas.entity.AgendamentoId;
import com.api.agendamentodesalas.rest.request.AtualizaAgendamentoRequestItem;
import com.api.agendamentodesalas.rest.response.CriaAgendamentoResponse;
import com.api.agendamentodesalas.rest.response.ListaAgendamentoResponseItem;

public class AgendamentoConverter {
    public static ListaAgendamentoResponseItem fromAgendamentoEntityToListaAgendamentosResponseItem(AgendamentoEntity agendamentoEntity) {
        return ListaAgendamentoResponseItem.builder()
                .salaId(agendamentoEntity.getSala().getId())
                .dia(agendamentoEntity.getId().getDia())
                .hora(agendamentoEntity.getId().getHora())
                .dataCriacaoAgendamento(agendamentoEntity.getDataCriacaoAgendamento())
                .build();
    }

    public static CriaAgendamentoResponse fromAgendamentoEntityToCriaAgendamentoResponse(AgendamentoEntity saved) {
        return CriaAgendamentoResponse.builder()
                .salaId(saved.getId().getSalaId())
                .dia(saved.getId().getDia())
                .hora(saved.getId().getHora())
                .dataCriacaoAgendamento(saved.getDataCriacaoAgendamento())
                .build();
    }

    public static AgendamentoId fromAtualizaAgendamentoRequestItemToAgendamentoId(AtualizaAgendamentoRequestItem agendamentoRequestItem) {
        return AgendamentoId.builder()
                .salaId(agendamentoRequestItem.getSalaId())
                .hora(agendamentoRequestItem.getHora())
                .dia(agendamentoRequestItem.getDia())
                .build();
    }

    public static AgendamentoEntity fromAtualizaAgendamentoRequestItemToAgendamentoEntity(AtualizaAgendamentoRequestItem novo) {

        return null;
    }
}
