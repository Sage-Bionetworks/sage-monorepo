package org.sagebionetworks.openchallenges.mcp.server;

import io.micrometer.common.lang.Nullable;
import java.time.LocalDate;
import java.util.List;
import org.sagebionetworks.openchallenges.api.client.api.ChallengeApi;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeCategory;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeDirection;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeIncentive;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeSort;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeStatus;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeSubmissionType;
import org.sagebionetworks.openchallenges.api.client.model.ChallengesPage;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class ChallengeService {

  private final ChallengeApi challengeApi;

  public ChallengeService(ChallengeApi challengeApi) {
    this.challengeApi = challengeApi;
  }

  @Tool(name = "list_challenges", description = "List challenges")
  public ChallengesPage listChallenges(
    @ToolParam(description = "The page number.") @Nullable Integer pageNumber,
    @ToolParam(description = "The number of items in a single page.") @Nullable Integer pageSize,
    @ToolParam(description = "What to sort results by.") @Nullable ChallengeSort sort,
    @ToolParam(
      description = "The seed that initializes the random sorter."
    ) @Nullable Integer sortSeed,
    @ToolParam(
      description = "The direction to sort the results by."
    ) @Nullable ChallengeDirection direction,
    @ToolParam(
      description = "An array of challenge incentive types used to filter the results."
    ) @Nullable List<ChallengeIncentive> incentives,
    @ToolParam(
      description = "Keep the challenges that start at this date or later (yyyy-MM-dd)."
    ) @Nullable String minStartDate,
    @ToolParam(
      description = "Keep the challenges that start at this date or sooner (yyyy-MM-dd)."
    ) @Nullable String maxStartDate,
    @ToolParam(
      description = "An array of challenge platform slugs used to filter the results."
    ) @Nullable List<String> platforms,
    @ToolParam(
      description = "An array of organization IDs used to filter the results."
    ) @Nullable List<Long> organizations,
    @ToolParam(
      description = "An array of challenge statuses used to filter the results."
    ) @Nullable List<ChallengeStatus> status,
    @ToolParam(
      description = "An array of challenge submission types used to filter the results."
    ) @Nullable List<ChallengeSubmissionType> submissionTypes,
    @ToolParam(
      description = "An array of EDAM concept IDs used to filter by input data types."
    ) @Nullable List<Long> inputDataTypes,
    @ToolParam(
      description = "An array of EDAM concept IDs used to filter by operations."
    ) @Nullable List<Long> operations,
    @ToolParam(
      description = "An array of challenge categories used to filter the results."
    ) @Nullable List<ChallengeCategory> categories,
    @ToolParam(
      description = "A string of search terms used to filter the results."
    ) @Nullable String searchTerms
  ) {
    ChallengeSearchQuery query = new ChallengeSearchQuery();
    query.setPageNumber(pageNumber);
    query.setPageSize(pageSize);
    query.setSort(sort);
    query.setSortSeed(sortSeed);
    query.setDirection(direction);
    query.setIncentives(incentives);
    query.setMinStartDate(minStartDate != null ? LocalDate.parse(minStartDate) : null);
    query.setMaxStartDate(maxStartDate != null ? LocalDate.parse(maxStartDate) : null);
    query.setPlatforms(platforms);
    query.setOrganizations(organizations);
    query.setStatus(status);
    query.setSubmissionTypes(submissionTypes);
    query.setInputDataTypes(inputDataTypes);
    query.setOperations(operations);
    query.setCategories(categories);
    query.setSearchTerms(searchTerms);

    return challengeApi.listChallenges(query);
  }
}
