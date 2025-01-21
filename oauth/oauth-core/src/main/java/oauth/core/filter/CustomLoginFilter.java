package oauth.core.filter;

import java.io.IOException;
import java.util.Map;

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
import oauth.core.util.JwtUtil;

public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	
	public CustomLoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
		this.jwtUtil = new JwtUtil();
        setFilterProcessesUrl("/api/login"); // 로그인 처리 URL
    }
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String username = obtainUsername(request);
		String password = obtainPassword(request);
		
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

		return authenticationManager.authenticate(authRequest);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authentication) throws JsonProcessingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		
		String token = jwtUtil.generateToken(authentication.getName());

		response.setContentType("application/json");
		response.getWriter().write(objectMapper
				.writeValueAsString(Map.of(
						"message", 
						"Authentication successful", 
						"user", 
						authentication.getPrincipal(),
						"token",
						token
				)));
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
