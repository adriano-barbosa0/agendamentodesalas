package com.api.agendamentodesalas.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AgendamentoId implements Serializable {
    private static final long serialVersionUID = 7082598637712688774L;
    @Column(name = "sala_id")
    private Long salaId;
    @Column
    private LocalDate dia;
    @Column
    private Integer hora;
}
