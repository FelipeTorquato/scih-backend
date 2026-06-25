package com.scih.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa o microrganismo identificado no laudo microbiológico.
 * Corresponde à entidade "microorganismo" do diagrama de classes (Figura 3).
 * Origem: Sistema R.E.A.L (via extração LLM).
 */
@Entity
@Table(name = "microorganismo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Microorganismo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ex: "Klebsiella pneumoniae", "Acinetobacter baumannii", "MRSA" */
    @Column(nullable = false)
    private String nome;

    /** Informações adicionais sobre o patógeno */
    @Column(columnDefinition = "TEXT")
    private String descricao;
}
