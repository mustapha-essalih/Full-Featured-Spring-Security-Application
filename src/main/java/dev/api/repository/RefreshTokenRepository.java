package dev.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.api.model.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken , Integer>
{
    Optional<RefreshToken> findByToken(String token);
}



