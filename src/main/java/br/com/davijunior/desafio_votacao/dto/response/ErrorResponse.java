package br.com.davijunior.desafio_votacao.dto.response;

import java.time.Instant;

public record ErrorResponse(Instant timestamp, int status, String message) {
}
