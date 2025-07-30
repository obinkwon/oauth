package oauth.core.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth.core.util.CookieUtil;
import oauth.core.util.JwtUtil;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {

	private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    	// 쿠키에서 토큰 가져오기
    	String accessToken = CookieUtil.getCookieToken(request);
    	if(StringUtils.isNotEmpty(accessToken)) {
    		// redis에서 해당 refresh 토큰 삭제
    		jwtUtil.expireRefreshToken(accessToken);
    	}
        // 쿠키 만료 시키기
    	CookieUtil.expireCookie(request, response);
    }
}
