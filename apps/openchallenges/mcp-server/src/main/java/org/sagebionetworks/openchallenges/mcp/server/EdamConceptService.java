package org.sagebionetworks.openchallenges.mcp.server;

import io.micrometer.common.lang.Nullable;
import java.util.List;
import org.sagebionetworks.openchallenges.api.client.api.EdamConceptApi;
import org.sagebionetworks.openchallenges.api.client.model.EdamConceptDirection;
import org.sagebionetworks.openchallenges.api.client.model.EdamConceptSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.EdamConceptSort;
import org.sagebionetworks.openchallenges.api.client.model.EdamConceptsPage;
import org.sagebionetworks.openchallenges.api.client.model.EdamSection;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class EdamConceptService {

  private final EdamConceptApi edamConceptApi;

  public EdamConceptService(EdamConceptApi edamConceptApi) {
    this.edamConceptApi = edamConceptApi;
  }

  @Tool(
    name = "list_edam_concepts",
    description = """
      Lists EDAM concepts that can be filtered and sorted based on a variety of criteria.
      If a parameter is not specified, its default value should be used.
      This tool can be used to find input data types for challenges by:
        - Simplify the user's keywords to its core concept (e.g., "RNA sequencing data" → "RNA sequence")
        - Searching with specific keywords (e.g., "genomic", "clinical")
          using the `searchTerms` parameter
        - Filtering by EDAM section (e.g., data, format) using the `sections` parameter
      Once you identify relevant EDAM concept IDs from the results, use them as `inputDataTypes`
      in the `list_challenges` tool to search challenges by input type.
    """
  )
  public EdamConceptsPage listEdamConcepts(
    @ToolParam(
      description = "The page number to retrieve. Default is 0."
    ) @Nullable Integer pageNumber,
    @ToolParam(
      description = "The number of items per page. Default is 100."
    ) @Nullable Integer pageSize,
    @ToolParam(
      description = "Sort field: preferred_label, relevance."
    ) @Nullable EdamConceptSort sort,
    @ToolParam(description = "Sort direction: asc, desc.") @Nullable EdamConceptDirection direction,
    @ToolParam(description = "List of EDAM concept IDs.") @Nullable List<Long> ids,
    @ToolParam(
      description = "Free-text search terms to match concept names or descriptions."
    ) @Nullable String searchTerms,
    @ToolParam(
      description = "EDAM sections to filter by: data, format, identifier, operation, topic."
    ) @Nullable List<EdamSection> sections
  ) {
    EdamConceptSearchQuery query = new EdamConceptSearchQuery();
    query.setPageNumber(pageNumber);
    query.setPageSize(pageSize);
    query.setSort(sort);
    query.setDirection(direction);
    query.setIds(ids);
    query.setSearchTerms(searchTerms);
    query.setSections(sections);

    return edamConceptApi.listEdamConcepts(query);
  }
}
