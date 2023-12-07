package com.api.agendamentodesalas.repository;

import com.api.agendamentodesalas.entity.SalaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<SalaEntity, Long> {
    @Query( value = "select * from sala where nome = ?1 and local = ?2", nativeQuery = true)
    Optional<SalaEntity> findByNomeAndLocal(String nome, String local);
}
