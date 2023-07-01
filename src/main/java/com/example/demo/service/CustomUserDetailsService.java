
package com.example.demo.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.AccountWithRelation;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	/*
	@Autowired
	private AccountService accountService;
	*/
	
	@Autowired
	private AccountWithRelationService accountService;
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AccountWithRelation account = accountService.findByUsername(username);

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
				Arrays.asList(new SimpleGrantedAuthority(account.getRole().getName()))
		);
    }
}