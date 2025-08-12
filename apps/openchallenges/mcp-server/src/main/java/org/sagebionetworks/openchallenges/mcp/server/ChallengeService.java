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
    Lists challenges that can be filtered and sorted based on a variety of parameters.

    Guidelines for using this tool:
    - If a parameter is not specified, you can omit it and default values will be applied.
    - Do not use `minStartDate` or `maxStartDate` unless the user specifies a time window (e.g., “challenges starting after June”).
    - Only apply filters that the user has explicitly requested or implied.
    - If the user mentions "Docker" or "Container", include "container_image" in the submissionTypes, while still allowing other types.
    - If the user describes specific types of input data / training data:
        1. First call the `list_edam_concepts` tool with the user's keywords as `searchTerms`.
        2. Set `sections = ["data"]` to filter results to input data types.
        3. From the results, extract the matching EDAM concept IDs.
        4. Then call `list_challenges` using: inputDataTypes = [<EDAM ID(s)>]
    - If the user refers to a specific organization (e.g., "DREAM", "Broad"):
        1. Use `list_organizations` to search by name or acronym.
        2. Extract the organization's `id` from the result.
        3. Call this tool using: organizations = [<organizationId>]
    - Search Term Strategy:
      - Use SHORT, DISTINCTIVE, and RELEVANT keywords.
      - Avoid generic filler words or overly long descriptions.
    - Always prefer returning *some relevant* result over none. If initial query returns nothing:
      - Relax some filters or retry with different filter combinations.
      - Try at least 3 times
    """
  )
  public ChallengesPage listChallenges(
    @ToolParam(
      description = "The page number to retrieve. The first page is 0."
    ) @Nullable Integer pageNumber,
    @ToolParam(description = "The number of items per page.") @Nullable Integer pageSize,
    @ToolParam(
      description = "Sort field: created, random, relevance, starred, start_date, end_date. Starred is sorted by the number of stars/bookmarks"
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
      Integer
    > inputDataTypes,
    @ToolParam(description = "EDAM concept IDs for operations.") @Nullable List<Integer> operations,
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
    query.setMinStartDate(
      minStartDate != null && !minStartDate.isEmpty() ? LocalDate.parse(minStartDate) : null
    );
    query.setMaxStartDate(
      maxStartDate != null && !maxStartDate.isEmpty() ? LocalDate.parse(maxStartDate) : null
    );
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
