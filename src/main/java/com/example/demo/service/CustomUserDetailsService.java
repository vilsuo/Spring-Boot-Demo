
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.service.AccountService;
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
	private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountService.findByUsername(username).orElseThrow(
			() -> new UsernameNotFoundException("No such username: " + username)
		);

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
				Arrays.asList(new SimpleGrantedAuthority(account.getRole().getName()))
		);
    }
}