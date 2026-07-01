package com.scih.service;

import com.scih.dto.AlertaDTO;
import com.scih.dto.LaudoDTO;
import com.scih.dto.PacienteDTO;
import com.scih.entity.RealAmostra;
import com.scih.repository.RealAmostraRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Camada de notificação — detecta eventos adversos e dispara alertas.
 * <p>
 * Responsabilidades (seção 3.5, Camada de Notificação do artigo):
 * - Identificar patógenos multirresistentes processados.
 * - Publicar AlertaDTO via ApplicationEventPublisher do Spring.
 * - Marcar a amostra como "alerta emitido" para evitar notificações duplicadas.
 * <p>
 * A interface JavaFX pode escutar os eventos via ApplicationListener<AlertaEvent>
 * ou via polling ao endpoint REST /api/alertas/pendentes.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AlertaService {

    private final RealAmostraRepository amostrasRepo;

    /**
     * Emite alerta para evento adverso detectado.
     * Chamado pelo IntegracaoService após identificar multirresistência.
     */
    public void notificar(RealAmostra amostra, PacienteDTO paciente, LaudoDTO laudo) {
        List<String> resistentes = laudo.getPerfilResistencia().entrySet().stream()
                .filter(e -> "RESISTENTE".equalsIgnoreCase(e.getValue()))
                .map(Map.Entry::getKey)
                .toList();

        AlertaDTO alerta = AlertaDTO.builder()
                .idAmostra(amostra.getId())
                .nomePaciente(paciente.getNome())
                .prontuario(paciente.getProntuarioId())
                .unidadeInternacao(paciente.getUnidadeInternacao())
                .leito(paciente.getLeito())
                .patogeno(laudo.getMicroorganismo())
                .multirresistente(true)
                .antimicrobianosResistentes(resistentes)
                .dataHoraAlerta(LocalDateTime.now())
                .build();

        // Marca amostra para evitar reemissão
        amostra.setAlertaEmitido(true);
        amostrasRepo.save(amostra);

        log.warn("[Alerta] ⚠ EVENTO ADVERSO — {}", alerta.getResumo());
    }
}
