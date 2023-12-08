package com.api.agendamentodesalas.rest.controller;

import com.api.agendamentodesalas.converter.SalaConverter;
import com.api.agendamentodesalas.entity.SalaEntity;
import com.api.agendamentodesalas.repository.SalaRepository;
import com.api.agendamentodesalas.rest.request.CriaSalaRequest;
import com.api.agendamentodesalas.rest.response.BuscaSalaResponse;
import com.api.agendamentodesalas.rest.response.CriaSalaResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sala")
@Api(tags = "Sala", description = "Operações relacionadas a salas")
public class SalaController {
    @Autowired
    private SalaRepository salaRepository;

    @GetMapping("/{id}")
    @ApiOperation(value = "Busca uma sala por ID",
            notes = "Retorna informações de uma sala com base no ID fornecido.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Sala encontrada. Retorna os detalhes da sala."),
            @ApiResponse(code = 404, message = "Sala não encontrada. O ID especificado não corresponde a nenhuma sala.")
    })
    private ResponseEntity buscaSala(@PathVariable Long id) {
        Optional<SalaEntity> byId = salaRepository.findById(id);
        if (byId.isEmpty())
            return ResponseEntity.notFound().build();
        SalaEntity salaEntity = byId.get();
        BuscaSalaResponse buscaSalaResponse = SalaConverter.fromSalaEntityToBuscaSalaResponse(salaEntity);
        return ResponseEntity.ok(buscaSalaResponse);
    }

    @GetMapping
    @ApiOperation(value = "Busca todas as salas",
            notes = "Retorna informações de todas as salas cadastradas.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Salas encontradas. Retorna a lista de todas as salas cadastradas.")
    })
    private ResponseEntity buscaTodasAsSalas() {
        List<BuscaSalaResponse> salaResponseList = salaRepository.findAll()
                .stream()
                .map(SalaConverter::fromSalaEntityToBuscaSalaResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(salaResponseList);
    }

    @PostMapping
    @ApiOperation(value = "Cria uma nova sala",
            notes = "Cria uma nova sala com base nos detalhes fornecidos.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "Sala criada com sucesso."),
            @ApiResponse(code = 400, message = "Solicitação inválida. A sala já existe no banco de dados.")
    })
    private ResponseEntity criaSala(@RequestBody @Valid CriaSalaRequest criaSalaRequest) {
        Optional<SalaEntity> byNomeAndLocal = salaRepository.findByNomeAndLocal(criaSalaRequest.getNome(), criaSalaRequest.getLocal());

        if (byNomeAndLocal.isPresent())
            return ResponseEntity.badRequest().body("sala ja existe no banco de dados");

        SalaEntity salaEntity = SalaConverter.fromCriaSalaRequestToSalaEntity(criaSalaRequest);
        CriaSalaResponse criaSalaResponse = SalaConverter.fromSalaEntityToCriaSalaResponse(salaRepository.save(salaEntity));
        return new ResponseEntity(criaSalaResponse, HttpStatus.CREATED);
    }
}
