package oauth.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

import oauth.core.handler.CustomAuthenticationFailureHandler;
import oauth.core.handler.CustomAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@Order(0)
public class SecurityConfig {
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .formLogin(form -> form
	            .loginProcessingUrl("/api/login")
	            .successHandler(new CustomAuthenticationSuccessHandler())
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