package org.sagebionetworks.openchallenges.config.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class OpenChallengesConfigServer {

	public static void main(String[] args) {
		SpringApplication.run(OpenChallengesConfigServer.class, args);
	}

}
