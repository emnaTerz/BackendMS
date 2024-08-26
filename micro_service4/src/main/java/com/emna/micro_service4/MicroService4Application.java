package com.emna.micro_service4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})

public class MicroService4Application {

	public static void main(String[] args) {
		SpringApplication.run(MicroService4Application.class, args);
	}

}
