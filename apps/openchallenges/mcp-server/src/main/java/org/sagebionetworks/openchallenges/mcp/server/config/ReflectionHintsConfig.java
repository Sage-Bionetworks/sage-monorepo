package org.sagebionetworks.openchallenges.mcp.server.config;

import org.sagebionetworks.openchallenges.mcp.server.ChallengePlatformsPage;
import org.sagebionetworks.openchallenges.mcp.server.ChallengesPerYear;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@RegisterReflectionForBinding({ ChallengesPerYear.class, ChallengePlatformsPage.class })
public class ReflectionHintsConfig {}
