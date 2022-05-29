package org.sagebionetworks.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ChallengeServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChallengeServiceRegistryApplication.class, args);
	}

}
