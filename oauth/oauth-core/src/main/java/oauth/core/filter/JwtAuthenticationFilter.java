package oauth.core.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import oauth.core.properties.JwtProperties;
import oauth.core.util.CookieUtil;
import oauth.core.util.JwtUtil;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final JwtProperties jwtProperties;
	
	public JwtAuthenticationFilter(JwtUtil jwtUtil, JwtProperties jwtProperties) {
		this.jwtUtil = jwtUtil;
		this.jwtProperties = jwtProperties;
	}

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String token = CookieUtil.getCookieToken(request);
		
		if(token != null) {
			if (jwtUtil.validateToken(token)) {
				Authentication authentication = jwtUtil.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				String refreshToken = jwtUtil.getRefreshToken(token);
				
				if(refreshToken != null) {
					String id = jwtUtil.getRefreshTokenId(refreshToken);
					Authentication authentication = jwtUtil.getAuthentication(token);
					String accessToken = jwtUtil.generateToken(authentication, id);
					CookieUtil.generateCookie(response, accessToken, (int) jwtProperties.getRefreshtokenTime().toMinutes());
					
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}

		chain.doFilter(request, response);
	}
}