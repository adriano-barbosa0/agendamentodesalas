package com.api.agendamentodesalas.rest;

import com.api.agendamentodesalas.converter.SalaConverter;
import com.api.agendamentodesalas.entity.SalaEntity;
import com.api.agendamentodesalas.repository.SalaRepository;
import com.api.agendamentodesalas.rest.request.CriaSalaRequest;
import com.api.agendamentodesalas.rest.response.BuscaSalaResponse;
import com.api.agendamentodesalas.rest.response.CriaSalaResponse;
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
public class SalaController {
    @Autowired
    private SalaRepository salaRepository;

    @GetMapping("/{id}")
    private ResponseEntity buscaSala(@PathVariable Long id) {
        Optional<SalaEntity> byId = salaRepository.findById(id);
        if (byId.isEmpty())
            return ResponseEntity.notFound().build();
        SalaEntity salaEntity = byId.get();
        BuscaSalaResponse buscaSalaResponse = SalaConverter.fromSalaEntityToBuscaSalaResponse(salaEntity);
        return ResponseEntity.ok(buscaSalaResponse);
    }

    @GetMapping
    private ResponseEntity buscaTodasAsSalas() {
        List<BuscaSalaResponse> salaResponseList = salaRepository.findAll()
                .stream()
                .map(SalaConverter::fromSalaEntityToBuscaSalaResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(salaResponseList);
    }

    @PostMapping
    private ResponseEntity criaSala(@RequestBody @Valid CriaSalaRequest criaSalaRequest) {
        Optional<SalaEntity> byNomeAndLocal = salaRepository.findByNomeAndLocal(criaSalaRequest.getNome(), criaSalaRequest.getLocal());

        if (byNomeAndLocal.isPresent())
            return ResponseEntity.badRequest().body("sala ja existe no banco de dados");

        SalaEntity salaEntity = SalaConverter.fromCriaSalaRequestToSalaEntity(criaSalaRequest);
        CriaSalaResponse criaSalaResponse = SalaConverter.fromSalaEntityToCriaSalaResponse(salaRepository.save(salaEntity));
        return new ResponseEntity(criaSalaResponse, HttpStatus.CREATED);
    }
}
