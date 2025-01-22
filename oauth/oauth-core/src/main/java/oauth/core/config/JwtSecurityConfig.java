package oauth.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import oauth.core.filter.CustomLoginFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class JwtSecurityConfig {
	
	private final AuthenticationConfiguration authenticationConfiguration;
	
	@Bean
	@Order(0)
	SecurityFilterChain jwtSecurityFilterChain(HttpSecurity http) throws Exception {
		
		AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
		
		http
			.securityMatcher("/api/**")
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(authz -> authz
				.requestMatchers("/api/login").permitAll()
                .anyRequest().authenticated()
			)
			.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
            )
			.addFilterAt(new CustomLoginFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
}
