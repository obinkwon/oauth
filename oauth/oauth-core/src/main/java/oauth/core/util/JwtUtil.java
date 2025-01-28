package oauth.core.util;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import oauth.core.model.OauthAttribute;

@Slf4j
@Component
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

	// 일반 JWT 토큰 생성
	public String generateToken(Authentication authentication) {
		return Jwts.builder()
	    		.subject(authentication.getName())
	    		.claim("authorities",authentication.getAuthorities().stream()
							                        .map(GrantedAuthority::getAuthority)
							                        .collect(Collectors.joining(",")))
	    		.claim("user",authentication.getCredentials())
	    		.issuedAt(new Date(System.currentTimeMillis()))
	    		.expiration(new Date(System.currentTimeMillis() + expirationTimeMs))
	    		.signWith(privateKey)
	    		.compact();
	}
	
	// oauth2 JWT 토큰 생성
	public String generateOauth2Token(OauthAttribute oauthAttribute) {
		
		return Jwts.builder()
	    		.subject(oauthAttribute.getEmail())
	    		.claim("authorities","ROLE_ANONYMOUS")
	    		.claim("attributes",oauthAttribute.getAttributes())
	    		.issuedAt(new Date(System.currentTimeMillis()))
	    		.expiration(new Date(System.currentTimeMillis() + expirationTimeMs))
	    		.signWith(privateKey)
	    		.compact();
	}
	
	// JWT 토큰 인증 정보 확인
	public Authentication getAuthentication(String accessToken) {
		
		Jws<Claims> token = getToken(accessToken);
		Claims claims = token.getPayload();
		Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("authorities").toString().split(","))
                													.map(SimpleGrantedAuthority::new)
                													.toList();
		UserDetails principal = new User(claims.getSubject(), "", authorities);
		
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}
	
	// JWT 토큰 확인
    private Jws<Claims> getToken(String token) {
        return Jwts.parser()
					.verifyWith(publicKey)
					.build()
					.parseSignedClaims(token);
    }
    
	// JWT 토큰 검증
	public boolean validateToken(String token) {
		try {
			getToken(token);
			return true;
		} catch (JwtException e) {
			log.error("JwtException ::: {}", e.getMessage());
			return false;
		}
	}
}


