package oauth.core.util;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth.core.model.oauth.OauthAttribute;
import oauth.core.properties.JwtProperties;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

	private final RedisTemplate<String, String> redisTemplate;
	private final JwtProperties jwtProperties;
	// 비대칭키 RS256 사용
	private KeyPair keyPair = Jwts.SIG.RS256.keyPair().build();
	private PrivateKey privateKey = keyPair.getPrivate();
	private PublicKey publicKey = keyPair.getPublic();

	// 일반 JWT 토큰 생성
	public String generateToken(Authentication authentication, String id) {
		return Jwts.builder().subject(authentication.getName())
								.claim("authorities",authentication.getAuthorities()
																	.stream()
																	.map(GrantedAuthority::getAuthority)
																	.collect(Collectors.joining(",")))
				.id(id)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccesstokenTime().toMillis()))
				.signWith(privateKey)
				.compact();
	}

	// oauth2 JWT 토큰 생성
	public String generateOauth2Token(Authentication authentication, String id) {
		OauthAttribute oauthAttribute = new OauthAttribute((OAuth2AuthenticationToken) authentication);

		return Jwts.builder().subject(oauthAttribute.getEmail())
								.claim("authorities", "ROLE_ANONYMOUS")
								.id(id)
								.issuedAt(new Date(System.currentTimeMillis()))
								.expiration(new Date(System.currentTimeMillis() + jwtProperties.getAccesstokenTime().toMillis()))
								.signWith(privateKey)
								.compact();
	}

	// Refresh Token 생성
	public void refreshToken(String id) {

		String refreshToken = Jwts.builder().id(id)
											.issuedAt(new Date())
											.expiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshtokenTime().toMillis()))
											.signWith(privateKey)
											.compact();

		// Redis에 Refresh Token 저장 (key는 id:{id}:refresh, value는 refreshToken)
		redisTemplate.opsForValue().set("id:" + id + ":refresh", refreshToken, 7, TimeUnit.DAYS);
	}

	// Refresh Token 검증
	public String getRefreshToken(String accessToken) {
		try {
			Claims claims = getToken(accessToken);
			String id = claims.getId();
			String redisToken = redisTemplate.opsForValue().get("id:" + id + ":refresh");

			// Redis에 저장된 Refresh Token과 비교하여 유효성 확인
			return redisToken;
		} catch (JwtException e) {
			log.error("refreshTokenValid JwtException ::: {}", e.getMessage());
			return null;
		}
	}
	
	// Refresh Token 만료
	public void expireRefreshToken(String accessToken) {
		try {
			Claims claims = getToken(accessToken);
			String id = claims.getId();
			// Redis에 저장된 Refresh Token 제거
			redisTemplate.delete("id:" + id + ":refresh");
		} catch (JwtException e) {
			log.error("expireRefreshToken JwtException ::: {}", e.getMessage());
		}
	}

	// Refresh Token 검증
	public String getRefreshTokenId(String refreshToken) {
		try {
			Claims claims = getToken(refreshToken);
			return claims.getId();
		} catch (JwtException e) {
			log.error("getRefreshTokenId JwtException ::: {}", e.getMessage());
			return null;
		}
	}

	// JWT 토큰 인증 정보 확인
	public Authentication getAuthentication(String accessToken) {

		Claims claims = getToken(accessToken);
		Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("authorities")
																					.toString()
																					.split(","))
																	.map(SimpleGrantedAuthority::new)
																	.toList();
		UserDetails principal = new User(claims.getSubject(), "", authorities);

		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	// JWT 토큰 확인
	private Claims getToken(String token) {
		try {
            return Jwts.parser()
                       .verifyWith(publicKey)
                       .build()
                       .parseSignedClaims(token)
                       .getPayload();
        } catch (ExpiredJwtException e) {
        	log.error("getToken ExpiredJwtException ::: {}", e.getMessage());
            return e.getClaims();
        }
	}

	// JWT 토큰 검증
	public boolean validateToken(String token) {
		try {
			Jwts.parser()
	            .verifyWith(publicKey)
	            .build()
	            .parseSignedClaims(token)
	            .getPayload();
			return true;
		} catch (ExpiredJwtException e) {
			log.error("validateToken ExpiredJwtException ::: {}", e.getMessage());
            return false;
		} catch (JwtException e) {
			log.error("validateToken JwtException ::: {}", e.getMessage());
			return false;
		}
	}
}
