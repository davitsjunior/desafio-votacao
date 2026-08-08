package br.com.davijunior.desafio_votacao.service;

import br.com.davijunior.desafio_votacao.dto.request.VotoRequest;
import br.com.davijunior.desafio_votacao.dto.response.VotoResponse;
import br.com.davijunior.desafio_votacao.entity.Associado;
import br.com.davijunior.desafio_votacao.entity.Sessao;
import br.com.davijunior.desafio_votacao.entity.Voto;
import br.com.davijunior.desafio_votacao.exception.SessaoFechadaException;
import br.com.davijunior.desafio_votacao.exception.SessaoNaoEncontradaException;
import br.com.davijunior.desafio_votacao.exception.VotoDuplicadoException;
import br.com.davijunior.desafio_votacao.repository.AssociadoRepository;
import br.com.davijunior.desafio_votacao.repository.SessaoRepository;
import br.com.davijunior.desafio_votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class VotoService {

    private final VotoRepository votoRepository;
    private final SessaoRepository sessaoRepository;
    private final AssociadoRepository associadoRepository;

    public VotoResponse votar(Long pautaId, VotoRequest request) {
        Sessao sessao = sessaoRepository.findByPautaId(pautaId)
                .orElseThrow(() -> new SessaoNaoEncontradaException("Sessão não encontrada para a pauta: " + pautaId));

        if (Instant.now().isAfter(sessao.getFim())) {
            throw new SessaoFechadaException("Sessão encerrada para a pauta: " + pautaId);
        }

        Associado associado = associadoRepository.findByCpf(request.cpf())
                .orElseGet(() -> associadoRepository.save(new Associado(null, request.cpf())));

        if (votoRepository.existsBySessaoIdAndAssociadoId(sessao.getId(), associado.getId())) {
            throw new VotoDuplicadoException("Associado já votou nesta sessão");
        }

        Voto voto = new Voto();
        voto.setSessao(sessao);
        voto.setAssociado(associado);
        voto.setOpcao(request.opcao());
        voto.setCreatedAt(Instant.now());

        Voto salvo = votoRepository.save(voto);

        return new VotoResponse(salvo.getId(), associado.getId(), salvo.getOpcao(), salvo.getCreatedAt());
    }
}