package com.scih.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

/**
 * DTO de transferência do laudo microbiológico.
 *
 * {
 *   "registro_amostra": "2026-98745A",
 *   "paciente": { "prontuario": "8472910" },
 *   "microbiologia": {
 *     "resultado_cultura": "Positivo",
 *     "patogeno": "Klebsiella pneumoniae",
 *     "antibiograma": [
 *       { "antimicrobiano": "Meropenem", "perfil": "Resistente" }
 *     ]
 *   }
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaudoDTO {

    @NotBlank(message = "Registro da amostra é obrigatório")
    @JsonProperty("registro_amostra")
    private String registroAmostra;

    @NotNull(message = "Dados do paciente são obrigatórios")
    @Valid
    private PacienteRef paciente;

    @NotNull(message = "Dados microbiológicos são obrigatórios")
    @Valid
    private Microbiologia microbiologia;

    // ----------------------------------------------------------------

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PacienteRef {
        @NotBlank(message = "Número do prontuário é obrigatório")
        private String prontuario;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Microbiologia {
        @NotBlank(message = "Resultado da cultura é obrigatório")
        @JsonProperty("resultado_cultura")
        private String resultadoCultura;

        @NotBlank(message = "Patógeno é obrigatório")
        private String patogeno;

        @NotEmpty(message = "Antibiograma não pode ser vazio")
        @Valid
        private List<EntradaAntibiograma> antibiograma;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntradaAntibiograma {
        @NotBlank(message = "Nome do antimicrobiano é obrigatório")
        private String antimicrobiano;

        @NotBlank(message = "Perfil de sensibilidade é obrigatório")
        private String perfil; // "Sensível", "Intermediário", "Resistente"
    }

    // ----------------------------------------------------------------
    // Métodos auxiliares de negócio

    /**
     * Retorna o número do prontuário diretamente, evitando navegação encadeada
     * em serviços que precisam apenas dessa chave.
     */
    public String getProntuarioId() {
        return paciente != null ? paciente.getProntuario() : null;
    }

    /**
     * Detecta multirresistência: considera multirresistente se o laudo possui
     * 2 ou mais antimicrobianos com perfil RESISTENTE.
     * Critério simplificado — pode ser refinado conforme protocolo do SCIH.
     */
    public boolean isMultirresistente() {
        if (microbiologia == null || microbiologia.getAntibiograma() == null) return false;
        long resistentes = microbiologia.getAntibiograma().stream()
                .filter(e -> "Resistente".equalsIgnoreCase(e.getPerfil()))
                .count();
        return resistentes >= 2;
    }
}
