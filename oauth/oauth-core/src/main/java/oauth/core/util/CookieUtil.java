package oauth.core.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

	public static void generateCookie(HttpServletResponse response, String tokenName, String token, int maxAge) {
		Cookie cookie = new Cookie(tokenName, token);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		
		response.addCookie(cookie);
	}
}
