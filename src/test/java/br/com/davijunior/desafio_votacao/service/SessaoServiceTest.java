package br.com.davijunior.desafio_votacao.service;

import br.com.davijunior.desafio_votacao.dto.request.SessaoRequest;
import br.com.davijunior.desafio_votacao.dto.response.SessaoResponse;
import br.com.davijunior.desafio_votacao.entity.Pauta;
import br.com.davijunior.desafio_votacao.entity.Sessao;
import br.com.davijunior.desafio_votacao.enums.StatusSessao;
import br.com.davijunior.desafio_votacao.exception.PautaNaoEncontradaException;
import br.com.davijunior.desafio_votacao.exception.SessaoJaExisteException;
import br.com.davijunior.desafio_votacao.repository.PautaRepository;
import br.com.davijunior.desafio_votacao.repository.SessaoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessaoServiceTest {

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private SessaoService sessaoService;

    @Test
    void deveAbrirSessaoComDuracaoInformada() {
        Pauta pauta = new Pauta(1L, "Título", "Descrição", Instant.now());

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.existsByPautaId(1L)).thenReturn(false);
        when(sessaoRepository.save(any(Sessao.class))).thenAnswer(invocation -> {
            Sessao sessao = invocation.getArgument(0);
            sessao.setId(10L);
            return sessao;
        });

        SessaoResponse response = sessaoService.abrir(1L, new SessaoRequest(5));

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.pautaId()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo(StatusSessao.ABERTA);
        assertThat(Duration.between(response.inicio(), response.fim()).toMinutes()).isEqualTo(5);
    }

    @Test
    void deveAbrirSessaoComDuracaoPadraoQuandoNaoInformada() {
        Pauta pauta = new Pauta(1L, "Título", "Descrição", Instant.now());

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.existsByPautaId(1L)).thenReturn(false);
        when(sessaoRepository.save(any(Sessao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SessaoResponse response = sessaoService.abrir(1L, new SessaoRequest(null));

        assertThat(Duration.between(response.inicio(), response.fim()).toMinutes()).isEqualTo(1);
    }

    @Test
    void deveLancarExceptionQuandoPautaNaoEncontrada() {
        when(pautaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessaoService.abrir(1L, new SessaoRequest(null)))
                .isInstanceOf(PautaNaoEncontradaException.class);
    }

    @Test
    void deveLancarExceptionQuandoSessaoJaExisteParaPauta() {
        Pauta pauta = new Pauta(1L, "Título", "Descrição", Instant.now());

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoRepository.existsByPautaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> sessaoService.abrir(1L, new SessaoRequest(null)))
                .isInstanceOf(SessaoJaExisteException.class);
    }
}
