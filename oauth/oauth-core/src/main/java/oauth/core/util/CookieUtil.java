package oauth.core.util;

import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {
	
	private static String TOKEN_NAME = "token" ;
	
	// 쿠키 생성
	public static void generateCookie(HttpServletResponse response, String token, int maxAge) {
		generateCookie(response, TOKEN_NAME, token, maxAge);
	}
		
	// 쿠키 생성
	public static void generateCookie(HttpServletResponse response, String tokenName, String token, int maxAge) {
		Cookie cookie = new Cookie(tokenName, token);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		
		response.addCookie(cookie);
	}
	
	// 쿠키 만료
	public static void expireCookie(HttpServletRequest request, HttpServletResponse response) {
		expireCookie(request, response, TOKEN_NAME);
	}
	
	// 쿠키 만료
	public static void expireCookie(HttpServletRequest request, HttpServletResponse response, String tokenName) {
		// 쿠키 가져오기
		Cookie cookie = WebUtils.getCookie(request, tokenName);
		
		if (cookie != null) {
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
	}
	
	public static String getCookieToken(HttpServletRequest request) {
		return getCookieToken(request, TOKEN_NAME);
	}
	
	public static String getCookieToken(HttpServletRequest request, String tokenName) {
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
