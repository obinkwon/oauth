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
import oauth.core.util.CookieUtil;
import oauth.core.util.JwtUtil;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/oauth")
public class OauthApiController {
	
	private final JwtUtil jwtUtil;
	
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshApi(HttpServletResponse response, @RequestParam("token") String token) throws Exception {

    	log.error("token :::: {}",token);
    	String refreshToken = jwtUtil.getRefreshToken(token);
		log.error("refreshToken :::: {}",refreshToken);
		if(refreshToken != null) {
			String id = jwtUtil.getRefreshTokenId(refreshToken);
			Authentication authentication = jwtUtil.getAuthentication(token);
			String accessToken = jwtUtil.generateToken(authentication, id);
			log.error("accessToken :::: {}",accessToken);
			CookieUtil.generateCookie(response, "token", accessToken, (int) 1000 * 60 * 60 / 1000);
	        
			SecurityContextHolder.getContext().setAuthentication(authentication);
			return ResponseEntity.ok("success");
		}
    	return ResponseEntity.ok("잘못된 토큰입니다.");
    }
    
}
