
package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

@Lazy
@TestConfiguration
public class TestConfig {
	
	/*
	@Bean
	public Integer port(@Value("${local.server.port}") Integer port) {
		return port;
	}
	*/
}
