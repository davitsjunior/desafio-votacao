package br.com.davijunior.desafio_votacao.controller;

import br.com.davijunior.desafio_votacao.dto.request.VotoRequest;
import br.com.davijunior.desafio_votacao.dto.response.VotoResponse;
import br.com.davijunior.desafio_votacao.service.VotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pautas/{pautaId}/votos")
@RequiredArgsConstructor
@Tag(name = "Votos")
public class VotoController {

    private final VotoService votoService;

    @PostMapping
    @Operation(summary = "Registra o voto de um associado na pauta")
    public ResponseEntity<VotoResponse> votar(@PathVariable Long pautaId, @Valid @RequestBody VotoRequest request) {
        VotoResponse response = votoService.votar(pautaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}