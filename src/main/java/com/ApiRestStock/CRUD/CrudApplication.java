package com.ApiRestStock.CRUD;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class CrudApplication {

	@PostConstruct
	public void init() {
		// Establece la zona horaria de Argentina para toda la aplicación
		TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
	}

	public static void main(String[] args) {
		SpringApplication.run(CrudApplication.class, args);
	}

}
