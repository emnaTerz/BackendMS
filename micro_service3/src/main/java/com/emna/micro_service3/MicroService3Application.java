package com.emna.micro_service3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class })
/*@EnableElasticsearchRepositories(basePackages = "com.emna.micro_service3.Repository")*/

public class MicroService3Application  {

	public static void main(String[] args) {
		SpringApplication.run(MicroService3Application.class, args);
	}

}
