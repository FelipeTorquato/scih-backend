package com.scih.service;

import com.scih.dto.LaudoDTO;
import com.scih.dto.PacienteDTO;
import com.scih.entity.*;
import com.scih.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Camada de integração — articulador central do sistema.
 *
 * Responsabilidades (seção 3.5, Camada de Integração do artigo):
 *  1. Receber o laudo processado pelo LLM (LaudoDTO) e os dados do paciente (PacienteDTO).
 *  2. Usar o número do prontuário como chave única para vincular as duas fontes.
 *  3. Persistir o registro unificado no banco relacional via JPA/Hibernate.
 *  4. Acionar o AlertaService se for detectado evento adverso (multirresistência).
 *
 * Fluxo corresponde ao Diagrama de Sequência (Figura 4 do artigo):
 *  Agendador → Extração → LLM → [este serviço] → IntegraSUS → Persistência → Alerta → JavaFX
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IntegracaoService {

    private final PacienteRepository pacienteRepo;
    private final ProntuarioRepository prontuarioRepo;
    private final RealAmostraRepository amostrasRepo;
    private final MicroorganismoRepository microorganismoRepo;
    private final DestinoRepository destinoRepo;
    private final AlertaService alertaService;

    /**
     * Ponto de entrada principal.
     * Recebe laudo + dados do paciente e executa a unificação completa.
     */
    @Transactional
    public void unificarEPersistir(LaudoDTO laudo, PacienteDTO pacienteDTO) {
        log.info("[Integração] Iniciando unificação — prontuário: {}", laudo.getProntuarioId());

        // Evitar reprocessamento de amostra já registrada
        if (amostrasRepo.findByRegistroAmostra(laudo.getRegistroAmostra()).isPresent()) {
            log.info("[Integração] Amostra {} já processada. Ignorando.", laudo.getRegistroAmostra());
            return;
        }

        // 1. Garante que o paciente existe no banco
        Paciente paciente = resolverPaciente(pacienteDTO);

        // 2. Garante que o prontuário existe e está atualizado
        Prontuario prontuario = resolverProntuario(paciente, pacienteDTO);

        // 3. Resolve o microrganismo (cria se não existir)
        Microorganismo microorganismo = resolverMicroorganismo(
                laudo.getMicrobiologia().getPatogeno());

        // 4. Cria a amostra laboratorial unificada
        RealAmostra amostra = RealAmostra.builder()
                .prontuario(prontuario)
                .registroAmostra(laudo.getRegistroAmostra())
                .descricao(laudo.getMicrobiologia().getResultadoCultura())
                .dataColeta(java.time.LocalDate.now())
                .microorganismo(microorganismo)
                .status(RealAmostra.StatusAmostra.CONCLUIDO)
                .alertaEmitido(false)
                .build();

        // 5. Mapeia o antibiograma completo
        List<PerfilAntimicrobiano> perfis = laudo.getMicrobiologia()
                .getAntibiograma().stream()
                .map(entrada -> PerfilAntimicrobiano.builder()
                        .amostra(amostra)
                        .antimicrobiano(entrada.getAntimicrobiano())
                        .perfil(mapearPerfil(entrada.getPerfil()))
                        .build())
                .toList();

        amostra.getPerfisAntimicrobianos().addAll(perfis);
        prontuario.getAmostras().add(amostra);
        amostrasRepo.save(amostra);

        log.info("[Integração] Amostra {} persistida para prontuário {}",
                laudo.getRegistroAmostra(), laudo.getProntuarioId());

        // 6. Verifica evento adverso e dispara alerta
        if (laudo.isMultirresistente()) {
            log.warn("[Integração] EVENTO ADVERSO detectado — patógeno: {} | paciente: {} | leito: {}",
                    laudo.getMicrobiologia().getPatogeno(),
                    pacienteDTO.getNome(),
                    pacienteDTO.getLeito());
            alertaService.notificar(amostra, pacienteDTO, laudo);
        }
    }

    // ----------------------------------------------------------------
    // Métodos auxiliares privados

    private Paciente resolverPaciente(PacienteDTO dto) {
        return pacienteRepo.findByNome(dto.getNome())
                .orElseGet(() -> {
                    Paciente novo = Paciente.builder()
                            .nome(dto.getNome())
                            .dataNascimento(dto.getDataNascimento())
                            .build();
                    return pacienteRepo.save(novo);
                });
    }

    private Prontuario resolverProntuario(Paciente paciente, PacienteDTO dto) {
        return prontuarioRepo.findByPacienteId(paciente.getId())
                .orElseGet(() -> {
                    Destino destino = destinoRepo.findByNome(dto.getUnidadeInternacao())
                            .orElseGet(() -> destinoRepo.save(
                                    Destino.builder()
                                            .nome(dto.getUnidadeInternacao())
                                            .descricao("Unidade importada do IntegraSUS")
                                            .build()));

                    Prontuario novo = Prontuario.builder()
                            .paciente(paciente)
                            .dataCadastro(dto.getDataInternacao())
                            .destino(destino)
                            .build();
                    return prontuarioRepo.save(novo);
                });
    }

    private Microorganismo resolverMicroorganismo(String nome) {
        return microorganismoRepo.findByNomeIgnoreCase(nome)
                .orElseGet(() -> microorganismoRepo.save(
                        Microorganismo.builder().nome(nome).build()));
    }

    private PerfilAntimicrobiano.PerfilSensibilidade mapearPerfil(String perfil) {
        return switch (perfil.trim().toUpperCase()
                .replace("Á", "A").replace("Ê", "E").replace("Ó", "O")) {
            case "RESISTENTE"    -> PerfilAntimicrobiano.PerfilSensibilidade.RESISTENTE;
            case "INTERMEDIARIO",
                 "INTERMEDIÁRIO" -> PerfilAntimicrobiano.PerfilSensibilidade.INTERMEDIARIO;
            default              -> PerfilAntimicrobiano.PerfilSensibilidade.SENSIVEL;
        };
    }
}
