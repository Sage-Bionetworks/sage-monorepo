package org.sagebionetworks.openchallenges.mcp.server.service;

import io.micrometer.common.lang.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class EdamConceptService {

  private final EdamConceptApi edamConceptApi;

  @Tool(
    name = "list_edam_concepts",
    description = """
    Search EDAM ontology concepts (data, format, operation, topic, identifier).

    When to use: User mentions specific input/training data types or needs standardized terminology.

    Strategy:
    - Extract the core scientific term: "MRI imaging data" -> "MRI"; "protein structural data" -> "protein structure".
    - Prefer singular concise forms ("image" not "images").
    - Avoid generic words (data, dataset, information, file).
    - If first attempt empty: try controlled synonyms (MRI -> magnetic resonance, CT -> computed tomography, DNA -> nucleotide sequence).
    - To target data input concepts only set sections=["data"].
    - After 2–3 unsuccessful variations, report no close EDAM match and optionally point user to BioPortal.

    Examples:
    - "Need MRI training data" -> searchTerms="MRI", sections=[data].
    - "Genomics datasets" -> searchTerms="genome", sections=[data].
    - "Protein structure operations" -> searchTerms="protein structure", sections=[operation] (if focusing on operations).
     """
  )
  public EdamConceptsPage listEdamConcepts(
    @ToolParam(
      description = "Page index (integer >= 0). First page is 0."
    ) @Nullable Integer pageNumber,
    @ToolParam(
      description = "Page size (integer 1–200). Default 100 if null."
    ) @Nullable Integer pageSize,
    @ToolParam(
      description = "Sort enum: preferred_label|relevance."
    ) @Nullable EdamConceptSort sort,
    @ToolParam(
      description = "Sort direction enum: asc|desc."
    ) @Nullable EdamConceptDirection direction,
    @ToolParam(description = "Concept ID integers list (exact match).") @Nullable List<Integer> ids,
    @ToolParam(description = "Free-text search (concise core term).") @Nullable String searchTerms,
    @ToolParam(
      description = "Section enums list: data|format|identifier|operation|topic."
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
