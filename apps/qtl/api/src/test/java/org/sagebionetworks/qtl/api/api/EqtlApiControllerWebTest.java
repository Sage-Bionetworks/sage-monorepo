package org.sagebionetworks.qtl.api.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.explorers.SortOrdersConverterFactory;
import org.sagebionetworks.qtl.api.exception.DataIntegrityException;
import org.sagebionetworks.qtl.api.exception.GlobalExceptionHandler;
import org.sagebionetworks.qtl.api.exception.InvalidFilterException;
import org.sagebionetworks.qtl.api.model.dto.EqtlDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlSearchQueryDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlsPageDto;
import org.sagebionetworks.qtl.api.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.qtl.api.model.dto.PageMetadataDto;
import org.sagebionetworks.qtl.api.service.EqtlService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class EqtlApiControllerWebTest {

  private static final String EQTL_PATH = "/v1/comparison-tools/eqtl";
  private static final String COMPOSITE_ID = "rs170094~ENSG00000136267~astro";

  private EqtlService service;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    service = mock(EqtlService.class);
    var controller = new EqtlApiController(new EqtlApiDelegateImpl(service));
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
      String.class,
      EqtlSearchQueryDto.SortOrdersEnum.class,
      SortOrdersConverterFactory.from(EqtlSearchQueryDto.SortOrdersEnum::fromValue)
    );
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setControllerAdvice(new GlobalExceptionHandler())
      .setConversionService(conversionService)
      .build();
  }

  @Test
  @DisplayName("should return ok with the page and no-cache headers when the query is valid")
  void shouldReturnOkWithThePageAndNoCacheHeadersWhenTheQueryIsValid() throws Exception {
    // given
    when(service.loadEqtls(any())).thenReturn(
      EqtlsPageDto.builder()
        .eqtls(List.of(EqtlDto.builder().compositeId(COMPOSITE_ID).build()))
        .page(
          PageMetadataDto.builder()
            .number(0)
            .size(100)
            .totalElements(381L)
            .totalPages(4)
            .hasNext(true)
            .hasPrevious(false)
            .build()
        )
        .build()
    );

    // when
    ResultActions result = mockMvc.perform(
      get(EQTL_PATH)
        .param("itemFilterType", "exclude")
        .param("cellClass", "Astrocyte")
        .param("sortFields", "adj_p_val")
        .param("sortOrders", "1")
    );

    // then
    result
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
      .andExpect(jsonPath("$.eqtls[0].composite_id").value(COMPOSITE_ID))
      .andExpect(jsonPath("$.page.totalElements").value(381));
  }

  @Test
  @DisplayName("should return bad request problem when a query param is unknown")
  void shouldReturnBadRequestProblemWhenAQueryParamIsUnknown() throws Exception {
    // when
    ResultActions result = mockMvc.perform(sortedRequest().param("class", "Astrocyte"));

    // then
    expectProblem(result, 400, "Bad Request").andExpect(
      jsonPath("$.detail").value("Unknown query parameter: class")
    );
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("should return invalid filter problem when a pin is malformed")
  void shouldReturnInvalidFilterProblemWhenAPinIsMalformed() throws Exception {
    // given
    String detail = "Invalid composite identifier format: 'rs1~ENSG1~astro~x'";
    when(service.loadEqtls(any())).thenThrow(new InvalidFilterException(detail));

    // when
    ResultActions result = mockMvc.perform(sortedRequest().param("items", "rs1~ENSG1~astro~x"));

    // then
    expectProblem(result, 400, "Invalid Filter").andExpect(jsonPath("$.detail").value(detail));
  }

  @Test
  @DisplayName("should return bad request when a sort order is not 1 or -1")
  void shouldReturnBadRequestWhenASortOrderIsNotOneOrMinusOne() throws Exception {
    // when
    ResultActions result = mockMvc.perform(
      get(EQTL_PATH).param("sortFields", "adj_p_val").param("sortOrders", "2")
    );

    // then
    result.andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("should return bad request when the sort fields are missing")
  void shouldReturnBadRequestWhenTheSortFieldsAreMissing() throws Exception {
    // when
    ResultActions result = mockMvc.perform(get(EQTL_PATH).param("sortOrders", "1"));

    // then
    result.andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("should return bad request when the page size is above the maximum")
  void shouldReturnBadRequestWhenThePageSizeIsAboveTheMaximum() throws Exception {
    // when
    ResultActions result = mockMvc.perform(sortedRequest().param("pageSize", "500"));

    // then
    result.andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("should return data integrity problem when a stored row is invalid")
  void shouldReturnDataIntegrityProblemWhenAStoredRowIsInvalid() throws Exception {
    // given
    String detail = "Unexpected effect_direction value 'Sideways' in eQTL record";
    when(service.loadEqtls(any())).thenThrow(new DataIntegrityException(detail));

    // when
    ResultActions result = mockMvc.perform(sortedRequest());

    // then
    expectProblem(result, 500, "Data Integrity Error").andExpect(
      jsonPath("$.detail").value(detail)
    );
  }

  @Test
  @DisplayName("should return internal server error problem when an unexpected error occurs")
  void shouldReturnInternalServerErrorProblemWhenAnUnexpectedErrorOccurs() throws Exception {
    // given
    when(service.loadEqtls(any())).thenThrow(new IllegalStateException("database unavailable"));

    // when
    ResultActions result = mockMvc.perform(sortedRequest());

    // then
    expectProblem(result, 500, "Internal Server Error").andExpect(
      jsonPath("$.detail").value("An unexpected error occurred")
    );
  }

  private static MockHttpServletRequestBuilder sortedRequest() {
    return get(EQTL_PATH).param("sortFields", "adj_p_val").param("sortOrders", "1");
  }

  private static ResultActions expectProblem(ResultActions result, int status, String title)
    throws Exception {
    return result
      .andExpect(status().is(status))
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value(title))
      .andExpect(jsonPath("$.status").value(status))
      .andExpect(jsonPath("$.instance").value(EQTL_PATH));
  }
}
