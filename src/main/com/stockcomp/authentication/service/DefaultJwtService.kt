package com.stockcomp.authentication.service

import com.stockcomp.authentication.entity.RefreshToken
import com.stockcomp.user.entity.User
import com.stockcomp.exception.TokenRefreshException
import com.stockcomp.authentication.repository.RefreshTokenRepository
import com.stockcomp.user.repository.UserRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Stream

@Service
@Transactional
class DefaultJwtService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) : JwtService {

    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    @Value("\${access.token.duration}")
    private val accessTokenDuration: Int = 0

    @Value("\${refresh.token.duration}")
    private val refreshTokenDuration: Long = 0

    override fun refreshTokenPair(refreshToken: String): Pair<String, String> {
        val currentRefreshToken = refreshTokenRepository.findByToken(refreshToken)
        if (currentRefreshTokenIsValid(currentRefreshToken)) {
            return generateTokenPair(currentRefreshToken.user.username)
        } else {
            throw TokenRefreshException("Expired or non-existing refresh token")
        }
    }

    override fun generateTokenPair(username: String): Pair<String, String> {
        val user = userRepository.findByUsername(username)
        deleteRefreshToken(user)
        val claims: HashMap<String, Any> = hashMapOf("sub" to username)
        val accessToken = createAccessToken(claims)
        val refreshToken = createRefreshToken(user)

        return Pair(accessToken, refreshToken)
    }


    override fun accessTokenIsValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)

        return username == userDetails.username && !isAccessTokenExpired(token)
    }

    override fun extractUsername(token: String): String = extractClaimFromAccessToken(token, Claims::getSubject)

    override fun deleteRefreshToken(user: User) {
        refreshTokenRepository.deleteRefreshTokenByUser(user)
    }

    private fun isAccessTokenExpired(token: String) = extractExpiration(token).before(Date())

    private fun extractExpiration(token: String) = extractClaimFromAccessToken(token, Claims::getExpiration)

    private fun <T> extractClaimFromAccessToken(token: String, lambda: (Claims) -> T): T {
        val claims = getAllClaims(token)

        return lambda(claims)
    }

    private fun getAllClaims(token: String) = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body

    private fun createAccessToken(claims: HashMap<String, Any>): String =
        Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + accessTokenDuration))
            .signWith(SignatureAlgorithm.HS256, secretKey).compact()

    private fun createRefreshToken(user: User): String {
        val refreshToken = RefreshToken(
            token = UUID.randomUUID().toString(),
            user = user,
            expirationTime = LocalDateTime.now().plusDays(refreshTokenDuration)
        )
        refreshTokenRepository.save(refreshToken)

        return refreshToken.token
    }

    private fun currentRefreshTokenIsValid(refreshToken: RefreshToken): Boolean {
        return Stream.ofNullable(refreshToken)
            .anyMatch { it.expirationTime.isAfter(LocalDateTime.now()) }
    }
}