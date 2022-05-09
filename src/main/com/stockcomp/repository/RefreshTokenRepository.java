package com.stockcomp.repository;

import com.stockcomp.domain.user.RefreshToken;
import com.stockcomp.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    RefreshToken findByToken(String token);

    void deleteRefreshTokenByUser(User user);
}
