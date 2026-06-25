package com.scih.repository;

import com.scih.entity.RealAmostra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RealAmostraRepository extends JpaRepository<RealAmostra, Long> {

    Optional<RealAmostra> findByRegistroAmostra(String registroAmostra);

    /** Lista amostras com evento adverso ainda sem alerta emitido. */
    List<RealAmostra> findByAlertaEmitidoFalseAndMicroorganismoIsNotNull();

    /** Lista todas as amostras de um prontuário. */
    List<RealAmostra> findByProntuarioId(Long prontuarioId);
}
