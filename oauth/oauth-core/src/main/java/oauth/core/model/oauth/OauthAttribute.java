package oauth.core.model.oauth;

import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@Getter
public class OauthAttribute {

	private final String email;
	private final String registrationId;
	private final Map<String, Object> attributes;
	private final String nameAttributeKey;

	@SuppressWarnings("unchecked")
	public OauthAttribute(OAuth2User oAuth2User, String clientRegistrationId) {
		Map<String, Object> attributes = oAuth2User.getAttributes();
		String nameAttributeKey = "email";

		if ("NAVER".equals(clientRegistrationId)) {
			attributes = (Map<String, Object>) attributes.get("response");
			nameAttributeKey = "response";
		} else if ("KAKAO".equals(clientRegistrationId)) {
			attributes = (Map<String, Object>) attributes.get("kakao_account");
			nameAttributeKey = "kakao_account";
		}

		this.email = (String)("GITHUB".equals(clientRegistrationId) ? attributes.get("html_url") : attributes.get("email"));
		this.registrationId = clientRegistrationId;
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
	}
}
