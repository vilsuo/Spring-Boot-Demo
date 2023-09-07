
package com.example.demo.service;

import com.example.demo.domain.Account;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private AccountFinderService accountFinderService;
	
    @Override
    public UserDetails loadUserByUsername(final String username)
			throws UsernameNotFoundException {
		
		final Account account = accountFinderService.findByUsername(username);

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
				Arrays.asList(
					new SimpleGrantedAuthority(account.getRole().getValue())
				)
		);
    }
}