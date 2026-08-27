package org.sagebionetworks.model.ad.api.next.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
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
  private SearchApiController controller;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    delegate = mock(SearchApiDelegate.class);
    controller = new SearchApiController(delegate);
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
  @DisplayName("should return bad request when delegate throws illegal argument")
  void shouldReturnBadRequestWhenDelegateThrowsIllegalArgument() throws Exception {
    when(delegate.searchModels(eq("apoe"), any())).thenThrow(
      new IllegalArgumentException("invalid search request")
    );

    mockMvc.perform(get(SEARCH_MODELS_PATH).param("q", "apoe")).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should return bad request when query violates the schema length constraint")
  void shouldReturnBadRequestWhenQueryViolatesLengthConstraint() throws Exception {
    when(delegate.searchModels(eq("apoe"), any())).thenThrow(buildEmptyQueryViolation());

    mockMvc
      .perform(get(SEARCH_MODELS_PATH).param("q", "apoe"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.title").value("Bad Request"))
      .andExpect(
        jsonPath("$.detail").value("Query parameter q must be between 1 and 100 characters")
      );
  }

  /** Validates the generated {@code searchModels} signature so the constraint isn't restated here. */
  private ConstraintViolationException buildEmptyQueryViolation() throws NoSuchMethodException {
    Method searchModels = SearchApi.class.getMethod("searchModels", String.class, List.class);
    Set<ConstraintViolation<SearchApiController>> violations =
      Validation.buildDefaultValidatorFactory()
        .getValidator()
        .forExecutables()
        .validateParameters(controller, searchModels, new Object[] { "", null });
    return new ConstraintViolationException(violations);
  }
}
