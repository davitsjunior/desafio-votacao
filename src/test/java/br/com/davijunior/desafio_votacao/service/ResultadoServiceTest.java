package br.com.davijunior.desafio_votacao.service;

import br.com.davijunior.desafio_votacao.dto.response.ResultadoResponse;
import br.com.davijunior.desafio_votacao.entity.Sessao;
import br.com.davijunior.desafio_votacao.enums.OpcaoVoto;
import br.com.davijunior.desafio_votacao.exception.SessaoNaoEncontradaException;
import br.com.davijunior.desafio_votacao.repository.SessaoRepository;
import br.com.davijunior.desafio_votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultadoServiceTest {

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private VotoRepository votoRepository;

    @InjectMocks
    private ResultadoService resultadoService;

    @Test
    void deveApurarResultado() {
        Sessao sessao = new Sessao();
        sessao.setId(10L);

        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(sessao));
        when(votoRepository.countBySessaoIdAndOpcao(10L, OpcaoVoto.SIM)).thenReturn(7L);
        when(votoRepository.countBySessaoIdAndOpcao(10L, OpcaoVoto.NAO)).thenReturn(3L);

        ResultadoResponse response = resultadoService.apurar(1L);

        assertThat(response.totalSim()).isEqualTo(7L);
        assertThat(response.totalNao()).isEqualTo(3L);
        assertThat(response.totalVotos()).isEqualTo(10L);
    }

    @Test
    void deveLancarExceptionQuandoSessaoNaoEncontrada() {
        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultadoService.apurar(1L))
                .isInstanceOf(SessaoNaoEncontradaException.class);
    }
}
