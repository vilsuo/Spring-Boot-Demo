
package com.example.demo.config;

import com.example.demo.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console; // !
import org.springframework.http.HttpMethod;

//@Profile("development")
@Configuration
@EnableWebSecurity
public class DevelopmentSecurityConfiguration {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		// mahdollistetaan h2-konsolin käyttö
		// https://stackoverflow.com/questions/74680244/h2-database-console-not-opening-with-spring-security
		http
			.csrf((csrf) -> csrf
				.ignoringRequestMatchers(toH2Console())
			);
		http
			.headers((headers) -> headers
				.frameOptions((frameOptions) -> 
					frameOptions.sameOrigin()
				)
			);
		
		http	
			// https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
			.authorizeHttpRequests((requests) -> requests
				.requestMatchers("/", "/accounts").permitAll()
				.requestMatchers(HttpMethod.GET, "/messages").hasAuthority(Role.USER.getName())
				.requestMatchers(HttpMethod.GET, "/admin").hasAuthority(Role.ADMIN.getName())
				.requestMatchers(toH2Console()).permitAll()
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
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
