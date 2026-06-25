package com.scih.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO com os dados administrativos e de localização do paciente.
 * Retornado pelo IntegraSUS (ou mock) a partir do número do prontuário.
 * Campos baseados na seção 4.2 do artigo: "setor de internação, nome,
 * data de cadastro do paciente, número do pedido da amostra e descrição do exame".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacienteDTO {

    @NotBlank(message = "ID do prontuário é obrigatório")
    @JsonProperty("prontuario_id")
    private String prontuarioId;

    @NotBlank(message = "Nome do paciente é obrigatório")
    private String nome;

    @JsonProperty("data_nascimento")
    private LocalDate dataNascimento;

    @NotBlank(message = "Unidade de internação é obrigatória")
    @JsonProperty("unidade_internacao")
    private String unidadeInternacao;

    @NotBlank(message = "Leito é obrigatório")
    private String leito;

    @NotNull(message = "Data de internação é obrigatória")
    @JsonProperty("data_internacao")
    private LocalDate dataInternacao;
}
