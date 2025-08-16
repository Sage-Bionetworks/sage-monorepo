package org.sagebionetworks.bixarena.api.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "org.sagebionetworks.bixarena.api")
@EntityScan(basePackages = "org.sagebionetworks.bixarena.api.model.entity")
@EnableJpaRepositories(basePackages = "org.sagebionetworks.bixarena.api.model.repository")
@EnableTransactionManagement
public class BixArenaApiConfiguration {}
