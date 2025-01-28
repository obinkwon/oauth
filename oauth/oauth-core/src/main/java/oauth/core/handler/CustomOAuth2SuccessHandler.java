package oauth.core.handler;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth.core.model.OauthAttribute;
import oauth.core.util.JwtUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
	
	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		
		String token = jwtUtil.generateOauth2Token(new OauthAttribute((OAuth2AuthenticationToken) authentication));

		Cookie jwtCookie = new Cookie("token", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge((int) 1000 * 60 * 60 / 1000);

        response.addCookie(jwtCookie);
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
