package br.com.davijunior.desafio_votacao.controller;

import br.com.davijunior.desafio_votacao.dto.request.PautaRequest;
import br.com.davijunior.desafio_votacao.dto.response.PautaResponse;
import br.com.davijunior.desafio_votacao.service.PautaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PautaController.class)
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private PautaService pautaService;

    @Test
    void deveCadastrarPauta() throws Exception {
        PautaResponse response = new PautaResponse(1L, "Título", "Descrição", Instant.now());
        when(pautaService.cadastrar(any(PautaRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PautaRequest("Título", "Descrição"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.titulo").value("Título"));
    }

    @Test
    void deveRetornar400QuandoTituloEmBranco() throws Exception {
        mockMvc.perform(post("/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PautaRequest("", "Descrição"))))
                .andExpect(status().isBadRequest());
    }
}
