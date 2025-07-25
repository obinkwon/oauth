package oauth.core.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import oauth.core.properties.JwtProperties;
import oauth.core.util.CookieUtil;
import oauth.core.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;
	private final JwtProperties jwtProperties;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String id = UUID.randomUUID().toString();
		String token = jwtUtil.generateToken(authentication, id);
		// Refresh Token 생성
		jwtUtil.refreshToken(id);

		CookieUtil.generateCookie(response, token, (int) jwtProperties.getRefreshtokenTime().toMinutes());

		response.setContentType("application/json");
		response.addHeader("Authorization", "Bearer " + token);
		response.getWriter().write(objectMapper
				.writeValueAsString(Map.of(
						"message", "Authentication successful",
						"token", token
				)));
		response.sendRedirect("/web/main");
	}
}
