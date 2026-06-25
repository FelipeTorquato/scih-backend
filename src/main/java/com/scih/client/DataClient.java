package com.scih.client;

import com.scih.dto.LaudoDTO;
import com.scih.dto.PacienteDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrato único de acesso a dados externos.
 * Em profile "dev": implementado por MockDataClient (consome o mock service em nuvem).
 * Em profile "prod": implementado por RealDataClient (aciona AIScraper + sistemas reais).
 *
 * O ColetaScheduler e o IntegracaoService dependem apenas desta interface,
 * garantindo que a troca de profile não exija nenhuma alteração de lógica.
 */
public interface DataClient {

    /**
     * Retorna os laudos microbiológicos positivos coletados na data informada.
     * @param data Data de coleta a ser consultada.
     * @return Lista de laudos no formato padronizado (Quadro 1 do artigo).
     */
    List<LaudoDTO> buscarLaudos(LocalDate data);

    /**
     * Retorna os dados administrativos e de localização do paciente.
     * @param prontuarioId Número do prontuário — chave de integração única.
     * @return DTO com unidade de internação, leito e dados cadastrais.
     */
    PacienteDTO buscarPaciente(String prontuarioId);
}
