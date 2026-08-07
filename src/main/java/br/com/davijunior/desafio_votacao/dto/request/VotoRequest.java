package br.com.davijunior.desafio_votacao.dto.request;

import br.com.davijunior.desafio_votacao.enums.OpcaoVoto;

public record VotoRequest(String cpf, OpcaoVoto opcao) {
}