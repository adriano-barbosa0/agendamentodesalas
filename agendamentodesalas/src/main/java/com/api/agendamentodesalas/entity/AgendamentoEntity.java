package com.api.agendamentodesalas.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamento")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AgendamentoEntity {
    @EmbeddedId
    private AgendamentoId id;
    @ManyToOne
    @MapsId("salaId")
    private SalaEntity sala;
    @Column(name = "data_criacao_agendamento")
    private LocalDateTime dataCriacaoAgendamento;
}
