package api.dev.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import api.dev.user.model.JwtToken;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken,Integer>{
    Optional<JwtToken> findByToken(String token);

    @Query(value = "select jwtToken from JwtToken jwtToken inner join User user on jwtToken.user.userId = user.userId where user.userId = :userid and (jwtToken.is_logged_out = false)")
    List<JwtToken> findAllValidTokenByUser(Integer userid);
}
