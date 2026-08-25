package org.sagebionetworks.model.ad.api.next.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.exception.GlobalExceptionHandler;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SearchResultDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class SearchApiControllerWebTest {

  private static final String SEARCH_MODELS_PATH = "/v1/search/models";

  private SearchApiDelegate delegate;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    delegate = mock(SearchApiDelegate.class);
    var controller = new SearchApiController(delegate);
    var conversionService = new FormattingConversionService();
    conversionService.addConverter(
      new Converter<String, ModelOrganismDto>() {
        @Override
        public ModelOrganismDto convert(String source) {
          return ModelOrganismDto.fromValue(source);
        }
      }
    );
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setControllerAdvice(new GlobalExceptionHandler())
      .setConversionService(conversionService)
      .build();
  }

  @Test
  @DisplayName("should delegate and return ok when query is provided")
  void shouldDelegateAndReturnOkWhenQueryIsProvided() throws Exception {
    SearchResultDto result = SearchResultDto.builder()
      .id("APOE4")
      .matchField("name")
      .matchValue("APOE4")
      .modelOrganism(ModelOrganismDto.MOUSE)
      .build();
    when(delegate.searchModels(eq("apoe"), any())).thenReturn(ResponseEntity.ok(List.of(result)));

    mockMvc
      .perform(get(SEARCH_MODELS_PATH).param("q", "apoe"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id").value("APOE4"))
      .andExpect(jsonPath("$[0].model_organism").value("mouse"));
  }

  @Test
  @DisplayName("should return bad request when query is missing")
  void shouldReturnBadRequestWhenQueryIsMissing() throws Exception {
    mockMvc.perform(get(SEARCH_MODELS_PATH)).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should return bad request when organism is unknown")
  void shouldReturnBadRequestWhenOrganismIsUnknown() throws Exception {
    mockMvc
      .perform(get(SEARCH_MODELS_PATH).param("q", "apoe").param("modelOrganisms", "rat"))
      .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should return bad request when delegate rejects the query length")
  void shouldReturnBadRequestWhenDelegateRejectsQueryLength() throws Exception {
    when(delegate.searchModels(eq(""), any())).thenThrow(
      new IllegalArgumentException("Query must be between 1 and 100 characters")
    );

    mockMvc.perform(get(SEARCH_MODELS_PATH).param("q", "")).andExpect(status().isBadRequest());
  }
}
