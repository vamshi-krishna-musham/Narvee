package com.narvee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
@LoadBalancerClients
public class AtsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtsServerApplication.class, args);
		
		//System.out.println("Instance one.......");
		System.out.println("Hi sai");
	}
	// hello kiran
}
