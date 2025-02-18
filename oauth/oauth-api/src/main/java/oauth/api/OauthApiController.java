package oauth.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth.core.properties.JwtProperties;
import oauth.core.util.CookieUtil;
import oauth.core.util.JwtUtil;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/oauth")
public class OauthApiController {
	
	private final JwtUtil jwtUtil;
	private final JwtProperties jwtProperties;
	
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshApi(HttpServletResponse response, @RequestParam("token") String token) throws Exception {

    	String refreshToken = jwtUtil.getRefreshToken(token);
    	
		if(refreshToken != null) {
			String id = jwtUtil.getRefreshTokenId(refreshToken);
			Authentication authentication = jwtUtil.getAuthentication(token);
			String accessToken = jwtUtil.generateToken(authentication, id);
			CookieUtil.generateCookie(response, "token", accessToken, (int) jwtProperties.getRefreshtokenTime().toMinutes());
	        
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return ResponseEntity.ok("success");
		}
    	return ResponseEntity.ok("잘못된 토큰입니다.");
    }
    
}
