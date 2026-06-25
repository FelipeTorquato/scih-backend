package com.scih.service;

import com.scih.dto.AlertaDTO;
import com.scih.dto.LaudoDTO;
import com.scih.dto.PacienteDTO;
import com.scih.entity.PerfilAntimicrobiano;
import com.scih.entity.RealAmostra;
import com.scih.repository.RealAmostraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Camada de notificação — detecta eventos adversos e dispara alertas.
 *
 * Responsabilidades (seção 3.5, Camada de Notificação do artigo):
 *  - Identificar patógenos multirresistentes processados.
 *  - Publicar AlertaDTO via ApplicationEventPublisher do Spring.
 *  - Marcar a amostra como "alerta emitido" para evitar notificações duplicadas.
 *
 * A interface JavaFX pode escutar os eventos via ApplicationListener<AlertaEvent>
 * ou via polling ao endpoint REST /api/alertas/pendentes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertaService {

    private final RealAmostraRepository amostrasRepo;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Emite alerta para evento adverso detectado.
     * Chamado pelo IntegracaoService após identificar multirresistência.
     */
    public void notificar(RealAmostra amostra, PacienteDTO paciente, LaudoDTO laudo) {
        List<String> resistentes = laudo.getMicrobiologia().getAntibiograma().stream()
                .filter(e -> "Resistente".equalsIgnoreCase(e.getPerfil()))
                .map(LaudoDTO.EntradaAntibiograma::getAntimicrobiano)
                .toList();

        AlertaDTO alerta = AlertaDTO.builder()
                .idAmostra(amostra.getId())
                .nomePaciente(paciente.getNome())
                .prontuario(paciente.getProntuarioId())
                .unidadeInternacao(paciente.getUnidadeInternacao())
                .leito(paciente.getLeito())
                .patogeno(laudo.getMicrobiologia().getPatogeno())
                .multirresistente(true)
                .antimicrobianosResistentes(resistentes)
                .dataHoraAlerta(LocalDateTime.now())
                .build();

        // Publica o evento internamente — JavaFX pode escutar via @EventListener
        eventPublisher.publishEvent(new AlertaEvent(this, alerta));

        // Marca amostra para evitar reemissão
        amostra.setAlertaEmitido(true);
        amostrasRepo.save(amostra);

        log.warn("[Alerta] ⚠ EVENTO ADVERSO — {}", alerta.getResumo());
    }

    // ----------------------------------------------------------------

    /**
     * Evento Spring publicado a cada novo alerta.
     * A camada JavaFX escuta este evento via ApplicationListener.
     */
    public static class AlertaEvent extends org.springframework.context.ApplicationEvent {
        private final AlertaDTO alerta;

        public AlertaEvent(Object source, AlertaDTO alerta) {
            super(source);
            this.alerta = alerta;
        }

        public AlertaDTO getAlerta() {
            return alerta;
        }
    }
}
