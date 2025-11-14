package org.sagebionetworks.openchallenges.mcp.server.service;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.openchallenges.api.client.api.OrganizationApi;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeParticipationRole;
import org.sagebionetworks.openchallenges.api.client.model.OrganizationCategory;
import org.sagebionetworks.openchallenges.api.client.model.OrganizationDirection;
import org.sagebionetworks.openchallenges.api.client.model.OrganizationSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.OrganizationSort;
import org.sagebionetworks.openchallenges.api.client.model.OrganizationsPage;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class OrganizationService {

  private final OrganizationApi organizationApi;

  @Tool(
    name = "list_organizations",
    description = """
    Retrieve organizations with optional filtering/sorting.

    Usage:
    - Provide only filters user specifies (categories, roles, ids, keywords).
    - To get challenges for an organization: (1) call this tool with searchTerms, (2) take returned id, (3) call list_challenges with organizations=[id].
    - Search terms: short, distinctive names or acronyms ("DREAM", "Broad", "Sage"). Avoid filler words.

    Examples:
    - "Find featured orgs" -> categories=[featured].
    - "Organizations sponsoring challenges" -> challengeParticipationRoles includes sponsor.
    - "DREAM challenges" -> searchTerms="DREAM" then pass its id to list_challenges.
    """
  )
  public OrganizationsPage listOrganizations(
    @ToolParam(
      description = "Page index (integer >=0). First page is 0."
    ) @Nullable @PositiveOrZero Integer pageNumber,
    @ToolParam(
      description = "Page size (integer 1-200). Uses default if null."
    ) @Nullable @Positive Integer pageSize,
    @ToolParam(description = "Category enums list: featured.") @Nullable List<
      OrganizationCategory
    > categories,
    @ToolParam(
      description = "Participation role enums: challenge_organizer|data_contributor|sponsor."
    ) @Nullable List<ChallengeParticipationRole> challengeParticipationRoles,
    @ToolParam(
      description = "Sort enum: challenge_count|created|name|relevance."
    ) @Nullable OrganizationSort sort,
    @ToolParam(
      description = "Sort direction enum: asc|desc."
    ) @Nullable OrganizationDirection direction,
    @ToolParam(description = "Organization ID list (long).") @Nullable List<
      @PositiveOrZero Long
    > ids,
    @ToolParam(
      description = "Free-text search (short distinctive name/acronym)."
    ) @Nullable String searchTerms
  ) {
    if (pageNumber != null && pageNumber < 0) {
      throw new IllegalArgumentException("pageNumber must be >= 0");
    }
    if (pageSize != null && pageSize <= 0) {
      throw new IllegalArgumentException("pageSize must be > 0");
    }
    if (ids != null && ids.stream().anyMatch(id -> id == null || id < 0)) {
      throw new IllegalArgumentException("organization ids must be >= 0");
    }

    OrganizationSearchQuery query = new OrganizationSearchQuery();
    query.setPageNumber(pageNumber);
    query.setPageSize(pageSize);
    query.setCategories(categories);
    query.setChallengeParticipationRoles(challengeParticipationRoles);
    query.setSort(sort);
    query.setDirection(direction);
    query.setIds(ids);
    query.setSearchTerms(searchTerms);

    return organizationApi.listOrganizations(query);
  }
}
