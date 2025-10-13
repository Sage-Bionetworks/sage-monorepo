package org.sagebionetworks.openchallenges.mcp.server.service;

import io.micrometer.common.lang.Nullable;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ChallengeService {

  private final ChallengeApi challengeApi;

  @Tool(
    name = "list_challenges",
    description = """
    Retrieve challenges filtered / sorted by user‑relevant criteria.

    Usage:
    - Pass only filters the user explicitly asks for or clearly implies.
    - Add minStartDate / maxStartDate ONLY if the user gives a time window.
    - If user mentions Docker/containers -> include submissionTypes contains "container_image" (alongside others if appropriate).
    - For data type filtering: (1) call list_edam_concepts(searchTerms=keywords, sections=["data"]) (2) collect returned concept IDs (3) call this tool with inputDataTypes=[ids].
    - For organization filtering: (1) call list_organizations(searchTerms=name/acronym) (2) take its id (3) set organizations=[id].
    - Search terms: keep short, distinctive scientific keywords; drop filler words.
    - If no results: relax or remove least critical filters and retry up to 3 attempts before returning empty.

    Examples:
    - "Show upcoming container image challenges" -> status=[upcoming], submissionTypes includes container_image.
    - "Challenges from DREAM about MRI" -> organizations=[<DREAM id>] after lookup; inputDataTypes from MRI EDAM lookup.
    - "Active benchmarking challenges" -> status=[active], categories may include benchmark.
    """
  )
  public ChallengesPage listChallenges(
    @ToolParam(
      description = "Page index (integer >= 0). First page is 0."
    ) @Nullable Integer pageNumber,
    @ToolParam(
      description = "Page size (integer 1–200). Default server value if null."
    ) @Nullable Integer pageSize,
    @ToolParam(
      description = "Sort field enum (created|random|relevance|starred|start_date|end_date). 'starred' = descending star count"
    ) @Nullable ChallengeSort sort,
    @ToolParam(
      description = "Seed (integer) required only when sort=random to make results reproducible."
    ) @Nullable Integer sortSeed,
    @ToolParam(
      description = "Sort direction enum (asc|desc)."
    ) @Nullable ChallengeDirection direction,
    @ToolParam(
      description = "Incentive type enums: monetary|publication|speaking_engagement|other. Provide list to OR-combine."
    ) @Nullable List<ChallengeIncentive> incentives,
    @ToolParam(
      description = "Earliest start date (string yyyy-MM-dd). Use only if user specifies lower bound."
    ) @Nullable String minStartDate,
    @ToolParam(
      description = "Latest start date (string yyyy-MM-dd). Use only if user specifies upper bound."
    ) @Nullable String maxStartDate,
    @ToolParam(
      description = "Platform slugs list (e.g. ['synapse','openchallenges'])."
    ) @Nullable List<String> platforms,
    @ToolParam(
      description = "Organization IDs list (long). Obtained via list_organizations."
    ) @Nullable List<Long> organizations,
    @ToolParam(description = "Status enums: upcoming|active|completed.") @Nullable List<
      ChallengeStatus
    > status,
    @ToolParam(
      description = "Submission type enums: container_image|prediction_file|notebook|mlcube|other."
    ) @Nullable List<ChallengeSubmissionType> submissionTypes,
    @ToolParam(
      description = "EDAM data concept ID integers (from list_edam_concepts)."
    ) @Nullable List<Integer> inputDataTypes,
    @ToolParam(description = "EDAM operation concept ID integers.") @Nullable List<
      Integer
    > operations,
    @ToolParam(
      description = "Category enums: featured|benchmark|hackathon|starting_soon|ending_soon|recently_started|recently_ended."
    ) @Nullable List<ChallengeCategory> categories,
    @ToolParam(
      description = "Free-text search (short distinctive keywords)."
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
