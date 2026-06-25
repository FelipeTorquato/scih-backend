package com.scih.repository;

import com.scih.entity.Microorganismo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MicroorganismoRepository extends JpaRepository<Microorganismo, Long> {
    Optional<Microorganismo> findByNomeIgnoreCase(String nome);
}
