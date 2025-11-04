package org.sagebionetworks.model.ad.api.next.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.util.ComparisonToolApiHelper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

class DiseaseCorrelationApiControllerWebTest {

  private DiseaseCorrelationApiDelegate delegate;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    delegate = mock(DiseaseCorrelationApiDelegate.class);
    var controller = new DiseaseCorrelationApiController(delegate);
    var conversionService = new FormattingConversionService();
    conversionService.addConverter(
      new Converter<String, ItemFilterTypeQueryDto>() {
        @Override
        public ItemFilterTypeQueryDto convert(String source) {
          return ItemFilterTypeQueryDto.fromValue(source);
        }
      }
    );
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setControllerAdvice(new ComparisonToolApiExceptionHandler())
      .setConversionService(conversionService)
      .build();
  }

  @Test
  @DisplayName("should return bad request problem when category missing")
  void shouldReturnBadRequestProblemWhenCategoryMissing() throws Exception {
    mockMvc
      .perform(get("/v1/comparison-tools/disease-correlation"))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Bad Request"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.detail").value(ComparisonToolApiHelper.CATEGORY_REQUIREMENT_MESSAGE))
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/disease-correlation"));
  }

  @Test
  @DisplayName("should return bad request problem when itemFilterType invalid")
  void shouldReturnBadRequestProblemWhenItemFilterTypeInvalid() throws Exception {
    mockMvc
      .perform(
        get("/v1/comparison-tools/disease-correlation")
          .param("category", "CONSENSUS NETWORK MODULES")
          .param("category", "Cluster A")
          .param("itemFilterType", "not-real")
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Bad Request"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(
        jsonPath("$.detail").value(
          "Query parameter itemFilterType must be either 'include' or 'exclude' if provided"
        )
      )
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/disease-correlation"));
  }

  @Test
  @DisplayName("should return bad request problem when delegate raises ResponseStatusException")
  void shouldReturnBadRequestProblemWhenDelegateRaisesResponseStatusException() throws Exception {
    when(delegate.getDiseaseCorrelations(anyList(), any(), any())).thenThrow(
      new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Query parameter item must contain valid ObjectId values"
      )
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/disease-correlation")
          .param("category", "CONSENSUS NETWORK MODULES")
          .param("category", "Cluster A")
          .param("item", "not-an-id")
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Bad Request"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(
        jsonPath("$.detail").value("Query parameter item must contain valid ObjectId values")
      )
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/disease-correlation"));
  }
}
