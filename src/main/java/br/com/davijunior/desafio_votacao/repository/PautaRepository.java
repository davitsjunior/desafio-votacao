package br.com.davijunior.desafio_votacao.repository;

import br.com.davijunior.desafio_votacao.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PautaRepository extends JpaRepository<Pauta, Long> {
}