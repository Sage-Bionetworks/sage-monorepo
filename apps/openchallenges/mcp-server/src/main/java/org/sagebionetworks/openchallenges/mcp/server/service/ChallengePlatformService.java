package org.sagebionetworks.openchallenges.mcp.server.service;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class ChallengePlatformService {

  private final ChallengePlatformApi challengePlatformApi;

  @Tool(
    name = "list_challenge_platforms",
    description = """
    Retrieve challenge platforms (hosting venues) with optional filtering and sorting.

    Usage:
    - Provide only filters user requests (slugs, searchTerms).
    - Use this first to discover a platform slug before filtering challenges (then pass slug to list_challenges via platforms list).

    Examples:
    - "List platforms" -> no filters.
    - "Find platform named synapse" -> searchTerms="synapse".
    - "Show specific platforms" -> slugs=["synapse","openchallenges"].
    """
  )
  public ChallengePlatformsPage listChallengePlatforms(
    @ToolParam(
      description = "Page index (integer >=0). First page is 0."
    ) @Nullable @PositiveOrZero Integer pageNumber,
    @ToolParam(
      description = "Page size (integer 1-200). Default if null."
    ) @Nullable @Positive Integer pageSize,
    @ToolParam(
      description = "Sort enum: created|name|relevance (see API)."
    ) @Nullable ChallengePlatformSort sort,
    @ToolParam(
      description = "Sort direction enum: asc|desc."
    ) @Nullable ChallengePlatformDirection direction,
    @ToolParam(description = "Platform slug list (exact matches).") @Nullable List<String> slugs,
    @ToolParam(
      description = "Free-text search (short distinctive keyword)."
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
