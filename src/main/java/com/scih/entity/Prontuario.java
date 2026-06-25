package com.scih.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade central do sistema — representa o prontuário unificado do paciente.
 * É a chave de ligação entre IntegraSUS (dados administrativos) e R.E.A.L (dados laboratoriais).
 * Corresponde à entidade "prontuario" do diagrama de classes (Figura 3).
 *
 * Campos administrativos: data_cadastro, hora_chegada, data_alta, dias_hospital,
 *                         dias_clinica, destino_id, clinica_saida_id.
 * Chave de integração:    paciente_id (número do prontuário como chave única).
 */
@Entity
@Table(name = "prontuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prontuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Chave de integração entre os sistemas.
     * Número do prontuário extraído do laudo R.E.A.L e usado para buscar o paciente no IntegraSUS.
     * Deve ser único e imutável.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    // ---- Dados vindos do IntegraSUS ----

    @Column(name = "data_cadastro")
    private LocalDate dataCadastro;

    @Column(name = "hora_chegada")
    private LocalTime horaChegada;

    @Column(name = "data_alta")
    private LocalDate dataAlta;

    @Column(name = "dias_hospital")
    private Integer diasHospital;

    @Column(name = "dias_clinica")
    private Integer diasClinica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destino_id")
    private Destino destino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinica_saida_id")
    private ClinicaSaida clinicaSaida;

    // ---- Amostras laboratoriais vinculadas (R.E.A.L) ----

    @OneToMany(mappedBy = "prontuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RealAmostra> amostras = new ArrayList<>();
}
