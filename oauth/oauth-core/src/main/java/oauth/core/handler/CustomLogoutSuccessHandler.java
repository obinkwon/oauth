package oauth.core.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	
	@Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("✅ 로그아웃 성공");
        
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "로그아웃 성공");
        responseBody.put("status", HttpServletResponse.SC_OK);

        String json = objectMapper.writeValueAsString(responseBody);
        response.getWriter().write(json);
    }
}

