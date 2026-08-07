package com.fereloboost.api;

import org.springframework.boot.SpringApplication;

public class TestFereloboostApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(FereloboostApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
