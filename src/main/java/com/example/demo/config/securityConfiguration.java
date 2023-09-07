
package com.example.demo.config;

import com.example.demo.domain.Role;
import com.example.demo.validator.UsernameValidator;
import org.springframework.beans.factory.annotation.Value;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {
	
	@Value("${security.enable-csrf:true}")
   	private boolean csrfEnabled;
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		if (!csrfEnabled) {
			http
				.csrf((csrf) -> csrf
					.ignoringRequestMatchers(toH2Console())
				)
				.authorizeHttpRequests((requests) -> requests
					.requestMatchers(toH2Console()).permitAll()
				)
				.headers((headers) -> headers
					.frameOptions((frameOptions) -> 
						frameOptions.sameOrigin()
					)
				);
		}
		
		http	
			// https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
			.authorizeHttpRequests((requests) -> requests
				// https://stackoverflow.com/questions/62531927/spring-security-redirect-to-static-resources-after-authentication
				.requestMatchers("/js/**", "/css/**", "/images/**").permitAll()
					
				// allow anyone to view 'home-page'
				.requestMatchers(HttpMethod.GET, "/", "/index", "/accounts").permitAll()
					
				// allow anyone to register an account
				.requestMatchers("/register", "/register/create").permitAll()
				
				// allow anyone to view account pages
				.requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "/accounts/" + UsernameValidator.SIMPLE_USERNAME_PATTERN)).permitAll()
					
				// allow anyone to view account images
				.requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET, "/accounts/" + UsernameValidator.SIMPLE_USERNAME_PATTERN + "/images")).permitAll()
				
				// DUBLICATE? IMPLEMENT SECURITY IN METHOD?
				// allow signed in accounts to follow other accounts
				//.requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.POST, "/accounts/" + UsernameValidator.SIMPLE_USERNAME_PATTERN + "/follow")).hasAuthority(Role.USER.getName())
					
				//.requestMatchers(HttpMethod.POST, "/accounts/ /follow").hasAuthority(Role.USER.getName())
					
				// allow admins to view and edit all admin pages
				.requestMatchers("/admin", "/admin/**").hasAuthority(Role.ADMIN.getValue())
				
				.anyRequest().authenticated()
			)
			// https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html
			.formLogin((form) -> form
				.loginPage("/login")
				.permitAll()
			)
			.logout((logout) -> logout.permitAll());
		
		return http.build();
	}
	
	/*
	In the old version you inject AuthenticationManagerBuilder, set 
	userDetailsService, passwordEncoder and build it. But authenticationManager 
	is already created in this step. It is created the way we wanted (with 
	userDetailsService and the passwordEncoder).
	
	source: https://stackoverflow.com/questions/72381114/spring-security-upgrading-the-deprecated-websecurityconfigureradapter-in-spring
	*/
	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration authenticationConfiguration) throws Exception {
			
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	/*
	// source: https://www.baeldung.com/role-and-privilege-for-spring-security-registration
	@Bean
	public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
		DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
		expressionHandler.setRoleHierarchy(roleHierarchy());
		return expressionHandler;
	}
	
	// info: https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/access/hierarchicalroles/RoleHierarchyImpl.html
	@Bean
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
		String hierarchy = Role.ADMIN.getName() + " > " + Role.USER.getName();
		roleHierarchy.setHierarchy(hierarchy);
		return roleHierarchy;
	}
	*/
}
