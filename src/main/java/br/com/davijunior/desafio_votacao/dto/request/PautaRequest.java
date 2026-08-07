package br.com.davijunior.desafio_votacao.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PautaRequest(
        @NotBlank String titulo,
        String descricao) {
}