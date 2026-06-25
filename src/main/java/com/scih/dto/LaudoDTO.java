package com.scih.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * DTO de transferência do laudo microbiológico.
 * <p>
 * {
 * "registro_amostra": "2026-98745A",
 * "paciente": { "prontuario": "8472910" },
 * "microbiologia": {
 * "resultado_cultura": "Positivo",
 * "patogeno": "Klebsiella pneumoniae",
 * "antibiograma": [
 * { "antimicrobiano": "Meropenem", "perfil": "Resistente" }
 * ]
 * }
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaudoDTO {

    @NotBlank(message = "ID do prontuário é obrigatório")
    @JsonProperty("prontuario_id")
    private String prontuarioId;

    @NotBlank(message = "Nome do paciente é obrigatório")
    @JsonProperty("paciente_nome")
    private String pacienteNome;

    @NotNull(message = "Data de coleta é obrigatória")
    @JsonProperty("data_coleta")
    private LocalDate dataColeta;

    @NotBlank(message = "Microorganismo é obrigatório")
    private String microorganismo;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotEmpty(message = "Perfil de resistência não pode ser vazio")
    @JsonProperty("perfil_resistencia")
    private Map<String, String> perfilResistencia;

    // ----------------------------------------------------------------
    // Métodos auxiliares de negócio

    /**
     * Detecta multirresistência: considera multirresistente se o laudo possui
     * 2 ou mais antimicrobianos com perfil RESISTENTE.
     */
    public boolean isMultirresistente() {
        if (perfilResistencia == null) return false;
        long resistentes = perfilResistencia.values().stream()
                .filter(perfil -> "RESISTENTE".equalsIgnoreCase(perfil))
                .count();
        return resistentes >= 2;
    }

    /**
     * Como o mock scih-nuvem não retorna um 'registro_amostra',
     * geramos um identificador provisório combinando o prontuário e a data
     * para manter a lógica de deduplicação no IntegracaoService.
     */
    public String getRegistroAmostra() {
        return prontuarioId + "-" + dataColeta.toString();
    }
}
