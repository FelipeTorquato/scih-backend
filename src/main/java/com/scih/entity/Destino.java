package com.scih.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa a unidade de internação e o leito do paciente.
 * Corresponde à entidade "destino" do diagrama de classes (Figura 3).
 * Origem: IntegraSUS.
 */
@Entity
@Table(name = "destino")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Destino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ex: "UTI Adulto", "Clínica Médica", "Cirurgia Geral" */
    @Column(nullable = false)
    private String nome;

    /** Descrição complementar da unidade */
    private String descricao;
}
