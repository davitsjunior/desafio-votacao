package br.com.davijunior.desafio_votacao.client;

import br.com.davijunior.desafio_votacao.enums.StatusVotacao;
import br.com.davijunior.desafio_votacao.exception.AssociadoNaoAptoException;
import br.com.davijunior.desafio_votacao.exception.CpfInvalidoException;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class CpfValidacaoClientFake implements CpfValidacaoClient {

    @Override
    public CpfValidacaoResponse validar(String cpf) {
        if (cpfInvalido()) {
            throw new CpfInvalidoException("CPF inválido");
        }

        StatusVotacao status = statusAleatorio();

        if (status == StatusVotacao.UNABLE_TO_VOTE) {
            throw new AssociadoNaoAptoException("Associado não apto a votar");
        }

        return new CpfValidacaoResponse(status);
    }

    protected boolean cpfInvalido() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    protected StatusVotacao statusAleatorio() {
        return ThreadLocalRandom.current().nextBoolean() ? StatusVotacao.ABLE_TO_VOTE : StatusVotacao.UNABLE_TO_VOTE;
    }
}