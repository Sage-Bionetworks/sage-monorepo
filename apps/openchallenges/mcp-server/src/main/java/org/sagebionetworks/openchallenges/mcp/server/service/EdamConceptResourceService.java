package org.sagebionetworks.openchallenges.mcp.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.openchallenges.api.client.api.EdamConceptApi;
import org.sagebionetworks.openchallenges.api.client.model.EdamConceptSearchQuery;
import org.sagebionetworks.openchallenges.api.client.model.EdamConceptsPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
@Configuration
@RequiredArgsConstructor
public class EdamConceptResourceService {

  private final EdamConceptApi edamConceptApi;
  private final ObjectMapper objectMapper;

  @Bean
  public List<McpServerFeatures.SyncResourceSpecification> edamConceptResources() {
    var edamConceptResource = McpSchema.Resource.builder()
      // 'uri' is required by the MCP schema builder; using same value as name for simplicity.
      // Consider a more structured scheme later, e.g. 'edam:concepts' or 'edam://concepts'.
      .uri("list_edam_concepts")
      .name("list_edam_concepts")
      .description(
        "List EDAM ontology for bioinformatics data types, formats, operations, and topics"
      )
      .mimeType("application/json")
      .build();

    var resourceSpecification = new McpServerFeatures.SyncResourceSpecification(
      edamConceptResource,
      (exchange, request) -> {
        try {
          EdamConceptSearchQuery query = new EdamConceptSearchQuery();
          EdamConceptsPage result = edamConceptApi.listEdamConcepts(query);
          String jsonContent = objectMapper.writeValueAsString(result);

          return new McpSchema.ReadResourceResult(
            List.of(
              new McpSchema.TextResourceContents(request.uri(), "application/json", jsonContent)
            )
          );
        } catch (Exception e) {
          throw new RuntimeException("Failed to retrieve EDAM concepts: " + e.getMessage(), e);
        }
      }
    );

    return List.of(resourceSpecification);
  }

  @Bean
  public List<McpSchema.ResourceTemplate> edamConceptResourceTemplates() {
    // Builder API for ResourceTemplate not present in MCP SDK 0.14.0, using constructor.
    var template = new McpSchema.ResourceTemplate(
      "list_edam_concepts?searchTerms={searchTerms}&sections={sections}&pageSize={pageSize}&ids={ids}",
      "list_edam_concepts_template",
      "Parameterized EDAM concepts listing. Parameters: searchTerms (text query), sections (data|format|operation|topic), pageSize (limit results), ids (specific concept IDs).",
      "application/json",
      null
    );
    return List.of(template);
  }
}
