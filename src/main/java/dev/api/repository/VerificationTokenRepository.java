package dev.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.api.model.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Integer> {
    VerificationToken findByToken(String token);
}