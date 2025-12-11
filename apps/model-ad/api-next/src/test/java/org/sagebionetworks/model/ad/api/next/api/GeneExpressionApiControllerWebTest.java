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
import org.sagebionetworks.model.ad.api.next.exception.GlobalExceptionHandler;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;
import org.sagebionetworks.model.ad.api.next.exception.InvalidObjectIdException;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

class GeneExpressionApiControllerWebTest {

  private GeneExpressionApiDelegate delegate;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    delegate = mock(GeneExpressionApiDelegate.class);
    var controller = new GeneExpressionApiController(delegate);
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
  @DisplayName("should return bad request problem when category has invalid tissue format")
  void shouldReturnBadRequestProblemWhenCategoryHasInvalidTissueFormat() throws Exception {
    when(delegate.getGeneExpressions(any())).thenThrow(
      new InvalidCategoryException("Tissue category must start with 'Tissue - ' prefix")
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/gene-expression")
          .param("categories", "RNA - DIFFERENTIAL EXPRESSION")
          .param("categories", "InvalidTissue")
          .param("categories", "Sex - Females & Males")
          .param("sortFields", "gene_symbol")
          .param("sortOrders", "1")
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Invalid Category"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.detail").value("Tissue category must start with 'Tissue - ' prefix"))
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/gene-expression"));
  }

  @Test
  @DisplayName("should return bad request problem when category has invalid sex cohort format")
  void shouldReturnBadRequestProblemWhenCategoryHasInvalidSexCohortFormat() throws Exception {
    when(delegate.getGeneExpressions(any())).thenThrow(
      new InvalidCategoryException("Sex cohort category must start with 'Sex - ' prefix")
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/gene-expression")
          .param("categories", "RNA - DIFFERENTIAL EXPRESSION")
          .param("categories", "Tissue - Hemibrain")
          .param("categories", "InvalidSexCohort")
          .param("sortFields", "gene_symbol")
          .param("sortOrders", "1")
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Invalid Category"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.detail").value("Sex cohort category must start with 'Sex - ' prefix"))
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/gene-expression"));
  }

  @Test
  @DisplayName("should return bad request problem when main category unsupported")
  void shouldReturnBadRequestProblemWhenMainCategoryUnsupported() throws Exception {
    when(delegate.getGeneExpressions(any())).thenThrow(
      new InvalidCategoryException("OTHER", "RNA - DIFFERENTIAL EXPRESSION")
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/gene-expression")
          .param("categories", "OTHER")
          .param("categories", "Tissue - Hemibrain")
          .param("categories", "Sex - Females & Males")
          .param("sortFields", "gene_symbol")
          .param("sortOrders", "1")
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Invalid Category"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(
        jsonPath("$.detail").value(
          "Category 'OTHER' is not supported. Only 'RNA - DIFFERENTIAL EXPRESSION' is supported"
        )
      )
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/gene-expression"));
  }

  @Test
  @DisplayName("should return bad request problem when delegate raises InvalidObjectIdException")
  void shouldReturnBadRequestProblemWhenDelegateRaisesInvalidObjectIdException() throws Exception {
    when(delegate.getGeneExpressions(any())).thenThrow(new InvalidObjectIdException("not-an-id"));

    mockMvc
      .perform(
        get("/v1/comparison-tools/gene-expression")
          .param("categories", "RNA - DIFFERENTIAL EXPRESSION")
          .param("categories", "Tissue - Hemibrain")
          .param("categories", "Sex - Females & Males")
          .param("item", "not-an-id")
          .param("sortFields", "gene_symbol")
          .param("sortOrders", "1")
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Invalid Request Parameter"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.detail").value("Invalid ObjectId format: not-an-id"))
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/gene-expression"));
  }

  @Test
  @DisplayName("should return not found problem when gene expression not found")
  void shouldReturnNotFoundProblemWhenGeneExpressionNotFound() throws Exception {
    String geneExpressionId = "673f5d8e8c1a2b3c4d5e6f7a";
    when(delegate.getGeneExpressions(any())).thenThrow(
      new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Gene expression not found with id: " + geneExpressionId
      )
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/gene-expression")
          .param("categories", "RNA - DIFFERENTIAL EXPRESSION")
          .param("categories", "Tissue - Hemibrain")
          .param("categories", "Sex - Females & Males")
          .param("item", geneExpressionId)
          .param("sortFields", "gene_symbol")
          .param("sortOrders", "1")
      )
      .andExpect(status().isNotFound());
  }
}
