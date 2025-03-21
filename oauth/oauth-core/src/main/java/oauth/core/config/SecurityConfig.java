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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import oauth.core.filter.JwtAuthenticationFilter;
import oauth.core.handler.CustomAuthenticationFailureHandler;
import oauth.core.handler.CustomAuthenticationSuccessHandler;
import oauth.core.handler.CustomOAuth2FailureHandler;
import oauth.core.handler.CustomOAuth2SuccessHandler;
import oauth.core.properties.JwtProperties;
import oauth.core.util.JwtUtil;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
	private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
	private final JwtUtil jwtUtil;
	private final JwtProperties jwtProperties;
	
	@Bean
	@Order(2)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable);
		
	    http.formLogin(form -> form
	        	.loginPage("/web/login")
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
	    		.requestMatchers("/web/login", "/web/login-fail", "/error", "/api/oauth/refresh").permitAll()
	    		.anyRequest().authenticated()
	    	);
	    
	    http.anonymous(AbstractHttpConfigurer::disable);
	    
	    http.sessionManagement(session -> session
	            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        );
	    
	    http.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, jwtProperties), UsernamePasswordAuthenticationFilter.class);
	    
	    return http.build();
	}
	
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/images/**");
	}
}