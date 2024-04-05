package dev.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.api.model.ResetTokenPassword;

 
@Repository
public interface ResetTokenPasswordRepository extends JpaRepository<ResetTokenPassword,Integer> {
    ResetTokenPassword findByToken(String token);
}
