package org.sagebionetworks.qtl.api.api;

import static org.mockito.Mockito.mock;
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
import org.sagebionetworks.qtl.api.exception.ComparisonToolConfigNotFoundException;
import org.sagebionetworks.qtl.api.exception.DataIntegrityException;
import org.sagebionetworks.qtl.api.exception.GlobalExceptionHandler;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolPageDto;
import org.sagebionetworks.qtl.api.service.ComparisonToolConfigService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ComparisonToolConfigApiControllerWebTest {

  private static final String CONFIG_PATH = "/v1/comparison-tools/config";
  private static final String EQTL_PAGE = "eQTLs";

  private ComparisonToolConfigService service;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    service = mock(ComparisonToolConfigService.class);
    var controller = new ComparisonToolConfigApiController(
      new ComparisonToolConfigApiDelegateImpl(service)
    );
    var conversionService = new FormattingConversionService();
    conversionService.addConverter(
      new Converter<String, ComparisonToolPageDto>() {
        @Override
        public ComparisonToolPageDto convert(String source) {
          return ComparisonToolPageDto.fromValue(source);
        }
      }
    );
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setControllerAdvice(new GlobalExceptionHandler())
      .setConversionService(conversionService)
      .build();
  }

  @Test
  @DisplayName("should return ok with the config and no-cache headers when the page exists")
  void shouldReturnOkWithTheConfigAndNoCacheHeadersWhenThePageExists() throws Exception {
    ComparisonToolConfigDto config = new ComparisonToolConfigDto();
    config.setPage(ComparisonToolPageDto.E_QTLS);
    when(service.getConfigsByPage(ComparisonToolPageDto.E_QTLS)).thenReturn(List.of(config));

    mockMvc
      .perform(get(CONFIG_PATH).param("page", EQTL_PAGE))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
      .andExpect(jsonPath("$[0].page").value(EQTL_PAGE));
  }

  @Test
  @DisplayName("should return bad request problem when the page is unknown")
  void shouldReturnBadRequestProblemWhenThePageIsUnknown() throws Exception {
    mockMvc
      .perform(get(CONFIG_PATH).param("page", "Bogus"))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Bad Request"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.detail").value("Query parameter page has invalid value 'Bogus'"))
      .andExpect(jsonPath("$.instance").value(CONFIG_PATH));
  }

  @Test
  @DisplayName("should return bad request problem when the page is missing")
  void shouldReturnBadRequestProblemWhenThePageIsMissing() throws Exception {
    mockMvc
      .perform(get(CONFIG_PATH))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Bad Request"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.detail").value("Query parameter page is required"))
      .andExpect(jsonPath("$.instance").value(CONFIG_PATH));
  }

  @Test
  @DisplayName("should return not found problem when no config exists for the page")
  void shouldReturnNotFoundProblemWhenNoConfigExistsForThePage() throws Exception {
    when(service.getConfigsByPage(ComparisonToolPageDto.E_QTLS)).thenThrow(
      new ComparisonToolConfigNotFoundException(EQTL_PAGE)
    );

    mockMvc
      .perform(get(CONFIG_PATH).param("page", EQTL_PAGE))
      .andExpect(status().isNotFound())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Entity not found"))
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.detail").value("Comparison Tool config not found for page: eQTLs"))
      .andExpect(jsonPath("$.instance").value(CONFIG_PATH));
  }

  @Test
  @DisplayName("should return data integrity problem when the stored config is invalid")
  void shouldReturnDataIntegrityProblemWhenTheStoredConfigIsInvalid() throws Exception {
    String detail = "Unexpected column type value 'string' in comparison tool config record";
    when(service.getConfigsByPage(ComparisonToolPageDto.E_QTLS)).thenThrow(
      new DataIntegrityException(detail)
    );

    mockMvc
      .perform(get(CONFIG_PATH).param("page", EQTL_PAGE))
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Data Integrity Error"))
      .andExpect(jsonPath("$.status").value(500))
      .andExpect(jsonPath("$.detail").value(detail))
      .andExpect(jsonPath("$.instance").value(CONFIG_PATH));
  }

  @Test
  @DisplayName("should return internal server error problem when an unexpected error occurs")
  void shouldReturnInternalServerErrorProblemWhenAnUnexpectedErrorOccurs() throws Exception {
    when(service.getConfigsByPage(ComparisonToolPageDto.E_QTLS)).thenThrow(
      new IllegalStateException("database unavailable")
    );

    mockMvc
      .perform(get(CONFIG_PATH).param("page", EQTL_PAGE))
      .andExpect(status().isInternalServerError())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Internal Server Error"))
      .andExpect(jsonPath("$.status").value(500))
      .andExpect(jsonPath("$.detail").value("An unexpected error occurred"))
      .andExpect(jsonPath("$.instance").value(CONFIG_PATH));
  }
}
