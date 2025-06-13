package org.sagebionetworks.openchallenges.mcp.server;

import java.util.List;
import java.util.stream.Collectors;
import org.sagebionetworks.openchallenges.api.client.api.ChallengeApi;
import org.sagebionetworks.openchallenges.api.client.api.EdamConceptApi;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeDirection;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.ChallengeSort;
import org.sagebionetworks.openchallenges.api.client.model.ChallengesPage;
import org.sagebionetworks.openchallenges.api.client.model.EdamConceptDirection;
import org.sagebionetworks.openchallenges.api.client.model.EdamConceptSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.EdamConceptSort;
import org.sagebionetworks.openchallenges.api.client.model.EdamConceptsPage;
import org.sagebionetworks.openchallenges.api.client.model.EdamSection;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class ChallengesByDataTypeService {

  private final ChallengeApi challengeApi;
  private final EdamConceptApi edamConceptApi;

  public ChallengesByDataTypeService(ChallengeApi challengeApi, EdamConceptApi edamConceptApi) {
    this.challengeApi = challengeApi;
    this.edamConceptApi = edamConceptApi;
  }

  @Tool(
    name = "find_challenges_by_data_type",
    description = """
    Finds challenges that use specific types of input data or training datasets by first searching
    EDAM ontology concepts and then filtering challenges accordingly.

    Use this tool when:
    - User mentions specific data types (e.g., "MRI imaging data", "genomic sequences", "protein structures")
    - User asks for challenges with particular input datasets
    - User wants to find challenges in a specific domain based on data modality
    - User describes the type of data they want to work with

    This tool automatically:
    1. Searches EDAM concepts to find matching data type IDs
    2. Uses those IDs to filter relevant challenges
    3. Returns both the EDAM concepts found and the matching challenges

    Examples of when to use:
    - "Find challenges using MRI data"
    - "Are there any genomics challenges?"
    - "Show me challenges with protein structure data"
    - "I want to work with medical imaging datasets"
    """
  )
  public ChallengesPage findChallengesByDataType(
    @ToolParam(
      description = "Keywords describing the data type (e.g., 'MRI imaging', 'protein structure', 'genomic sequence')"
    ) String dataTypeKeywords
  ) {
    // Set predefined defaults for optimal results
    int pageSize = 20;
    ChallengeSort sort = ChallengeSort.RELEVANCE;

    // Step 1: Search EDAM concepts for the data type
    EdamConceptSearchQuery edamQuery = new EdamConceptSearchQuery();
    edamQuery.setPageNumber(0);
    edamQuery.setPageSize(1000); // Get more EDAM concepts to find best matches
    edamQuery.setSort(EdamConceptSort.RELEVANCE);
    edamQuery.setDirection(EdamConceptDirection.DESC);
    edamQuery.setSearchTerms(dataTypeKeywords);
    edamQuery.setSections(List.of(EdamSection.DATA)); // Focus on data types only

    EdamConceptsPage edamResults = edamConceptApi.listEdamConcepts(edamQuery);

    // Step 2: Extract EDAM concept IDs
    List<Long> edamIds = edamResults
      .getEdamConcepts()
      .stream()
      .map(concept -> concept.getId())
      .collect(Collectors.toList());

    ChallengesPage challengeResults = null;

    // Step 3: Search challenges using the EDAM IDs (only if we found matching concepts)
    if (!edamIds.isEmpty()) {
      ChallengeSearchQuery challengeQuery = new ChallengeSearchQuery();
      challengeQuery.setPageNumber(0);
      challengeQuery.setPageSize(pageSize);
      challengeQuery.setSort(sort);
      challengeQuery.setDirection(ChallengeDirection.DESC);
      challengeQuery.setStatus(null); // All statuses
      challengeQuery.setSubmissionTypes(null); // All submission types
      challengeQuery.setInputDataTypes(edamIds);

      challengeResults = challengeApi.listChallenges(challengeQuery);
    } else {
      // If no EDAM concepts found, return empty challenge results but still show the EDAM search attempt
      challengeResults = new ChallengesPage();
      challengeResults.setChallenges(List.of());
      challengeResults.setTotalElements(0L);
    }

    return challengeResults;
  }
}
