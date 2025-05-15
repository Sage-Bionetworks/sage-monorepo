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
    Searches for EDAM (EMBRACE Data and Methods), an ontology for bioinformatics data types,
    formats, operations, and topics.

    Usage guidelines:
    - Use this tool when the user refers to a specific type of input data or bioinformatics concept.
      1. Extract relevant keywords from the user’s prompt to match EDAM concepts
      (e.g., "MRI Imaging Data" → "MRI Image").
        - If no matches are found, the keywords may not align with standard EDAM ontology terms.
      2. Call `list_edam_concepts` using those keywords in the `searchTerms` parameter.
      3. Set `sections = ["data"]` to restrict the search to EDAM data-related concepts only.
    """
  )
  public EdamConceptsPage listEdamConcepts(
    @ToolParam(
      description = "The page number to retrieve. The first page is 0."
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
