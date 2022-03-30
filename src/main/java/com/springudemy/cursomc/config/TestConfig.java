package com.springudemy.cursomc.config;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.springudemy.cursomc.services.DBService;

@Configuration
@Profile("test")
public class TestConfig {

	@Autowired
	DBService dbServ;
	
	@Bean
	public boolean instantiateDatabase() throws ParseException {
		
		dbServ.instantiateTestDatabase();
		
		return true;
	}
	
}
