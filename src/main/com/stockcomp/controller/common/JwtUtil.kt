package com.stockcomp.controller.common

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*


@Component
class JwtUtil {

    @Value("\${jwt.secret}")
    private lateinit var secretKey : String

    @Value("\${jwt.token.duration}")
    private val tokenDuration : Int = 0

    fun generateToken(username: String): String {
        val claims: HashMap<String, Any> = hashMapOf("sub" to username)

        return createToken(claims)
    }

    fun tokenIsValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)

        return username == userDetails.username && !isTokenExpired(token)
    }

    fun extractUsername(token: String): String = extractClaimFromToken(token, Claims::getSubject)

    private fun isTokenExpired(token: String) = extractExpiration(token).before(Date())

    private fun extractExpiration(token: String) = extractClaimFromToken(token, Claims::getExpiration)

    private fun <T> extractClaimFromToken(token: String, lambda: (Claims) -> T): T {
        val claims = getAllClaims(token)

        return lambda(claims)
    }

    private fun getAllClaims(token: String) = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body

    private fun createToken(claims: HashMap<String, Any>): String =
        Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + tokenDuration))
            .signWith(SignatureAlgorithm.HS256, secretKey).compact()
}