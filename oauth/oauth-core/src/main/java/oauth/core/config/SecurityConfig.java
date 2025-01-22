package oauth.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import oauth.core.handler.CustomAuthenticationFailureHandler;
import oauth.core.handler.CustomAuthenticationSuccessHandler;
import oauth.core.handler.CustomOAuth2FailureHandler;
import oauth.core.handler.CustomOAuth2SuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
	private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
	
	@Bean
	@Order(2)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable);
	    http.formLogin(form -> form
	        	.loginPage("/web/login-view")
	            .loginProcessingUrl("/api/login")
	            .successHandler(customAuthenticationSuccessHandler)
	            .failureHandler(customAuthenticationFailureHandler)
	            .permitAll()
	        );
	    
	    http.oauth2Login(oauth2 -> oauth2
                .redirectionEndpoint(redir -> redir
                    .baseUri("/login/oauth2/code/*")
                )
                .successHandler(customOAuth2SuccessHandler)
                .failureHandler(customOAuth2FailureHandler)
            );
	    
	    http.authorizeHttpRequests(authz -> authz
	    		.requestMatchers("/web/login-view", "/web/login-fail", "/web/main").permitAll()
	    		.anyRequest().authenticated()
	    	);
	    
	    http.sessionManagement(session -> session
	            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        );
	    
	    return http.build();
	}
	
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**");
	}
}