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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessaoService {

    private static final int DURACAO_PADRAO_MINUTOS = 1;

    private final SessaoRepository sessaoRepository;
    private final PautaRepository pautaRepository;

    public SessaoResponse abrir(Long pautaId, SessaoRequest request) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new PautaNaoEncontradaException("Pauta não encontrada: " + pautaId));

        if (sessaoRepository.existsByPautaId(pautaId)) {
            throw new SessaoJaExisteException("Já existe sessão para a pauta: " + pautaId);
        }

        int duracaoMinutos = request.duracaoMinutos() != null ? request.duracaoMinutos() : DURACAO_PADRAO_MINUTOS;

        Instant inicio = Instant.now();

        Sessao sessao = new Sessao();
        sessao.setPauta(pauta);
        sessao.setInicio(inicio);
        sessao.setFim(inicio.plus(duracaoMinutos, ChronoUnit.MINUTES));
        sessao.setStatus(StatusSessao.ABERTA);

        Sessao salva = sessaoRepository.save(sessao);

        log.info("Sessão aberta: id={}, pautaId={}, fim={}", salva.getId(), pauta.getId(), salva.getFim());

        return new SessaoResponse(salva.getId(), pauta.getId(), salva.getInicio(), salva.getFim(), salva.getStatus());
    }
}