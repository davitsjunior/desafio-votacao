package br.com.davijunior.desafio_votacao.controller;

import br.com.davijunior.desafio_votacao.dto.request.PautaRequest;
import br.com.davijunior.desafio_votacao.dto.response.PautaResponse;
import br.com.davijunior.desafio_votacao.service.PautaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/pautas")
@RequiredArgsConstructor
@Tag(name = "Pautas")
public class PautaController {

    private final PautaService pautaService;

    @PostMapping
    @Operation(summary = "Cadastra uma nova pauta")
    public ResponseEntity<PautaResponse> cadastrar(@Valid @RequestBody PautaRequest request) {
        PautaResponse response = pautaService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
