package br.com.davijunior.desafio_votacao.dto.response;

import java.time.Instant;

public record PautaResponse(Long id, String titulo, String descricao, Instant createdAt) {
}