package com.stockcomp.service.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*

@Service
class DefaultJwtService : JwtService {

    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    @Value("\${jwt.token.duration}")
    private val tokenDuration: Int = 0

    override fun generateTokenPair(username: String): String {
        val claims: HashMap<String, Any> = hashMapOf("sub" to username)

        return createAccessToken(claims)
    }

    override fun refreshTokenPair(username: String): String {
        TODO("Not yet implemented")
    }

    override fun accessTokenIsValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)

        return username == userDetails.username && !isAccessTokenExpired(token)
    }

    override fun extractUsername(token: String): String = extractClaimFromAccessToken(token, Claims::getSubject)

    override fun deleteRefreshToken(refreshToken: String): String {
        TODO("Not yet implemented")
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
            .setExpiration(Date(System.currentTimeMillis() + tokenDuration))
            .signWith(SignatureAlgorithm.HS256, secretKey).compact()
}