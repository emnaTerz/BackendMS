package com.emna.micro_service2;

import com.emna.micro_service2.repository.IndexConfigurationAttributeToAddRepository;
import com.emna.micro_service2.repository.IndexConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients

public class MicroService2Application extends SpringBootServletInitializer {
	@Autowired
	IndexConfigurationRepository  indexConfigurationRepository;
	@Autowired
	IndexConfigurationAttributeToAddRepository indexConfigurationAttributeToAddRepository;
	public static void main(String[] args) {
		SpringApplication.run(MicroService2Application.class, args);
	}

}
