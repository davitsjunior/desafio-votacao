package br.com.davijunior.desafio_votacao.repository;

import br.com.davijunior.desafio_votacao.entity.Associado;
import br.com.davijunior.desafio_votacao.entity.Pauta;
import br.com.davijunior.desafio_votacao.entity.Sessao;
import br.com.davijunior.desafio_votacao.entity.Voto;
import br.com.davijunior.desafio_votacao.enums.OpcaoVoto;
import br.com.davijunior.desafio_votacao.enums.StatusSessao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VotoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VotoRepository votoRepository;

    private Sessao criarSessao() {
        Pauta pauta = entityManager.persistAndFlush(new Pauta(null, "Título", "Descrição", Instant.now()));
        Instant inicio = Instant.now();
        return entityManager.persistAndFlush(
                new Sessao(null, pauta, inicio, inicio.plus(1, ChronoUnit.MINUTES), StatusSessao.ABERTA));
    }

    private Associado criarAssociado(String cpf) {
        return entityManager.persistAndFlush(new Associado(null, cpf));
    }

    @Test
    void deveIndicarQueAssociadoJaVotouNaSessao() {
        Sessao sessao = criarSessao();
        Associado associado = criarAssociado("11111111111");
        entityManager.persistAndFlush(new Voto(null, sessao, associado, OpcaoVoto.SIM, Instant.now()));

        boolean jaVotou = votoRepository.existsBySessaoIdAndAssociadoId(sessao.getId(), associado.getId());

        assertThat(jaVotou).isTrue();
    }

    @Test
    void naoDeveIndicarVotoQuandoAssociadoNaoVotouNaSessao() {
        Sessao sessao = criarSessao();
        Associado associado = criarAssociado("22222222222");

        boolean jaVotou = votoRepository.existsBySessaoIdAndAssociadoId(sessao.getId(), associado.getId());

        assertThat(jaVotou).isFalse();
    }

    @Test
    void deveContarVotosPorSessaoEOpcao() {
        Sessao sessao = criarSessao();
        Associado associado1 = criarAssociado("33333333333");
        Associado associado2 = criarAssociado("44444444444");
        Associado associado3 = criarAssociado("55555555555");

        entityManager.persistAndFlush(new Voto(null, sessao, associado1, OpcaoVoto.SIM, Instant.now()));
        entityManager.persistAndFlush(new Voto(null, sessao, associado2, OpcaoVoto.SIM, Instant.now()));
        entityManager.persistAndFlush(new Voto(null, sessao, associado3, OpcaoVoto.NAO, Instant.now()));

        assertThat(votoRepository.countBySessaoIdAndOpcao(sessao.getId(), OpcaoVoto.SIM)).isEqualTo(2);
        assertThat(votoRepository.countBySessaoIdAndOpcao(sessao.getId(), OpcaoVoto.NAO)).isEqualTo(1);
    }
}