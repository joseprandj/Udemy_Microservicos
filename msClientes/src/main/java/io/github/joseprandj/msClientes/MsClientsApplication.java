package io.github.joseprandj.msClientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MsClientsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsClientsApplication.class, args);
	}

}
