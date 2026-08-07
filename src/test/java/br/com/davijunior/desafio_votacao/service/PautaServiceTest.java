package br.com.davijunior.desafio_votacao.service;

import br.com.davijunior.desafio_votacao.dto.request.PautaRequest;
import br.com.davijunior.desafio_votacao.dto.response.PautaResponse;
import br.com.davijunior.desafio_votacao.entity.Pauta;
import br.com.davijunior.desafio_votacao.repository.PautaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @InjectMocks
    private PautaService pautaService;

    @Test
    void deveCadastrarPauta() {
        PautaRequest request = new PautaRequest("Título", "Descrição");

        when(pautaRepository.save(any(Pauta.class))).thenAnswer(invocation -> {
            Pauta pauta = invocation.getArgument(0);
            pauta.setId(1L);
            return pauta;
        });

        PautaResponse response = pautaService.cadastrar(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.titulo()).isEqualTo("Título");
        assertThat(response.descricao()).isEqualTo("Descrição");
        assertThat(response.createdAt()).isNotNull();

        ArgumentCaptor<Pauta> captor = ArgumentCaptor.forClass(Pauta.class);
        org.mockito.Mockito.verify(pautaRepository).save(captor.capture());
        assertThat(captor.getValue().getCreatedAt()).isBeforeOrEqualTo(Instant.now());
    }
}
