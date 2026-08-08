package br.com.davijunior.desafio_votacao.dto.request;

import br.com.davijunior.desafio_votacao.enums.OpcaoVoto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VotoRequest(
        @NotBlank @Size(max = 20) String cpf,
        @NotNull OpcaoVoto opcao) {
}