package com.freelance.agency.repository;

import com.freelance.agency.entity.Lead;
import com.freelance.agency.enums.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    Optional<Lead> findByEmail(String email);

    List<Lead> findByStatus(LeadStatus status);

    boolean existsByEmail(String email);
}