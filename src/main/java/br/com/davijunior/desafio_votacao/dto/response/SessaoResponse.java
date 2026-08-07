package br.com.davijunior.desafio_votacao.dto.response;

import br.com.davijunior.desafio_votacao.enums.StatusSessao;

import java.time.Instant;

public record SessaoResponse(Long id, Long pautaId, Instant inicio, Instant fim, StatusSessao status) {
}