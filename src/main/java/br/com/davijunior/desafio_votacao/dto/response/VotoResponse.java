package br.com.davijunior.desafio_votacao.dto.response;

import br.com.davijunior.desafio_votacao.enums.OpcaoVoto;

import java.time.Instant;

public record VotoResponse(Long id, Long associadoId, OpcaoVoto opcao, Instant createdAt) {
}