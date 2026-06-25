package com.scih.repository;

import com.scih.entity.Prontuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProntuarioRepository extends JpaRepository<Prontuario, Long> {

    /**
     * Busca o prontuário pelo ID do paciente vinculado.
     * Usado como chave de integração: IntegraSUS retorna o prontuarioId
     * que é então cruzado com este repositório.
     */
    @Query("SELECT p FROM Prontuario p JOIN p.paciente pa WHERE pa.id = :pacienteId")
    Optional<Prontuario> findByPacienteId(Long pacienteId);

    /** Lista todos os prontuários com amostras pendentes (para dashboard). */
    @Query("SELECT DISTINCT p FROM Prontuario p JOIN p.amostras a " +
           "WHERE a.status = 'AGUARDANDO' OR a.status = 'NAO_COLETADA'")
    List<Prontuario> findComAmostrasPendentes();
}
