package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.model.document.SearchResultDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SearchResultDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.SearchResultMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ModelSearchRepository;

@ExtendWith(MockitoExtension.class)
class ModelSearchServiceTest {

  private static final String QUERY = "apoe";
  private static final List<ModelOrganismDto> ALL_ORGANISMS = List.of(ModelOrganismDto.values());

  @Mock
  private ModelSearchRepository modelSearchRepository;

  private ModelSearchService service;

  @BeforeEach
  void setUp() {
    service = new ModelSearchService(modelSearchRepository, new SearchResultMapper());
  }

  @Test
  @DisplayName("should return mapped results when query is valid")
  void shouldReturnMappedResultsWhenQueryIsValid() {
    when(modelSearchRepository.searchModels(QUERY, ALL_ORGANISMS)).thenReturn(
      List.of(buildDocument("APOE4", ModelOrganismDto.MOUSE))
    );

    List<SearchResultDto> results = service.searchModels(QUERY, ALL_ORGANISMS);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo("APOE4");
    assertThat(results.get(0).getModelOrganism()).isEqualTo(ModelOrganismDto.MOUSE);
  }

  private SearchResultDocument buildDocument(String id, ModelOrganismDto organism) {
    SearchResultDocument document = new SearchResultDocument();
    document.setId(id);
    document.setMatchField("name");
    document.setMatchValue(id);
    document.setModelOrganism(organism.getValue());
    document.setPrecedence(1);
    return document;
  }
}
