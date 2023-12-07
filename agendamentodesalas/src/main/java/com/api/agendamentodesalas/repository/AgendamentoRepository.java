package com.api.agendamentodesalas.repository;

import com.api.agendamentodesalas.entity.AgendamentoEntity;
import com.api.agendamentodesalas.entity.AgendamentoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<AgendamentoEntity, AgendamentoId> {
    @Query(nativeQuery = true, value = "select * from agendamento where dia between ?1 and ?2 and sala_id = ?3")
    List<AgendamentoEntity> buscaDiasMarcadosPorPeriodo(LocalDate diaInicial, LocalDate diaFinal, Long salaId);
}
