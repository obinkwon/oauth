package oauth.core.model;

import java.util.Map;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OauthAttribute {

	private final String email;
	private final String registrationId;
	private final Map<String, Object> attributes;
	
	
	@SuppressWarnings("unchecked")
	public OauthAttribute(OAuth2AuthenticationToken oauth2AuthenticationToken) {
		OAuth2User oauth2User = oauth2AuthenticationToken.getPrincipal();
        String clientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId().toUpperCase();
        
		Map<String, Object> attributes = oauth2User.getAttributes();

		if ("NAVER".equals(clientRegistrationId)) {
			attributes = (Map<String, Object>) attributes.get("response");
		} else if ("KAKAO".equals(clientRegistrationId)) {
			attributes = (Map<String, Object>) attributes.get("kakao_account");
		}

		this.email = (String)("GITHUB".equals(clientRegistrationId) ? attributes.get("html_url") : attributes.get("email"));
		this.registrationId = clientRegistrationId;
		this.attributes = attributes;
	}
}
