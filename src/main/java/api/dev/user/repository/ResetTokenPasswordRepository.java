package api.dev.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import api.dev.user.model.ResetTokenPassword;


 
@Repository
public interface ResetTokenPasswordRepository extends JpaRepository<ResetTokenPassword,Integer> {
    Optional<ResetTokenPassword> findByToken(String token);
}
