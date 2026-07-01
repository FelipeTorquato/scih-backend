package com.scih.controller;

import com.scih.entity.Prontuario;
import com.scih.entity.RealAmostra;
import com.scih.repository.ProntuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoint REST para o Painel de Gerenciamento (Figura 6 do artigo).
 * Fornece à interface JavaFX os dados unificados exibidos na tabela de registros:
 * ID administrativo, nome do paciente, enfermaria/leito, data de internação,
 * status da amostra, patógeno e link para antibiograma.
 */
@RestController
@RequestMapping("/api/prontuarios")
@RequiredArgsConstructor
public class ProntuarioController {

    private final ProntuarioRepository prontuarioRepo;

    /** Lista todos os prontuários para o dashboard principal. */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarTodos() {
        List<Map<String, Object>> resultado = prontuarioRepo.findAll()
                .stream()
                .map(this::toResumo)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    /** Lista prontuários com amostras pendentes (status AGUARDANDO ou NAO_COLETADA). */
    @GetMapping("/pendentes")
    public ResponseEntity<List<Map<String, Object>>> listarPendentes() {
        List<Map<String, Object>> resultado = prontuarioRepo
                .findComAmostrasPendentes()
                .stream()
                .map(this::toResumo)
                .toList();
        return ResponseEntity.ok(resultado);
    }

    /** Detalhe completo de um prontuário com antibiograma. */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        return prontuarioRepo.findById(id)
                .map(p -> ResponseEntity.ok(toDetalhe(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------------------------------

    private Map<String, Object> toResumo(Prontuario p) {
        String patogeno = p.getAmostras().stream()
                .filter(a -> a.getMicroorganismo() != null)
                .map(a -> a.getMicroorganismo().getNome())
                .findFirst().orElse("-");

        String status = p.getAmostras().stream()
                .map(a -> a.getStatus().name())
                .findFirst().orElse("NAO_COLETADA");

        return Map.of(
                "id", p.getId(),
                "paciente", p.getPaciente() != null ? p.getPaciente().getNome() : "-",
                "unidade", p.getDestino() != null ? p.getDestino().getNome() : "-",
                "dataCadastro", p.getDataCadastro() != null ? p.getDataCadastro().toString() : "-",
                "statusAmostra", status,
                "patogeno", patogeno
        );
    }

    private Map<String, Object> toDetalhe(Prontuario p) {
        List<Map<String, Object>> amostras = p.getAmostras().stream()
                .map(a -> Map.<String, Object>of(
                        "id", a.getId(),
                        "registroAmostra", a.getRegistroAmostra() != null ? a.getRegistroAmostra() : "-",
                        "descricao", a.getDescricao(),
                        "dataColeta", a.getDataColeta().toString(),
                        "status", a.getStatus().name(),
                        "microorganismo", a.getMicroorganismo() != null
                                ? a.getMicroorganismo().getNome() : "-",
                        "antibiograma", a.getPerfisAntimicrobianos().stream()
                                .map(perf -> Map.of(
                                        "antimicrobiano", perf.getAntimicrobiano(),
                                        "perfil", perf.getPerfil().name()))
                                .toList()
                )).toList();

        return Map.of(
                "id", p.getId(),
                "paciente", p.getPaciente() != null ? p.getPaciente().getNome() : "-",
                "unidade", p.getDestino() != null ? p.getDestino().getNome() : "-",
                "dataCadastro", p.getDataCadastro() != null ? p.getDataCadastro().toString() : "-",
                "amostras", amostras
        );
    }
}
