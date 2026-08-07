package br.com.davijunior.desafio_votacao.repository;

import br.com.davijunior.desafio_votacao.entity.Voto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoRepository extends JpaRepository<Voto, Long> {
}