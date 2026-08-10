package br.com.davijunior.desafio_votacao.repository;

import br.com.davijunior.desafio_votacao.entity.Pauta;
import br.com.davijunior.desafio_votacao.entity.Sessao;
import br.com.davijunior.desafio_votacao.enums.StatusSessao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SessaoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SessaoRepository sessaoRepository;

    @Test
    void deveEncontrarSessaoPorPautaId() {
        Pauta pauta = entityManager.persistAndFlush(new Pauta(null, "Título", "Descrição", Instant.now()));

        Instant inicio = Instant.now();
        Sessao sessao = entityManager.persistAndFlush(
                new Sessao(null, pauta, inicio, inicio.plus(1, ChronoUnit.MINUTES), StatusSessao.ABERTA));

        Optional<Sessao> encontrada = sessaoRepository.findByPautaId(pauta.getId());

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getId()).isEqualTo(sessao.getId());
    }

    @Test
    void naoDeveEncontrarSessaoQuandoPautaNaoPossuiSessao() {
        Pauta pauta = entityManager.persistAndFlush(new Pauta(null, "Título", "Descrição", Instant.now()));

        Optional<Sessao> encontrada = sessaoRepository.findByPautaId(pauta.getId());

        assertThat(encontrada).isEmpty();
    }
}
