package com.scih.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa a clínica de saída do paciente.
 * Corresponde à entidade "clinica_saida" do diagrama de classes (Figura 3).
 */
@Entity
@Table(name = "clinica_saida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicaSaida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;
}
