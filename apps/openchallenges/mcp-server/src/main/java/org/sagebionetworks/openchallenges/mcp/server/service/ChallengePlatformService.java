package org.sagebionetworks.openchallenges.mcp.server.service;

import io.micrometer.common.lang.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.openchallenges.api.client.api.ChallengePlatformApi;
import org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformDirection;
import org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformSort;
import org.sagebionetworks.openchallenges.api.client.model.ChallengePlatformsPage;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengePlatformService {

  private final ChallengePlatformApi challengePlatformApi;

  @Tool(name = "list_challenge_platforms", description = "List challenge platforms")
  public ChallengePlatformsPage listChallengePlatforms(
    @ToolParam(description = "The page number.") @Nullable Integer pageNumber,
    @ToolParam(description = "The number of items in a single page.") @Nullable Integer pageSize,
    @ToolParam(description = "What to sort results by.") @Nullable ChallengePlatformSort sort,
    @ToolParam(
      description = "The direction to sort the results by."
    ) @Nullable ChallengePlatformDirection direction,
    @ToolParam(
      description = "An array of challenge platform slugs used to filter the results."
    ) @Nullable List<String> slugs,
    @ToolParam(
      description = "A string of search terms used to filter the results."
    ) @Nullable String searchTerms
  ) {
    ChallengePlatformSearchQuery query = new ChallengePlatformSearchQuery();

    query.setPageNumber(pageNumber);
    query.setPageSize(pageSize);
    query.setSort(sort);
    query.setDirection(direction);
    query.setSlugs(slugs);
    query.setSearchTerms(searchTerms);

    return challengePlatformApi.listChallengePlatforms(query);
  }
}
