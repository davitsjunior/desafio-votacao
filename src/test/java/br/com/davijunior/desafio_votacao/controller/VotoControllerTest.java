package br.com.davijunior.desafio_votacao.controller;

import br.com.davijunior.desafio_votacao.dto.request.VotoRequest;
import br.com.davijunior.desafio_votacao.dto.response.VotoResponse;
import br.com.davijunior.desafio_votacao.enums.OpcaoVoto;
import br.com.davijunior.desafio_votacao.exception.SessaoFechadaException;
import br.com.davijunior.desafio_votacao.exception.SessaoNaoEncontradaException;
import br.com.davijunior.desafio_votacao.exception.VotoDuplicadoException;
import br.com.davijunior.desafio_votacao.service.VotoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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

@WebMvcTest(VotoController.class)
class VotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private VotoService votoService;

    @Test
    void deveRegistrarVoto() throws Exception {
        VotoResponse response = new VotoResponse(100L, 2L, OpcaoVoto.SIM, Instant.now());
        when(votoService.votar(eq(1L), any(VotoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/pautas/{pautaId}/votos", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VotoRequest("12345678900", OpcaoVoto.SIM))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.opcao").value("SIM"));
    }

    @Test
    void deveRetornar400QuandoCpfEmBranco() throws Exception {
        mockMvc.perform(post("/v1/pautas/{pautaId}/votos", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VotoRequest("", OpcaoVoto.SIM))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornar404QuandoSessaoNaoEncontrada() throws Exception {
        when(votoService.votar(eq(1L), any(VotoRequest.class)))
                .thenThrow(new SessaoNaoEncontradaException("Sessão não encontrada para a pauta: 1"));

        mockMvc.perform(post("/v1/pautas/{pautaId}/votos", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VotoRequest("12345678900", OpcaoVoto.SIM))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRetornar409QuandoSessaoFechada() throws Exception {
        when(votoService.votar(eq(1L), any(VotoRequest.class)))
                .thenThrow(new SessaoFechadaException("Sessão encerrada para a pauta: 1"));

        mockMvc.perform(post("/v1/pautas/{pautaId}/votos", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VotoRequest("12345678900", OpcaoVoto.SIM))))
                .andExpect(status().isConflict());
    }

    @Test
    void deveRetornar409QuandoVotoDuplicado() throws Exception {
        when(votoService.votar(eq(1L), any(VotoRequest.class)))
                .thenThrow(new VotoDuplicadoException("Associado já votou nesta sessão"));

        mockMvc.perform(post("/v1/pautas/{pautaId}/votos", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new VotoRequest("12345678900", OpcaoVoto.SIM))))
                .andExpect(status().isConflict());
    }
}
