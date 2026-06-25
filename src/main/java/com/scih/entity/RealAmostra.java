package com.scih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a amostra microbiológica proveniente do sistema R.E.A.L.
 * Corresponde à entidade "real_amostra" do diagrama de classes (Figura 3).
 * É vinculada ao Prontuario via prontuario_id, fechando o ciclo de integração.
 */
@Entity
@Table(name = "real_amostra")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealAmostra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prontuario_id", nullable = false)
    private Prontuario prontuario;

    /** Ex: "Cultura de urina", "Hemocultura", "Cultura de secreção" */
    @Column(nullable = false)
    private String descricao;

    @Column(name = "data_coleta", nullable = false)
    private LocalDate dataColeta;

    /** Registro de amostra no sistema R.E.A.L. Ex: "2026-98745A" */
    @Column(name = "registro_amostra")
    private String registroAmostra;

    /** Status do processamento da amostra */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusAmostra status = StatusAmostra.AGUARDANDO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "microorganismo_id")
    private Microorganismo microorganismo;

    /** Perfis de sensibilidade/resistência extraídos do antibiograma */
    @OneToMany(mappedBy = "amostra", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PerfilAntimicrobiano> perfisAntimicrobianos = new ArrayList<>();

    /** Indica se já foi emitido alerta para este evento adverso */
    @Column(name = "alerta_emitido", nullable = false)
    @Builder.Default
    private Boolean alertaEmitido = false;

    public enum StatusAmostra {
        NAO_COLETADA,
        AGUARDANDO,
        CONCLUIDO
    }
}
