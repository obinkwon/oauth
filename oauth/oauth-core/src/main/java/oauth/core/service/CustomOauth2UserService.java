package oauth.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oauth.core.entity.UserEntity;
import oauth.core.model.oauth.OauthAttribute;
import oauth.core.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ANONYMOUS");
        // Oauth 인증 정보
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String clientRegistrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        // Oauth attribute
        OauthAttribute oauthAttribute = new OauthAttribute(oAuth2User, clientRegistrationId);
        // Oauth 인증 이메일
        String email = oauthAttribute.getEmail();
        // 인증정보에서 email 정보 체크
        if(StringUtils.isEmpty(email)){
            throw new OAuth2AuthenticationException("이메일 정보가 없습니다.");
        }
        // 사용자 이메일 조회
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);
        // 사용자 이메일 등록 여부 체크
        if(userEntityOptional.isPresent()){
            authority = new SimpleGrantedAuthority("USER");
        }
        // 사용자 정보 반환
        return new DefaultOAuth2User(List.of(authority),oAuth2User.getAttributes(),oauthAttribute.getNameAttributeKey());
    }
}

