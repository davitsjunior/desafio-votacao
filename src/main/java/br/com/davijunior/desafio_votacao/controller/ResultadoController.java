package br.com.davijunior.desafio_votacao.controller;

import br.com.davijunior.desafio_votacao.dto.response.ResultadoResponse;
import br.com.davijunior.desafio_votacao.service.ResultadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pautas/{pautaId}/resultado")
@RequiredArgsConstructor
public class ResultadoController {

    private final ResultadoService resultadoService;

    @GetMapping
    public ResultadoResponse consultar(@PathVariable Long pautaId) {
        return resultadoService.apurar(pautaId);
    }
}