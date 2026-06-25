package com.scih.client;

import com.scih.dto.LaudoDTO;
import com.scih.dto.PacienteDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementação de DataClient ativa no profile "prod".
 *
 * TODO — Implementar com automação real:
 *   - buscarLaudos(): acionar AIScraper/Playwright no sistema R.E.A.L,
 *     capturar PDFs de laudos positivos, enviar ao módulo LLM e receber
 *     o payload JSON (Quadro 1 do artigo).
 *   - buscarPaciente(): acionar AIScraper/Playwright no IntegraSUS,
 *     buscar por número de prontuário e retornar localização e dados cadastrais.
 *
 * Referência de trabalhos futuros (seção 6 do artigo):
 *   "a dependência da extensão AIScraper será substituída por ferramentas como
 *    Playwright ou Selenium, com scripts executados diretamente no backend."
 */
@Component
@Profile("prod")
@Slf4j
public class RealDataClient implements DataClient {

    @Override
    public List<LaudoDTO> buscarLaudos(LocalDate data) {
        // TODO: integrar com Playwright/AIScraper → R.E.A.L → LLM → LaudoDTO
        throw new UnsupportedOperationException(
                "RealDataClient.buscarLaudos() ainda não implementado. " +
                "Ative o profile 'dev' para testes com mock service.");
    }

    @Override
    public PacienteDTO buscarPaciente(String prontuarioId) {
        // TODO: integrar com Playwright/AIScraper → IntegraSUS → PacienteDTO
        throw new UnsupportedOperationException(
                "RealDataClient.buscarPaciente() ainda não implementado. " +
                "Ative o profile 'dev' para testes com mock service.");
    }
}
