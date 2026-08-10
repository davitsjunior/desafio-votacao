package br.com.davijunior.desafio_votacao.controller;

import br.com.davijunior.desafio_votacao.dto.response.ResultadoResponse;
import br.com.davijunior.desafio_votacao.service.ResultadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/pautas/{pautaId}/resultado")
@RequiredArgsConstructor
@Tag(name = "Resultado")
public class ResultadoController {

    private final ResultadoService resultadoService;

    @GetMapping
    @Operation(summary = "Consulta o resultado da votação da pauta")
    public ResultadoResponse consultar(@PathVariable Long pautaId) {
        return resultadoService.apurar(pautaId);
    }
}