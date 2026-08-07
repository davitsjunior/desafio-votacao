package br.com.davijunior.desafio_votacao.service;

import br.com.davijunior.desafio_votacao.dto.request.PautaRequest;
import br.com.davijunior.desafio_votacao.dto.response.PautaResponse;
import br.com.davijunior.desafio_votacao.entity.Pauta;
import br.com.davijunior.desafio_votacao.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;

    public PautaResponse cadastrar(PautaRequest request) {
        Pauta pauta = new Pauta();
        pauta.setTitulo(request.titulo());
        pauta.setDescricao(request.descricao());
        pauta.setCreatedAt(Instant.now());

        Pauta salva = pautaRepository.save(pauta);

        return new PautaResponse(salva.getId(), salva.getTitulo(), salva.getDescricao(), salva.getCreatedAt());
    }
}