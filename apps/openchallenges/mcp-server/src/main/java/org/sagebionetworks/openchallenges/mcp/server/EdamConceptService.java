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
    Searches for EDAM, an ontology for bioinformatics data types, formats, operations, and topics.

    Usage guidelines:
    - Use this tool when the user refers to a specific type of input data or training dataset.
    - EDAM uses standardized terminology. Start with SHORT, CORE terms and avoid extra descriptive words:

    Search Strategy:
    1. Extract the CORE scientific/technical term from user input:
       - "MRI imaging data" → use "MRI" or "MRI image"
       - "genomic sequence data" → use "sequence" or "genome"
       - "protein structure information" → use "protein structure"
       - "CT scan images" → use "CT" or "CT image"

    2. If no results with core terms, try slight variations:
       - "MRI" → try "magnetic resonance"
       - "CT" → try "computed tomography"
       - "DNA" → try "nucleotide sequence"

    3. Avoid adding generic words like "data", "information", "dataset", "file" to search terms
    4. Use singular forms when possible ("image" not "images")
    5. Set `sections = ["data"]` to restrict search to EDAM data-related concepts only
    6. If no matches found after trying variations**:
    - Inform the user that their terminology may not exist in EDAM ontology (data section)
    - Direct them to browse available terms at: https://bioportal.bioontology.org/ontologies/EDAM?p=classes
    - Suggest they find the closest matching EDAM data concept and retry the search

    Example searches:
    - User: "MRI imaging data" → searchTerms: "MRI"
    - User: "protein structural data" → searchTerms: "protein structure"
    - User: "genomics datasets" → searchTerms: "genome"
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
    @ToolParam(description = "List of EDAM concept IDs.") @Nullable List<Integer> ids,
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
