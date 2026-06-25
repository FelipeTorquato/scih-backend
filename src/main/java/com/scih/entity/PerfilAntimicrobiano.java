package com.scih.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa uma entrada do antibiograma de uma amostra.
 * Cada registro corresponde a um antimicrobiano testado e seu perfil de sensibilidade.
 * Derivado do payload JSON do Quadro 1 do artigo:
 *   { "antimicrobiano": "Meropenem", "perfil": "Resistente" }
 */
@Entity
@Table(name = "perfil_antimicrobiano")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerfilAntimicrobiano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amostra_id", nullable = false)
    private RealAmostra amostra;

    /** Ex: "Meropenem", "Polimixina B", "Amikacina" */
    @Column(nullable = false)
    private String antimicrobiano;

    /** SENSIVEL, INTERMEDIARIO, RESISTENTE */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilSensibilidade perfil;

    public enum PerfilSensibilidade {
        SENSIVEL,
        INTERMEDIARIO,
        RESISTENTE
    }
}
