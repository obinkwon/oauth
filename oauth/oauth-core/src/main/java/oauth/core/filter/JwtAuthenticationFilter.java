package oauth.core.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import oauth.core.properties.JwtProperties;
import oauth.core.util.CookieUtil;
import oauth.core.util.JwtUtil;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	@Autowired
	private JwtProperties jwtProperties;
	
	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String token = resolveToken(request, "token");
		
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
					CookieUtil.generateCookie(response, "token", accessToken, (int) jwtProperties.getRefreshtokenTime().toMinutes());
					
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}

		chain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request, String tokenName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (tokenName.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}