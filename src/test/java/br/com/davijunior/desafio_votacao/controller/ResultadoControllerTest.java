package br.com.davijunior.desafio_votacao.controller;

import br.com.davijunior.desafio_votacao.dto.response.ResultadoResponse;
import br.com.davijunior.desafio_votacao.exception.SessaoNaoEncontradaException;
import br.com.davijunior.desafio_votacao.service.ResultadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResultadoController.class)
class ResultadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResultadoService resultadoService;

    @Test
    void deveConsultarResultado() throws Exception {
        when(resultadoService.apurar(1L)).thenReturn(new ResultadoResponse(7L, 3L, 10L));

        mockMvc.perform(get("/pautas/{pautaId}/resultado", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSim").value(7))
                .andExpect(jsonPath("$.totalNao").value(3))
                .andExpect(jsonPath("$.totalVotos").value(10));
    }

    @Test
    void deveRetornar404QuandoSessaoNaoEncontrada() throws Exception {
        when(resultadoService.apurar(1L))
                .thenThrow(new SessaoNaoEncontradaException("Sessão não encontrada para a pauta: 1"));

        mockMvc.perform(get("/pautas/{pautaId}/resultado", 1L))
                .andExpect(status().isNotFound());
    }
}
