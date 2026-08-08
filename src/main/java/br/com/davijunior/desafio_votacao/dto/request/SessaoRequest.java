package br.com.davijunior.desafio_votacao.dto.request;

import jakarta.validation.constraints.Positive;

public record SessaoRequest(@Positive Integer duracaoMinutos) {
}