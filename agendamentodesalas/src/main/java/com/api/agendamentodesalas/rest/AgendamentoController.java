package com.api.agendamentodesalas.rest;

import com.api.agendamentodesalas.converter.AgendamentoConverter;
import com.api.agendamentodesalas.entity.AgendamentoEntity;
import com.api.agendamentodesalas.entity.AgendamentoId;
import com.api.agendamentodesalas.entity.SalaEntity;
import com.api.agendamentodesalas.repository.AgendamentoRepository;
import com.api.agendamentodesalas.repository.SalaRepository;
import com.api.agendamentodesalas.rest.request.AtualizaAgendamentoRequest;
import com.api.agendamentodesalas.rest.request.AtualizaAgendamentoRequestItem;
import com.api.agendamentodesalas.rest.request.CriaAgendamentoRequest;
import com.api.agendamentodesalas.rest.response.ListaAgendamentoResponseItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agendamento")
@Slf4j
public class AgendamentoController {
    @Autowired
    private SalaRepository salaRepository;
    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @GetMapping("/agendados")
    public ResponseEntity buscaAgendados(
            @RequestParam("dataInicio") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim,
            @RequestParam("salaId") Long salaId) {
       // log.info("inciando fluxo de busca de agendamento");
       // log.error("houve um erro ao buscar a informacao");
        Optional<SalaEntity> salaEntityOptional = salaRepository.findById(salaId);
        if (salaEntityOptional.isEmpty())
            return new ResponseEntity("nao existe uma sala para o id informado", HttpStatus.NOT_FOUND);

        List<AgendamentoEntity> agendamentoEntities = agendamentoRepository.buscaDiasMarcadosPorPeriodo(dataInicio, dataFim, salaId);
        List<ListaAgendamentoResponseItem> listaAgendamentoResponseItems = agendamentoEntities.stream()
                .map(AgendamentoConverter::fromAgendamentoEntityToListaAgendamentosResponseItem)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaAgendamentoResponseItems);
    }

    @GetMapping("/disponibilidade")
    public List<AgendamentoEntity> buscaDisponibilidade(
            @RequestParam("dataInicio") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim,
            @RequestParam("salaId") Long salaId) {
        List<AgendamentoEntity> agendamentoEntities = agendamentoRepository.buscaDiasMarcadosPorPeriodo(dataInicio, dataFim, salaId);
        return agendamentoEntities;
    }

    @PostMapping
    public ResponseEntity criaAgendamento(@RequestBody CriaAgendamentoRequest criaAgendamentoRequest) {
        Optional<SalaEntity> salaEntityOptional = salaRepository.findById(criaAgendamentoRequest.getSalaId());

        if (salaEntityOptional.isEmpty())
            return ResponseEntity.badRequest().body("sala informada nao existe!");

        AgendamentoId agendamentoId = AgendamentoId.builder()
                .dia(criaAgendamentoRequest.getDia())
                .hora(criaAgendamentoRequest.getHora())
                .salaId(criaAgendamentoRequest.getSalaId())
                .build();

        Optional<AgendamentoEntity> agendamentoEntityOptional = agendamentoRepository.findById(agendamentoId);

        if (agendamentoEntityOptional.isPresent())
            return ResponseEntity.badRequest().body("agendamento ja existente para a sala, data e hora informada!");

        AgendamentoEntity agendamento = AgendamentoEntity.builder()
                .id(agendamentoId)
                .sala(salaEntityOptional.get())
                .dataCriacaoAgendamento(LocalDateTime.now())
                .build();
        ;
        AgendamentoEntity saved = agendamentoRepository.save(agendamento);
        return new ResponseEntity(AgendamentoConverter.fromAgendamentoEntityToCriaAgendamentoResponse(saved), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity reagendar(@RequestBody AtualizaAgendamentoRequest atualizaAgendamentoRequest) {
        
        // verifica se o antigo existe
        AtualizaAgendamentoRequestItem antigo = atualizaAgendamentoRequest.getAntigo();
        AgendamentoId agendamentoIdAntigo = AgendamentoConverter.fromAtualizaAgendamentoRequestItemToAgendamentoId(antigo);
        Optional<AgendamentoEntity> agendamentoAntigoEntityOptional = agendamentoRepository.findById(agendamentoIdAntigo);
        if (agendamentoAntigoEntityOptional.isEmpty())
            return new ResponseEntity("o agendamento informado como antigo nao existe!", HttpStatus.NOT_FOUND);

        // verifica se o novo nao existe
        AtualizaAgendamentoRequestItem novo = atualizaAgendamentoRequest.getNovo();
        AgendamentoId agendamentoIdNovo = AgendamentoConverter.fromAtualizaAgendamentoRequestItemToAgendamentoId(novo);
        Optional<AgendamentoEntity> agendamentoNovoEntityOptional = agendamentoRepository.findById(agendamentoIdNovo);
        if (agendamentoNovoEntityOptional.isPresent())
            return ResponseEntity.badRequest().body("agendamento ja realizado para a nova data escolhida!");


        SalaEntity salaNova = salaRepository.getById(novo.getSalaId());
        AgendamentoEntity novaAgendamento = AgendamentoEntity.builder()
                .sala(salaNova)
                .dataCriacaoAgendamento(LocalDateTime.now())
                .id(agendamentoIdNovo)
                .build();
        AgendamentoEntity novoAgendamento = agendamentoRepository.save(novaAgendamento);
        agendamentoRepository.deleteById(agendamentoIdAntigo);
        return ResponseEntity.ok(novoAgendamento);
    }
}
