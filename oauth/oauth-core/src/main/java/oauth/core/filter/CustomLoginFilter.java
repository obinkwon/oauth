package oauth.core.filter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import oauth.core.util.CookieUtil;
import oauth.core.util.JwtUtil;

public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	@Autowired
	private JwtUtil jwtUtil;
	
	public CustomLoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/login"); // 로그인 처리 URL
    }
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String email = request.getParameter("email");
        String password = obtainPassword(request);
		
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);

		return authenticationManager.authenticate(authRequest);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws JsonProcessingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		
		String id = UUID.randomUUID().toString();
		String token = jwtUtil.generateToken(authentication, id);
		// Refresh Token 생성
		jwtUtil.getRefreshToken(id);

        CookieUtil.generateCookie(response, "token", token, (int) 1000 * 60 * 60 / 1000);
        
		response.setContentType("application/json");
		response.addHeader("Authorization", "Bearer " + token);
		response.getWriter().write(objectMapper
				.writeValueAsString(Map.of(
						"message", "Authentication successful",
						"token", token
				)));
		response.sendRedirect("/web/main");
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws JsonProcessingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write(objectMapper
				.writeValueAsString(Map.of(
						"message", 
						"Authentication failed", 
						"error", 
						failed.getLocalizedMessage()
				)));
	}
}
