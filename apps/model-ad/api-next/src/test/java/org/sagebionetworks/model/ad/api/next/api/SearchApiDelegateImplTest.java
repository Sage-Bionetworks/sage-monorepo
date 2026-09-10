package org.sagebionetworks.model.ad.api.next.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SearchResultDto;
import org.sagebionetworks.model.ad.api.next.service.ModelSearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SearchApiDelegateImplTest {

  private static final String QUERY = "apoe";

  @Mock
  private ModelSearchService modelSearchService;

  private SearchApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    delegate = new SearchApiDelegateImpl(modelSearchService);
  }

  @Test
  @DisplayName("should default to all organisms when param is null")
  void shouldDefaultToAllOrganismsWhenParamIsNull() {
    SearchResultDto result = buildResult();
    when(modelSearchService.searchModels(eq(QUERY), anyList())).thenReturn(List.of(result));

    ResponseEntity<List<SearchResultDto>> response = delegate.searchModels(QUERY, null);

    assertThat(capturedOrganisms())
      .as("must equal .sorted() output so the omitted and explicit forms share one cache key")
      .containsExactly(ModelOrganismDto.MARMOSET, ModelOrganismDto.MOUSE);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).containsExactly(result);
    assertThat(response.getHeaders().getCacheControl())
      .isNotNull()
      .contains("no-cache")
      .contains("no-store")
      .contains("must-revalidate");
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
  }

  @Test
  @DisplayName("should default to all organisms when param is empty")
  void shouldDefaultToAllOrganismsWhenParamIsEmpty() {
    when(modelSearchService.searchModels(eq(QUERY), anyList())).thenReturn(List.of());

    delegate.searchModels(QUERY, List.of());

    assertThat(capturedOrganisms())
      .as("an empty param must behave exactly like an omitted one")
      .containsExactly(ModelOrganismDto.MARMOSET, ModelOrganismDto.MOUSE);
  }

  @Test
  @DisplayName("should deduplicate and sort organisms when param has duplicates")
  void shouldDeduplicateAndSortOrganismsWhenParamHasDuplicates() {
    when(modelSearchService.searchModels(eq(QUERY), anyList())).thenReturn(List.of());

    delegate.searchModels(
      QUERY,
      List.of(ModelOrganismDto.MOUSE, ModelOrganismDto.MOUSE, ModelOrganismDto.MARMOSET)
    );

    assertThat(capturedOrganisms())
      .as("normalizing above the cache boundary collapses redundant keys")
      .containsExactly(ModelOrganismDto.MARMOSET, ModelOrganismDto.MOUSE);
  }

  private List<ModelOrganismDto> capturedOrganisms() {
    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<ModelOrganismDto>> organismsCaptor = ArgumentCaptor.forClass(List.class);
    verify(modelSearchService).searchModels(eq(QUERY), organismsCaptor.capture());
    return organismsCaptor.getValue();
  }

  private SearchResultDto buildResult() {
    return SearchResultDto.builder()
      .id("APOE4")
      .matchField("name")
      .matchValue("APOE4")
      .modelOrganism(ModelOrganismDto.MOUSE)
      .build();
  }
}
