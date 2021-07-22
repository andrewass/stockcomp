package com.stockcomp.repository;

import com.stockcomp.domain.user.RefreshToken;
import com.stockcomp.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    RefreshToken findRefreshTokenByUser(User user);

    RefreshToken findRefreshTokenByToken(String token);

    void deleteRefreshTokenByUser(User user);
}
