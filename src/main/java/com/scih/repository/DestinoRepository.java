package com.scih.repository;

import com.scih.entity.Destino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DestinoRepository extends JpaRepository<Destino, Long> {
    Optional<Destino> findByNome(String nome);
}
