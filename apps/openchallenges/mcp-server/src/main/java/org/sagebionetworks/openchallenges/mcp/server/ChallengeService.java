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

  @Tool(
    name = "list_challenges",
    description = """
    Lists challenges that can be filtered and sorted based on a variety of criteria.

    When using the `list_challenges` tool:
    - If a parameter is not specified, its default value should be used.
    - If the user mentions 'Docker' or 'container', use `submissionTypes = ["container_image"]`
    - If the user describes specific types of input data (e.g., "RNA-Seq"), you should:
      1. Call the `list_edam_concepts` tool with `searchTerms` matching the user’s keywords.
      2. Include `sections = ["data"]` to filter the EDAM concepts specifically to input data types.
      3. From the results, extract the matching EDAM concept ID(s).
      4. Then call `list_challenges` with those IDs as the `inputDataTypes` filter.
    """
  )
  public ChallengesPage listChallenges(
    @ToolParam(
      description = "The page number to retrieve. Default is 0."
    ) @Nullable Integer pageNumber,
    @ToolParam(description = "The number of items per page.") @Nullable Integer pageSize,
    @ToolParam(
      description = "Sort field: created, random, relevance, starred, start_date, end_date."
    ) @Nullable ChallengeSort sort,
    @ToolParam(
      description = "Seed for random sort to ensure reproducible results."
    ) @Nullable Integer sortSeed,
    @ToolParam(description = "Sort direction: asc, desc.") @Nullable ChallengeDirection direction,
    @ToolParam(
      description = "Incentive types: monetary, publication, speaking_engagement, other."
    ) @Nullable List<ChallengeIncentive> incentives,
    @ToolParam(
      description = "Filter for challenges starting on or after this date (format: yyyy-MM-dd)."
    ) @Nullable String minStartDate,
    @ToolParam(
      description = "Filter for challenges starting on or before this date (format: yyyy-MM-dd)."
    ) @Nullable String maxStartDate,
    @ToolParam(
      description = "List of platform slugs (e.g., synapse, openchallenges)."
    ) @Nullable List<String> platforms,
    @ToolParam(description = "List of organization IDs to filter by.") @Nullable List<
      Long
    > organizations,
    @ToolParam(description = "Challenge statuses: upcoming, active, completed.") @Nullable List<
      ChallengeStatus
    > status,
    @ToolParam(
      description = "Submission types: container_image, prediction_file, notebook, mlcube, other."
    ) @Nullable List<ChallengeSubmissionType> submissionTypes,
    @ToolParam(description = "EDAM concept IDs for input data types.") @Nullable List<
      Long
    > inputDataTypes,
    @ToolParam(description = "EDAM concept IDs for operations.") @Nullable List<Long> operations,
    @ToolParam(
      description = "Challenge categories: featured, benchmark, hackathon, starting_soon, ending_soon, recently_started, recently_ended."
    ) @Nullable List<ChallengeCategory> categories,
    @ToolParam(
      description = "Free-text search query used to match challenge names and descriptions."
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
