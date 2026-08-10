package br.com.davijunior.desafio_votacao.controller;

import br.com.davijunior.desafio_votacao.dto.request.SessaoRequest;
import br.com.davijunior.desafio_votacao.dto.response.SessaoResponse;
import br.com.davijunior.desafio_votacao.enums.StatusSessao;
import br.com.davijunior.desafio_votacao.exception.PautaNaoEncontradaException;
import br.com.davijunior.desafio_votacao.exception.SessaoJaExisteException;
import br.com.davijunior.desafio_votacao.service.SessaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessaoController.class)
class SessaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private SessaoService sessaoService;

    @Test
    void deveAbrirSessaoSemCorpo() throws Exception {
        SessaoResponse response = new SessaoResponse(10L, 1L, Instant.now(), Instant.now().plusSeconds(60), StatusSessao.ABERTA);
        when(sessaoService.abrir(eq(1L), any(SessaoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/pautas/{pautaId}/sessao", 1L))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.status").value("ABERTA"));
    }

    @Test
    void deveRetornar404QuandoPautaNaoEncontrada() throws Exception {
        when(sessaoService.abrir(eq(1L), any(SessaoRequest.class)))
                .thenThrow(new PautaNaoEncontradaException("Pauta não encontrada: 1"));

        mockMvc.perform(post("/pautas/{pautaId}/sessao", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SessaoRequest(null))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar409QuandoSessaoJaExiste() throws Exception {
        when(sessaoService.abrir(eq(1L), any(SessaoRequest.class)))
                .thenThrow(new SessaoJaExisteException("Já existe sessão para a pauta: 1"));

        mockMvc.perform(post("/pautas/{pautaId}/sessao", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SessaoRequest(null))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar400QuandoDuracaoMinutosForZero() throws Exception {
        mockMvc.perform(post("/pautas/{pautaId}/sessao", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SessaoRequest(0))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar400QuandoDuracaoMinutosForNegativa() throws Exception {
        mockMvc.perform(post("/pautas/{pautaId}/sessao", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SessaoRequest(-1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar409QuandoViolarRestricaoDeIntegridade() throws Exception {
        when(sessaoService.abrir(eq(1L), any(SessaoRequest.class)))
                .thenThrow(new DataIntegrityViolationException("uk_sessao_pauta"));

        mockMvc.perform(post("/pautas/{pautaId}/sessao", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SessaoRequest(null))))
                .andExpect(status().isConflict());
    }
}
