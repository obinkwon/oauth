package oauth.core.config;

import lombok.RequiredArgsConstructor;
import oauth.core.filter.JwtAuthenticationFilter;
import oauth.core.handler.*;
import oauth.core.properties.JwtProperties;
import oauth.core.provider.CustomAuthenticationProvider;
import oauth.core.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
	private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
	private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
	private final CustomLogoutHandler customLogoutHandler;
	private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
	private final JwtUtil jwtUtil;
	private final JwtProperties jwtProperties;
	
	@Bean
	@Order(0)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable);
		
	    http.formLogin(form -> form
	        	.loginPage("/web/login")
	            .loginProcessingUrl("/web/login/process")
	            .successHandler(customAuthenticationSuccessHandler)
	            .failureHandler(customAuthenticationFailureHandler)
	            .permitAll()
	        );
	    
	    http.logout(logout -> logout
	            .logoutUrl("/web/logout/process").permitAll()
	            .addLogoutHandler(customLogoutHandler)
	            .logoutSuccessHandler(customLogoutSuccessHandler)
	            .deleteCookies("JSESSIONID")
	            .invalidateHttpSession(true)
	    	);
	    
	    http.oauth2Login(oauth2 -> oauth2
                .redirectionEndpoint(redir -> redir
                    .baseUri("/login/oauth2/code/*")
                )
                .successHandler(customOAuth2SuccessHandler)
                .failureHandler(customOAuth2FailureHandler)
            );
	    
	    http.authorizeHttpRequests(authz -> authz
	    		.requestMatchers("/web/login", "/web/login-fail", "/error", "/api/oauth/refresh", "/api/logout/*", "/web/signup", "/api/login/*").permitAll()
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
	
	@Bean
	AuthenticationManager authenticationManager(HttpSecurity http, CustomAuthenticationProvider customAuthenticationProvider) throws Exception {
	    return http.getSharedObject(AuthenticationManagerBuilder.class)
			       .authenticationProvider(customAuthenticationProvider)
			       .build();
	}

	// 암호화 방식 설정
    @Bean
	PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}