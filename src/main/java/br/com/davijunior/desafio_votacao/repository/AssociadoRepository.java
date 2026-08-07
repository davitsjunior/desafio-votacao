package br.com.davijunior.desafio_votacao.repository;

import br.com.davijunior.desafio_votacao.entity.Associado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssociadoRepository extends JpaRepository<Associado, Long> {
}