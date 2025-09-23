package org.sagebionetworks.openchallenges.mcp.server.configuration;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@RegisterReflectionForBinding(
  {
    org.sagebionetworks.openchallenges.api.client.model.ChallengesPage.class,
    org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionsPage.class,
    org.sagebionetworks.openchallenges.api.client.model.ChallengesPerYear.class,
    org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformsPage.class,
    org.sagebionetworks.openchallenges.api.client.model.EdamConceptsPage.class,
    org.sagebionetworks.openchallenges.api.client.model.OrganizationsPage.class,
  }
)
public class ReflectionHintsConfig {}
