package br.com.davijunior.desafio_votacao.controller;

import br.com.davijunior.desafio_votacao.dto.request.SessaoRequest;
import br.com.davijunior.desafio_votacao.dto.response.SessaoResponse;
import br.com.davijunior.desafio_votacao.service.SessaoService;
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
@RequestMapping("/pautas/{pautaId}/sessao")
@RequiredArgsConstructor
public class SessaoController {

    private final SessaoService sessaoService;

    @PostMapping
    public ResponseEntity<SessaoResponse> abrir(@PathVariable Long pautaId,
                                                 @Valid @RequestBody(required = false) SessaoRequest request) {
        SessaoRequest body = request != null ? request : new SessaoRequest(null);
        SessaoResponse response = sessaoService.abrir(pautaId, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
