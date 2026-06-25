package com.scih.controller;

import com.scih.dto.AlertaDTO;
import com.scih.entity.RealAmostra;
import com.scih.repository.RealAmostraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoint REST para que a interface JavaFX consulte alertas pendentes.
 *
 * A JavaFX pode operar de duas formas complementares:
 *  1. Polling: chamar GET /api/alertas/pendentes a cada N segundos.
 *  2. Evento interno: escutar AlertaService.AlertaEvent via @EventListener
 *     (funciona quando JavaFX e Spring Boot rodam no mesmo processo).
 *
 * Trabalho futuro (seção 6 do artigo): migrar para WebSocket para push em tempo real.
 */
@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final RealAmostraRepository amostrasRepo;

    /**
     * Lista amostras com evento adverso cujo alerta ainda não foi confirmado.
     * Usado pela JavaFX para polling periódico.
     */
    @GetMapping("/pendentes")
    public ResponseEntity<List<AlertaDTO>> listarPendentes() {
        List<AlertaDTO> alertas = amostrasRepo
                .findByAlertaEmitidoFalseAndMicroorganismoIsNotNull()
                .stream()
                .map(this::toAlertaDTO)
                .toList();

        return ResponseEntity.ok(alertas);
    }

    /**
     * Confirma o isolamento do paciente — marcado pelo profissional do SCIH
     * após ver o alerta na tela (botão "Confirmar Isolamento" da Figura 5).
     */
    @PostMapping("/{idAmostra}/confirmar-isolamento")
    public ResponseEntity<Void> confirmarIsolamento(@PathVariable Long idAmostra) {
        amostrasRepo.findById(idAmostra).ifPresent(amostra -> {
            amostra.setAlertaEmitido(true);
            amostrasRepo.save(amostra);
        });
        return ResponseEntity.noContent().build();
    }

    // ----------------------------------------------------------------

    private AlertaDTO toAlertaDTO(RealAmostra amostra) {
        String patogeno = amostra.getMicroorganismo() != null
                ? amostra.getMicroorganismo().getNome() : "Desconhecido";

        String pacienteNome = amostra.getProntuario().getPaciente() != null
                ? amostra.getProntuario().getPaciente().getNome() : "-";

        String unidade = amostra.getProntuario().getDestino() != null
                ? amostra.getProntuario().getDestino().getNome() : "-";

        List<String> resistentes = amostra.getPerfisAntimicrobianos().stream()
                .filter(p -> p.getPerfil() == com.scih.entity.PerfilAntimicrobiano
                        .PerfilSensibilidade.RESISTENTE)
                .map(com.scih.entity.PerfilAntimicrobiano::getAntimicrobiano)
                .toList();

        return AlertaDTO.builder()
                .idAmostra(amostra.getId())
                .nomePaciente(pacienteNome)
                .prontuario(String.valueOf(amostra.getProntuario().getId()))
                .unidadeInternacao(unidade)
                .leito("-") // leito está em Internacao — expandir se necessário
                .patogeno(patogeno)
                .multirresistente(resistentes.size() >= 2)
                .antimicrobianosResistentes(resistentes)
                .dataHoraAlerta(java.time.LocalDateTime.now())
                .build();
    }
}
