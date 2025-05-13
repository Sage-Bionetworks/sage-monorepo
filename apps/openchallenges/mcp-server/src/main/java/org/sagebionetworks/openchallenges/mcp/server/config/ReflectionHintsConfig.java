package org.sagebionetworks.openchallenges.mcp.server.config;

import org.sagebionetworks.openchallenges.mcp.server.ChallengePlatformsPage;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@RegisterReflectionForBinding(
  {
    org.sagebionetworks.openchallenges.api.client.model.ChallengesPerYear.class,
    ChallengePlatformsPage.class,
  }
)
public class ReflectionHintsConfig {}
