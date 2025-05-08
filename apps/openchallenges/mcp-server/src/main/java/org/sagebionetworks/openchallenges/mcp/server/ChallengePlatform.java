package org.sagebionetworks.openchallenges.mcp.server;

public record ChallengePlatform(
  long id,
  String slug,
  String name,
  String avatarUrl,
  String websiteUrl,
  String createdAt,
  String updatedAt
) {}
