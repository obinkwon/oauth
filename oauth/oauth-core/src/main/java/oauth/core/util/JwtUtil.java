package oauth.core.util;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

public class JwtUtil {

	private final PrivateKey privateKey;
	private final PublicKey publicKey;
	private final long expirationTimeMs = 1000 * 60 * 60; // 1시간 (ms)

	public JwtUtil() {
		// 비대칭키 RS256 사용
		KeyPair keyPair = Jwts.SIG.RS256.keyPair().build();
		this.privateKey = keyPair.getPrivate();
		this.publicKey = keyPair.getPublic();
	}

	// JWT 토큰 생성
	public String generateToken(Authentication authentication) {
		return Jwts.builder()
	    		.subject(authentication.getName())
	    		.claim("user",authentication.getDetails())
	    		.issuedAt(new Date(System.currentTimeMillis()))
	    		.expiration(new Date(System.currentTimeMillis() + expirationTimeMs))
	    		.signWith(privateKey)
	    		.compact();
	}
	
	// oauth2 토큰 생성
	public String generateOauth2Token(OAuth2User oauth2User) {
		return Jwts.builder()
	    		.subject(oauth2User.getAttribute("email"))
	    		.claim("user",oauth2User.getAttributes())
	    		.issuedAt(new Date(System.currentTimeMillis()))
	    		.expiration(new Date(System.currentTimeMillis() + expirationTimeMs))
	    		.signWith(privateKey)
	    		.compact();
	}
    
	// JWT 토큰 검증
	public String validateToken(String token) {
		 
		try {
			Jws<Claims> jws = Jwts.parser()
								.verifyWith(publicKey)
								.build()
								.parseSignedClaims(token);
			return jws.getPayload().getSubject();
		} catch (JwtException e) {
			throw new IllegalArgumentException("Invalid JWT token", e);
		}
	}
}


