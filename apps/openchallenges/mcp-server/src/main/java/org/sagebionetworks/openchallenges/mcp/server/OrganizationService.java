package org.sagebionetworks.openchallenges.mcp.server;

import io.micrometer.common.lang.Nullable;
import java.util.List;
import org.sagebionetworks.openchallenges.api.client.api.OrganizationApi;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeContributionRole;
import org.sagebionetworks.openchallenges.api.client.model.OrganizationCategory;
import org.sagebionetworks.openchallenges.api.client.model.OrganizationDirection;
import org.sagebionetworks.openchallenges.api.client.model.OrganizationSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.OrganizationSort;
import org.sagebionetworks.openchallenges.api.client.model.OrganizationsPage;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

  private final OrganizationApi organizationApi;

  public OrganizationService(OrganizationApi organizationApi) {
    this.organizationApi = organizationApi;
  }

  @Tool(
    name = "list_organizations",
    description = """
    Lists organizations that can be filtered and sorted based on a variety of parameters.

    Guidelines for using this tool:
    - If a parameter is not specified, you can omit it and default values will be applied.
    - To find challenges associated with an organization:
      1. Call this tool to retrieve the organization and get its `id`.
      2. Then call `list_challenges` with the `organizations` parameter set to that `id`.
    """
  )
  public OrganizationsPage listOrganizations(
    @ToolParam(
      description = "The page number to retrieve. The first page is 0."
    ) @Nullable Integer pageNumber,
    @ToolParam(description = "The number of items per page.") @Nullable Integer pageSize,
    @ToolParam(description = "Organization categories: featured") @Nullable List<
      OrganizationCategory
    > categories,
    @ToolParam(
      description = "Challenge contribution roles: challenge_organizer, data_contributor, sponsor"
    ) @Nullable List<ChallengeContributionRole> challengeContributionRoles,
    @ToolParam(
      description = "Sort field: challenge_count, created, name, relevance"
    ) @Nullable OrganizationSort sort,
    @ToolParam(description = "Sort direction: asc, desc") @Nullable OrganizationDirection direction,
    @ToolParam(description = "List of organization IDs to filter by") @Nullable List<Long> ids,
    @ToolParam(
      description = "Free-text search string to match organization names or descriptions"
    ) @Nullable String searchTerms
  ) {
    OrganizationSearchQuery query = new OrganizationSearchQuery();
    query.setPageNumber(pageNumber);
    query.setPageSize(pageSize);
    query.setCategories(categories);
    query.setChallengeContributionRoles(challengeContributionRoles);
    query.setSort(sort);
    query.setDirection(direction);
    query.setIds(ids);
    query.setSearchTerms(searchTerms);

    return organizationApi.listOrganizations(query);
  }
}
