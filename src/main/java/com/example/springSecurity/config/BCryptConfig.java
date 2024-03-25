package com.example.springSecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class BCryptConfig {
	
	@Bean
	public BCryptPasswordEncoder bCryptEcnoder() {
		return new BCryptPasswordEncoder();
		
	}
}
