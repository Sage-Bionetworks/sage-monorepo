package org.sagebionetworks.model.ad.api.next.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.exception.DiseaseCorrelationNotFoundException;
import org.sagebionetworks.model.ad.api.next.exception.GlobalExceptionHandler;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;
import org.sagebionetworks.model.ad.api.next.exception.InvalidObjectIdException;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
      .setControllerAdvice(new GlobalExceptionHandler())
      .setConversionService(conversionService)
      .build();
  }

  @Test
  @DisplayName("should return bad request problem when category has empty subcategory")
  void shouldReturnBadRequestProblemWhenCategoryHasEmptySubcategory() throws Exception {
    when(delegate.getDiseaseCorrelations(any())).thenThrow(
      new InvalidCategoryException(
        "Query parameter category must repeat twice (e.g. ?category=CONSENSUS NETWORK MODULES" +
        "&category=subcategory) and each value must be a string"
      )
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/disease-correlation")
          .param("category", "CONSENSUS NETWORK MODULES")
          .param("category", "")
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Invalid Category"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(
        jsonPath("$.detail").value(
          "Query parameter category must repeat twice (e.g. ?category=CONSENSUS NETWORK MODULES" +
          "&category=subcategory) and each value must be a string"
        )
      )
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/disease-correlation"));
  }

  @Test
  @DisplayName("should return bad request problem when category unsupported")
  void shouldReturnBadRequestProblemWhenCategoryUnsupported() throws Exception {
    when(delegate.getDiseaseCorrelations(any())).thenThrow(
      new InvalidCategoryException("OTHER", "CONSENSUS NETWORK MODULES")
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/disease-correlation")
          .param("category", "OTHER")
          .param("category", "Cluster A")
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Invalid Category"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(
        jsonPath("$.detail").value(
          "Category 'OTHER' is not supported. Only 'CONSENSUS NETWORK MODULES' is supported"
        )
      )
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/disease-correlation"));
  }

  @Test
  @DisplayName("should return bad request problem when delegate raises InvalidObjectIdException")
  void shouldReturnBadRequestProblemWhenDelegateRaisesInvalidObjectIdException() throws Exception {
    when(delegate.getDiseaseCorrelations(any())).thenThrow(
      new InvalidObjectIdException("not-an-id")
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
      .andExpect(jsonPath("$.title").value("Invalid Request Parameter"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.detail").value("Invalid ObjectId format: not-an-id"))
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/disease-correlation"));
  }

  @Test
  @DisplayName("should return not found problem when disease correlation not found")
  void shouldReturnNotFoundProblemWhenDelegateRaisesDiseaseCorrelationNotFoundException()
    throws Exception {
    String correlationId = "673f5d8e8c1a2b3c4d5e6f7a";
    when(delegate.getDiseaseCorrelations(any())).thenThrow(
      new DiseaseCorrelationNotFoundException(correlationId)
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/disease-correlation")
          .param("category", "CONSENSUS NETWORK MODULES")
          .param("category", "Cluster A")
          .param("item", correlationId)
      )
      .andExpect(status().isNotFound())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Entity not found"))
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(
        jsonPath("$.detail").value("Disease correlation not found with id: " + correlationId)
      )
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/disease-correlation"));
  }

  @Test
  @DisplayName("should return not found problem with cluster when disease correlation not found")
  void shouldReturnNotFoundProblemWhenDiseaseCorrelationNotFoundWithCluster() throws Exception {
    String cluster = "Cluster A";
    String correlationId = "673f5d8e8c1a2b3c4d5e6f7a";
    when(delegate.getDiseaseCorrelations(any())).thenThrow(
      new DiseaseCorrelationNotFoundException(cluster, correlationId)
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/disease-correlation")
          .param("category", "CONSENSUS NETWORK MODULES")
          .param("category", cluster)
          .param("item", correlationId)
      )
      .andExpect(status().isNotFound())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Entity not found"))
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(
        jsonPath("$.detail").value(
          "Disease correlation not found with cluster: " + cluster + ", id: " + correlationId
        )
      )
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/disease-correlation"));
  }
}
