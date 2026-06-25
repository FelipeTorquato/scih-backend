package com.scih.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Representa o vínculo de internação entre um prontuário e uma unidade/leito.
 * Corresponde à entidade "internacao" do diagrama de classes (Figura 3).
 * Armazena o leito atual do paciente para que o alerta direcione corretamente.
 */
@Entity
@Table(name = "internacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Internacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Número ou código do leito. Ex: "UTI-07", "CM-14" */
    @Column(nullable = false)
    private String leito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private Prontuario prontuario;
}
