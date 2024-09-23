package com.emna.micro_service5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) // Exclude DataSource auto-configuration
@EnableFeignClients // Enable Feign clients
public class MicroService5Application {

	public static void main(String[] args) {
		SpringApplication.run(MicroService5Application.class, args);
	}
}
