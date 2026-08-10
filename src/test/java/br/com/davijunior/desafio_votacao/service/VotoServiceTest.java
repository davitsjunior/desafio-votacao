package br.com.davijunior.desafio_votacao.service;

import br.com.davijunior.desafio_votacao.client.CpfValidacaoClient;
import br.com.davijunior.desafio_votacao.client.CpfValidacaoResponse;
import br.com.davijunior.desafio_votacao.dto.request.VotoRequest;
import br.com.davijunior.desafio_votacao.dto.response.VotoResponse;
import br.com.davijunior.desafio_votacao.entity.Associado;
import br.com.davijunior.desafio_votacao.entity.Sessao;
import br.com.davijunior.desafio_votacao.entity.Voto;
import br.com.davijunior.desafio_votacao.enums.OpcaoVoto;
import br.com.davijunior.desafio_votacao.enums.StatusVotacao;
import br.com.davijunior.desafio_votacao.exception.AssociadoNaoAptoException;
import br.com.davijunior.desafio_votacao.exception.CpfInvalidoException;
import br.com.davijunior.desafio_votacao.exception.SessaoFechadaException;
import br.com.davijunior.desafio_votacao.exception.SessaoNaoEncontradaException;
import br.com.davijunior.desafio_votacao.exception.VotoDuplicadoException;
import br.com.davijunior.desafio_votacao.repository.AssociadoRepository;
import br.com.davijunior.desafio_votacao.repository.SessaoRepository;
import br.com.davijunior.desafio_votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private SessaoRepository sessaoRepository;

    @Mock
    private AssociadoRepository associadoRepository;

    @Mock
    private CpfValidacaoClient cpfValidacaoClient;

    @InjectMocks
    private VotoService votoService;

    private Sessao sessaoAberta() {
        Sessao sessao = new Sessao();
        sessao.setId(10L);
        sessao.setInicio(Instant.now());
        sessao.setFim(Instant.now().plus(1, ChronoUnit.MINUTES));
        return sessao;
    }

    @Test
    void deveRegistrarVotoComAssociadoExistente() {
        Sessao sessao = sessaoAberta();
        Associado associado = new Associado(2L, "12345678900");

        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(sessao));
        when(cpfValidacaoClient.validar("12345678900")).thenReturn(new CpfValidacaoResponse(StatusVotacao.ABLE_TO_VOTE));
        when(associadoRepository.findByCpf("12345678900")).thenReturn(Optional.of(associado));
        when(votoRepository.existsBySessaoIdAndAssociadoId(10L, 2L)).thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenAnswer(invocation -> {
            Voto voto = invocation.getArgument(0);
            voto.setId(100L);
            return voto;
        });

        VotoResponse response = votoService.votar(1L, new VotoRequest("12345678900", OpcaoVoto.SIM));

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.associadoId()).isEqualTo(2L);
        assertThat(response.opcao()).isEqualTo(OpcaoVoto.SIM);
    }

    @Test
    void deveCriarAssociadoQuandoCpfNaoCadastrado() {
        Sessao sessao = sessaoAberta();

        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(sessao));
        when(cpfValidacaoClient.validar("12345678900")).thenReturn(new CpfValidacaoResponse(StatusVotacao.ABLE_TO_VOTE));
        when(associadoRepository.findByCpf("12345678900")).thenReturn(Optional.empty());
        when(associadoRepository.save(any(Associado.class))).thenAnswer(invocation -> {
            Associado associado = invocation.getArgument(0);
            associado.setId(3L);
            return associado;
        });
        when(votoRepository.existsBySessaoIdAndAssociadoId(10L, 3L)).thenReturn(false);
        when(votoRepository.save(any(Voto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VotoResponse response = votoService.votar(1L, new VotoRequest("12345678900", OpcaoVoto.NAO));

        assertThat(response.associadoId()).isEqualTo(3L);
    }

    @Test
    void deveLancarExceptionQuandoSessaoNaoEncontrada() {
        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> votoService.votar(1L, new VotoRequest("12345678900", OpcaoVoto.SIM)))
                .isInstanceOf(SessaoNaoEncontradaException.class);
    }

    @Test
    void deveLancarExceptionQuandoSessaoEncerrada() {
        Sessao sessao = new Sessao();
        sessao.setId(10L);
        sessao.setInicio(Instant.now().minus(2, ChronoUnit.MINUTES));
        sessao.setFim(Instant.now().minus(1, ChronoUnit.MINUTES));

        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(sessao));

        assertThatThrownBy(() -> votoService.votar(1L, new VotoRequest("12345678900", OpcaoVoto.SIM)))
                .isInstanceOf(SessaoFechadaException.class);
    }

    @Test
    void deveLancarExceptionQuandoAssociadoJaVotou() {
        Sessao sessao = sessaoAberta();
        Associado associado = new Associado(2L, "12345678900");

        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(sessao));
        when(cpfValidacaoClient.validar("12345678900")).thenReturn(new CpfValidacaoResponse(StatusVotacao.ABLE_TO_VOTE));
        when(associadoRepository.findByCpf("12345678900")).thenReturn(Optional.of(associado));
        when(votoRepository.existsBySessaoIdAndAssociadoId(10L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> votoService.votar(1L, new VotoRequest("12345678900", OpcaoVoto.SIM)))
                .isInstanceOf(VotoDuplicadoException.class);
    }

    @Test
    void deveLancarExceptionQuandoCpfForInvalido() {
        Sessao sessao = sessaoAberta();

        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(sessao));
        when(cpfValidacaoClient.validar("12345678900")).thenThrow(new CpfInvalidoException("CPF inválido"));

        assertThatThrownBy(() -> votoService.votar(1L, new VotoRequest("12345678900", OpcaoVoto.SIM)))
                .isInstanceOf(CpfInvalidoException.class);
    }

    @Test
    void deveLancarExceptionQuandoAssociadoNaoAptoAVotar() {
        Sessao sessao = sessaoAberta();

        when(sessaoRepository.findByPautaId(1L)).thenReturn(Optional.of(sessao));
        when(cpfValidacaoClient.validar("12345678900")).thenThrow(new AssociadoNaoAptoException("Associado não apto a votar"));

        assertThatThrownBy(() -> votoService.votar(1L, new VotoRequest("12345678900", OpcaoVoto.SIM)))
                .isInstanceOf(AssociadoNaoAptoException.class);
    }
}
