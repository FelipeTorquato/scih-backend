package com.scih.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de alerta enviado à interface JavaFX quando um evento adverso é detectado.
 * Corresponde à Figura 5 do artigo: exibe paciente, prontuário, localização
 * e patógeno identificado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaDTO {

    private Long idAmostra;
    private String nomePaciente;
    private String prontuario;
    private String unidadeInternacao;
    private String leito;
    private String patogeno;
    private boolean multirresistente;
    private List<String> antimicrobianosResistentes;
    private LocalDateTime dataHoraAlerta;

    /** Descrição resumida para exibição no painel */
    public String getResumo() {
        return String.format("[%s] %s — %s | %s, %s",
                dataHoraAlerta, nomePaciente, patogeno, unidadeInternacao, leito);
    }
}
