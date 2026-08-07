package br.com.davijunior.desafio_votacao.service;

import br.com.davijunior.desafio_votacao.dto.response.ResultadoResponse;
import br.com.davijunior.desafio_votacao.entity.Sessao;
import br.com.davijunior.desafio_votacao.enums.OpcaoVoto;
import br.com.davijunior.desafio_votacao.exception.SessaoNaoEncontradaException;
import br.com.davijunior.desafio_votacao.repository.SessaoRepository;
import br.com.davijunior.desafio_votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResultadoService {

    private final SessaoRepository sessaoRepository;
    private final VotoRepository votoRepository;

    public ResultadoResponse apurar(Long pautaId) {
        Sessao sessao = sessaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> new SessaoNaoEncontradaException("Sessão não encontrada para a pauta: " + pautaId));

        long totalSim = votoRepository.countBySessaoIdAndOpcao(sessao.getId(), OpcaoVoto.SIM);
        long totalNao = votoRepository.countBySessaoIdAndOpcao(sessao.getId(), OpcaoVoto.NAO);

        return new ResultadoResponse(totalSim, totalNao, totalSim + totalNao);
    }
}