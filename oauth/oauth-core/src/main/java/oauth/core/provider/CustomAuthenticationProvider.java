package oauth.core.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;
    
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String email = authentication.getName();
        String password = (String) authentication.getCredentials();

	    UserDetails user = userDetailsService.loadUserByUsername(email);

		if(user == null) {
			throw new BadCredentialsException("등록되지 않은 이메일입니다.");
		}

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException("비밀번호가 올바르지 않습니다.");
		}

	    return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
