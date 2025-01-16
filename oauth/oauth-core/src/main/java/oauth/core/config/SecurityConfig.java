package oauth.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import oauth.core.handler.CustomAuthenticationFailureHandler;
import oauth.core.handler.CustomAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	@Order(0)
	SecurityFilterChain securityApiFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher("/api/**")
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(authz -> authz
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.requestMatchers("/api/user/**").hasRole("USER")
				.anyRequest().authenticated()
			);
		
		return http.build();
	}

	@Bean
	@Order(1)
	SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
	    http
	        .formLogin(form -> form
	            .loginProcessingUrl("/api/login")
	            .defaultSuccessUrl("/main", true)
	            .successHandler(new CustomAuthenticationSuccessHandler())
	            .failureUrl("/login-fail")
	            .failureHandler(new CustomAuthenticationFailureHandler())
	            .permitAll()
	        )
	        .authorizeHttpRequests(authz -> authz
	            .requestMatchers("/login", "/error").permitAll()
	            .anyRequest().authenticated()
	        );
	    return http.build();
	}
	
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**");
	}
}