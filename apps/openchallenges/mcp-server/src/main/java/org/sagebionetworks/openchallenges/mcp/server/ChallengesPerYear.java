package org.sagebionetworks.openchallenges.mcp.server;

import java.util.List;

public record ChallengesPerYear(
  List<String> years,
  List<Integer> challengeCounts,
  int undatedChallengeCount
) {}
