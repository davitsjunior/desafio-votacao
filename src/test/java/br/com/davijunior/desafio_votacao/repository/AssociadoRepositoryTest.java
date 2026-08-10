package br.com.davijunior.desafio_votacao.repository;

import br.com.davijunior.desafio_votacao.entity.Associado;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AssociadoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AssociadoRepository associadoRepository;

    @Test
    void deveEncontrarAssociadoPorCpf() {
        Associado associado = entityManager.persistAndFlush(new Associado(null, "12345678901"));

        Optional<Associado> encontrado = associadoRepository.findByCpf("12345678901");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getId()).isEqualTo(associado.getId());
    }

    @Test
    void naoDeveEncontrarAssociadoQuandoCpfNaoCadastrado() {
        Optional<Associado> encontrado = associadoRepository.findByCpf("00000000000");

        assertThat(encontrado).isEmpty();
    }
}