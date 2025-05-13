package org.sagebionetworks.openchallenges.mcp.server;

import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.sagebionetworks.openchallenges.api.client.api.ChallengePlatformApi;
import org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformDirection;
import org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformSort;
import org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformsPage;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class ChallengePlatformService {

  private final ChallengePlatformApi challengePlatformApi;

  public ChallengePlatformService(ChallengePlatformApi challengePlatformApi) {
    this.challengePlatformApi = challengePlatformApi;
  }

  @Tool(
    name = "list_challenge_platforms",
    description = "List available challenge platforms with optional filters"
  )
  public ChallengePlatformsPage listChallengePlatforms(
    @Nullable @Schema(description = "Page number to retrieve (0-based)") Integer pageNumber,
    @Nullable @Schema(description = "Page size to retrieve") Integer pageSize,
    @Nullable @Schema(description = "Sort field (enum): NAME") ChallengePlatformSort sort,
    @Nullable @Schema(
      description = "Sort direction (enum): ASC or DESC"
    ) ChallengePlatformDirection direction,
    @Nullable @Schema(description = "Platform slugs to filter by") List<String> slugs,
    @Nullable @Schema(description = "Search keywords") String searchTerms
  ) {
    ChallengePlatformSearchQuery query = new ChallengePlatformSearchQuery();

    if (pageNumber != null) {
      query.setPageNumber(pageNumber);
    }
    if (pageSize != null) {
      query.setPageSize(pageSize);
    }
    if (sort != null) {
      query.setSort(sort);
    }
    if (direction != null) {
      query.setDirection(direction);
    }
    if (slugs != null && !slugs.isEmpty()) {
      query.setSlugs(slugs);
    }
    if (searchTerms != null && !searchTerms.isEmpty()) {
      query.setSearchTerms(searchTerms);
    }

    return challengePlatformApi.listChallengePlatforms(query);
  }
}
