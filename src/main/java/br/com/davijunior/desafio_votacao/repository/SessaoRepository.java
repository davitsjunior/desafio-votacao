package br.com.davijunior.desafio_votacao.repository;

import br.com.davijunior.desafio_votacao.entity.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {

    boolean existsByPautaId(Long pautaId);
}