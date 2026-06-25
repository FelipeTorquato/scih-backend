package com.scih.client;

import com.scih.dto.LaudoDTO;
import com.scih.dto.PacienteDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Implementação de DataClient ativa no profile "dev".
 * Consome o mock service hospedado em nuvem (Railway/Render).
 * Simula o comportamento do AIScraper + sistemas reais sem acesso à rede hospitalar.
 */
@Component
@Profile("dev")
@Slf4j
public class MockDataClient implements DataClient {

    @Value("${scih.mock.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public List<LaudoDTO> buscarLaudos(LocalDate data) {
        String url = baseUrl + "/real/laudos?data_coleta=" + data;
        log.debug("[MockDataClient] Buscando laudos em: {}", url);

        try {
            LaudoDTO[] laudos = restTemplate.getForObject(url, LaudoDTO[].class);
            List<LaudoDTO> resultado = laudos != null ? Arrays.asList(laudos) : List.of();
            log.info("[MockDataClient] {} laudo(s) recebido(s) para {}", resultado.size(), data);
            return resultado;
        } catch (Exception e) {
            log.error("[MockDataClient] Falha ao buscar laudos: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public PacienteDTO buscarPaciente(String prontuarioId) {
        String url = baseUrl + "/integrasus/paciente?prontuario=" + prontuarioId;
        log.debug("[MockDataClient] Buscando paciente prontuário {} em: {}", prontuarioId, url);

        try {
            PacienteDTO paciente = restTemplate.getForObject(url, PacienteDTO.class);
            log.info("[MockDataClient] Paciente encontrado: {}", prontuarioId);
            return paciente;
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("[MockDataClient] Prontuário {} não encontrado no mock", prontuarioId);
            return null;
        } catch (Exception e) {
            log.error("[MockDataClient] Falha ao buscar paciente {}: {}", prontuarioId, e.getMessage());
            return null;
        }
    }
}
