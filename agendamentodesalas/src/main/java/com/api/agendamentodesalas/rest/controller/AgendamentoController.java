package com.api.agendamentodesalas.rest.controller;

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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/agendamento")
@Slf4j
@Api(tags = "Agendamento", description = "Operações de agendamento")
public class AgendamentoController   {
    @Autowired
    private SalaRepository salaRepository;
    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @GetMapping("/agendados")
    @ApiOperation(value = "Busca agendamentos de uma sala por período",
            notes = "Retorna uma lista de agendamentos de uma sala para um intervalo de datas especificado.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Operação bem-sucedida. Retorna os agendamentos."),
            @ApiResponse(code = 400, message = "Requisição inválida. Verifique os parâmetros da solicitação."),
            @ApiResponse(code = 404, message = "Sala não encontrada. A sala especificada não existe."),
            @ApiResponse(code = 500, message = "Erro interno do servidor. Entre em contato com o suporte técnico.")
    })
    public ResponseEntity buscaAgendados(

            @RequestParam("dataInicio") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim,
            @RequestParam("salaId") Long salaId) {
        log.info("inciando fluxo de busca de agendamento");
        log.error("houve um erro ao buscar a informacao");
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
    @ApiOperation(value = "Busca horários disponíveis para agendamento em uma sala por período",
            notes = "Retorna os horários disponíveis para agendamento em uma sala para um intervalo de datas especificado.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Operação bem-sucedida. Retorna os horários disponíveis."),
            @ApiResponse(code = 404, message = "Sala não encontrada. A sala especificada não existe."),
            @ApiResponse(code = 500, message = "Erro interno do servidor. Entre em contato com o suporte técnico.")
    })
    public List<LocalDateTime> buscaDisponibilidade(
            @RequestParam("dataInicio") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate dataFim,
            @RequestParam("salaId") Long salaId) {
        List<AgendamentoEntity> agendamentoEntities = agendamentoRepository.buscaDiasMarcadosPorPeriodo(dataInicio, dataFim, salaId);

        List<LocalDateTime> horariosDisponiveis = criaHorariosParaOsDias(dataInicio, dataFim);
        List<LocalDateTime> horariosAgendados = agendamentoEntities.stream().map(agendamentoEntity -> {
            LocalDateTime localDateTime = agendamentoEntity.getId().getDia().atStartOfDay();
            return localDateTime.plusHours(agendamentoEntity.getId().getHora());
        }).collect(Collectors.toList());
        horariosDisponiveis.removeAll(horariosAgendados);
        return horariosDisponiveis;
    }

    private static List<LocalDateTime> criaHorariosParaOsDias(LocalDate dataInicio, LocalDate dataFim) {
        List<LocalDate> intervaloDeDatas = criaDatasEntreDataInicioEDataFim(dataInicio, dataFim);
        List<LocalDateTime> horarios = new ArrayList<>();
        // para cada data
        intervaloDeDatas.forEach(localDate -> {
            // preenche os 24 slots de horario
            for (int i = 0; i < 24; i++) {
                horarios.add(localDate.atStartOfDay().plusHours(i));
            }
        });
        return horarios;
    }

    @PostMapping
    @ApiOperation(value = "Cria um novo agendamento para uma sala",
            notes = "Cria um novo agendamento para uma sala em uma data e hora especificadas.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Agendamento criado com sucesso."),
            @ApiResponse(code = 400, message = "Solicitação inválida. Verifique os dados enviados."),
            @ApiResponse(code = 404, message = "Sala não encontrada. A sala especificada não existe."),
            @ApiResponse(code = 500, message = "Erro interno do servidor. Entre em contato com o suporte técnico.")
    })
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
    @ApiResponses({
            @ApiResponse(code = 200, message = "Agendamento reagendado com sucesso."),
            @ApiResponse(code = 400, message = "Solicitação inválida. Verifique os dados enviados."),
            @ApiResponse(code = 404, message = "Agendamento antigo não encontrado. O agendamento informado como antigo não existe."),
            @ApiResponse(code = 500, message = "Erro interno do servidor. Entre em contato com o suporte técnico.")
    })
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

    public static List<LocalDate> criaDatasEntreDataInicioEDataFim(
            LocalDate startDate, LocalDate endDate) {

        return startDate.datesUntil(endDate)
                .collect(Collectors.toList());
    }
}
