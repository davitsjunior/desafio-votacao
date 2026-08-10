package br.com.davijunior.desafio_votacao.client;

import br.com.davijunior.desafio_votacao.enums.StatusVotacao;

public record CpfValidacaoResponse(StatusVotacao status) {
}