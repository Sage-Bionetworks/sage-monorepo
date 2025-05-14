package org.sagebionetworks.openchallenges.mcp.server.config;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@RegisterReflectionForBinding(
  {
    org.sagebionetworks.openchallenges.api.client.model.ChallengesPage.class,
    org.sagebionetworks.openchallenges.api.client.model.ChallengesPerYear.class,
    org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformsPage.class,
  }
)
public class ReflectionHintsConfig {}
