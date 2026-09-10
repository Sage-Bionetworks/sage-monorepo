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
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsSearchQueryDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ProteomicsApiControllerWebTest {

  private static final String PROTEOMICS_PATH = "/v1/comparison-tools/proteomics";
  private static final String PROTEOMICS_CATEGORY = "PROTEIN - DIFFERENTIAL EXPRESSION";

  private ProteomicsApiDelegate delegate;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    delegate = mock(ProteomicsApiDelegate.class);
    var controller = new ProteomicsApiController(delegate);
    var conversionService = new FormattingConversionService();
    conversionService.addConverter(
      new Converter<String, ItemFilterTypeQueryDto>() {
        @Override
        public ItemFilterTypeQueryDto convert(String source) {
          return ItemFilterTypeQueryDto.fromValue(source);
        }
      }
    );
    conversionService.addConverter(
      new Converter<String, ProteomicsSearchQueryDto.SortOrdersEnum>() {
        @Override
        public ProteomicsSearchQueryDto.SortOrdersEnum convert(String source) {
          return ProteomicsSearchQueryDto.SortOrdersEnum.fromValue(Integer.parseInt(source));
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
    when(delegate.getProteomics(any())).thenThrow(
      new InvalidCategoryException("Tissue category must start with 'Tissue - ' prefix")
    );

    mockMvc
      .perform(
        get(PROTEOMICS_PATH)
          .param("categories", PROTEOMICS_CATEGORY)
          .param("categories", "InvalidTissue")
          .param("sortFields", "display_symbol")
          .param("sortOrders", "1")
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Invalid Category"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.detail").value("Tissue category must start with 'Tissue - ' prefix"))
      .andExpect(jsonPath("$.instance").value(PROTEOMICS_PATH));
  }

  @Test
  @DisplayName("should return bad request problem when main category unsupported")
  void shouldReturnBadRequestProblemWhenMainCategoryUnsupported() throws Exception {
    when(delegate.getProteomics(any())).thenThrow(
      new InvalidCategoryException("RNA - DIFFERENTIAL EXPRESSION", PROTEOMICS_CATEGORY)
    );

    mockMvc
      .perform(
        get(PROTEOMICS_PATH)
          .param("categories", "RNA - DIFFERENTIAL EXPRESSION")
          .param("categories", "Tissue - Hemibrain")
          .param("sortFields", "display_symbol")
          .param("sortOrders", "1")
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Invalid Category"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(
        jsonPath("$.detail").value(
          "Category 'RNA - DIFFERENTIAL EXPRESSION' is not supported. Only " +
          "'PROTEIN - DIFFERENTIAL EXPRESSION' is supported"
        )
      )
      .andExpect(jsonPath("$.instance").value(PROTEOMICS_PATH));
  }

  @Test
  @DisplayName("should return bad request problem when an item is not a valid composite id")
  void shouldReturnBadRequestProblemWhenItemIsNotValidCompositeId() throws Exception {
    when(delegate.getProteomics(any())).thenThrow(
      new InvalidFilterException("Invalid composite identifier: 'not-an-id'")
    );

    mockMvc
      .perform(
        get(PROTEOMICS_PATH)
          .param("categories", PROTEOMICS_CATEGORY)
          .param("categories", "Tissue - Hemibrain")
          .param("items", "not-an-id")
          .param("sortFields", "display_symbol")
          .param("sortOrders", "1")
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.instance").value(PROTEOMICS_PATH));
  }

  @Test
  @DisplayName("should reject a non-integer sortOrders value")
  void shouldRejectNonIntegerSortOrdersValue() throws Exception {
    mockMvc
      .perform(
        get(PROTEOMICS_PATH)
          .param("categories", PROTEOMICS_CATEGORY)
          .param("categories", "Tissue - Hemibrain")
          .param("sortFields", "display_symbol")
          .param("sortOrders", "ascending")
      )
      .andExpect(status().isBadRequest());
  }
}
