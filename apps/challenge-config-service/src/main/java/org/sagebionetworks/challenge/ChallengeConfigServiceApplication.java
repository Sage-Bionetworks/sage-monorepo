package org.sagebionetworks.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableConfigServer
@SpringBootApplication
public class ChallengeConfigServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChallengeConfigServiceApplication.class, args);
	}

}
