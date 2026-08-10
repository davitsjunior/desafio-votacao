package br.com.davijunior.desafio_votacao.client;

import br.com.davijunior.desafio_votacao.enums.StatusVotacao;
import br.com.davijunior.desafio_votacao.exception.AssociadoNaoAptoException;
import br.com.davijunior.desafio_votacao.exception.CpfInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CpfValidacaoClientFakeTest {

    @Test
    void deveLancarCpfInvalidoExceptionQuandoCpfForInvalido() {
        CpfValidacaoClientFake client = new CpfValidacaoClientFake() {
            @Override
            protected boolean cpfInvalido() {
                return true;
            }
        };

        assertThatThrownBy(() -> client.validar("12345678900"))
                .isInstanceOf(CpfInvalidoException.class);
    }

    @Test
    void deveLancarAssociadoNaoAptoExceptionQuandoStatusForUnableToVote() {
        CpfValidacaoClientFake client = new CpfValidacaoClientFake() {
            @Override
            protected boolean cpfInvalido() {
                return false;
            }

            @Override
            protected StatusVotacao statusAleatorio() {
                return StatusVotacao.UNABLE_TO_VOTE;
            }
        };

        assertThatThrownBy(() -> client.validar("12345678900"))
                .isInstanceOf(AssociadoNaoAptoException.class);
    }

    @Test
    void deveRetornarAbleToVoteQuandoCpfValidoEAptoAVotar() {
        CpfValidacaoClientFake client = new CpfValidacaoClientFake() {
            @Override
            protected boolean cpfInvalido() {
                return false;
            }

            @Override
            protected StatusVotacao statusAleatorio() {
                return StatusVotacao.ABLE_TO_VOTE;
            }
        };

        CpfValidacaoResponse response = client.validar("12345678900");

        assertThat(response.status()).isEqualTo(StatusVotacao.ABLE_TO_VOTE);
    }
}