package com.scih.scheduler;

import com.scih.client.DataClient;
import com.scih.dto.LaudoDTO;
import com.scih.dto.PacienteDTO;
import com.scih.service.IntegracaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Agendador que dispara o ciclo de monitoramento automatizado.
 *
 * Implementa o componente "@Scheduled" descrito na seção 4.3 do artigo:
 * "O fluxo da aplicação se inicia com o componente de agendamento do Spring Boot
 *  (@Scheduled), disparando requisições para verificações dos dados em intervalos pré-definidos."
 *
 * Fluxo executado a cada ciclo (Diagrama de Sequência, Figura 4):
 *  1. Solicita novos laudos ao DataClient (mock ou real)
 *  2. Para cada laudo, busca a localização do paciente pelo prontuário
 *  3. Delega a unificação e detecção de eventos ao IntegracaoService
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ColetaScheduler {

    /** Injetado pelo Spring — MockDataClient (dev) ou RealDataClient (prod) */
    private final DataClient dataClient;
    private final IntegracaoService integracaoService;

    /**
     * Ciclo principal de monitoramento.
     * Intervalo configurável via scih.coleta.intervalo-ms (padrão: 5 minutos).
     */
    @Scheduled(fixedDelayString = "${scih.coleta.intervalo-ms:300000}")
    public void executarCicloDeColeta() {
        LocalDateTime inicio = LocalDateTime.now();
        log.info("[Scheduler] ▶ Iniciando ciclo de coleta — {}", inicio);

        LocalDate dataLaudos = LocalDate.of(2026,6,25);

        try {
            List<LaudoDTO> laudos = dataClient.buscarLaudos(dataLaudos);

            if (laudos.isEmpty()) {
                log.info("[Scheduler] Nenhum laudo novo encontrado.");
                return;
            }

            log.info("[Scheduler] {} laudo(s) recebido(s). Iniciando unificação...", laudos.size());

            int processados = 0;
            int falhas = 0;

            for (LaudoDTO laudo : laudos) {
                try {
                    PacienteDTO paciente = dataClient.buscarPaciente(laudo.getProntuarioId());

                    if (paciente == null) {
                        log.warn("[Scheduler] Paciente não encontrado para prontuário: {}",
                                laudo.getProntuarioId());
                        falhas++;
                        continue;
                    }

                    integracaoService.unificarEPersistir(laudo, paciente);
                    processados++;

                } catch (Exception e) {
                    log.error("[Scheduler] Erro ao processar laudo {}: {}",
                            laudo.getRegistroAmostra(), e.getMessage());
                    falhas++;
                }
            }

            log.info("[Scheduler] ✔ Ciclo concluído — processados: {} | falhas: {} | duração: {}ms",
                    processados, falhas,
                    java.time.Duration.between(inicio, LocalDateTime.now()).toMillis());

        } catch (Exception e) {
            log.error("[Scheduler] Falha crítica no ciclo de coleta: {}", e.getMessage(), e);
        }
    }
}
