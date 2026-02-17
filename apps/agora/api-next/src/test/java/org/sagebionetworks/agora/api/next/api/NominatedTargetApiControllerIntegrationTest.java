package org.sagebionetworks.agora.api.next.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.agora.api.next.configuration.EnumConverterConfiguration;
import org.sagebionetworks.agora.api.next.configuration.IntegerEnumConverterConfiguration;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetsPageDto;
import org.sagebionetworks.agora.api.next.model.dto.PageMetadataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

/**
 * Integration test for NominatedTargetApiController that uses the actual Spring context
 * with real converter configurations. This ensures that all required converters are properly
 * configured in the application.
 *
 * <p>Unlike the standalone web test, this test imports the actual converter configurations
 * rather than manually creating them. If a converter is missing from production code,
 * this test will fail with a 400 Bad Request error.
 */
@WebMvcTest(NominatedTargetApiController.class)
@Import({ EnumConverterConfiguration.class, IntegerEnumConverterConfiguration.class })
@Tag("integration")
class NominatedTargetApiControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private NominatedTargetApiDelegate delegate;

  @Test
  @DisplayName("should successfully bind sortOrders integer enum from query parameters")
  void shouldSuccessfullyBindSortOrdersIntegerEnumFromQueryParameters() throws Exception {
    NominatedTargetsPageDto pageDto = buildEmptyPageDto();
    when(delegate.getNominatedTargets(any())).thenReturn(ResponseEntity.ok(pageDto));

    mockMvc
      .perform(
        get("/v1/comparison-tools/nominated-target")
          .param("sortFields", "hgnc_symbol")
          .param("sortOrders", "1")
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.nominatedTargets").isArray());
  }

  @Test
  @DisplayName("should successfully bind negative sortOrders from query parameters")
  void shouldSuccessfullyBindNegativeSortOrdersFromQueryParameters() throws Exception {
    NominatedTargetsPageDto pageDto = buildEmptyPageDto();
    when(delegate.getNominatedTargets(any())).thenReturn(ResponseEntity.ok(pageDto));

    mockMvc
      .perform(
        get("/v1/comparison-tools/nominated-target")
          .param("sortFields", "total_nominations")
          .param("sortOrders", "-1")
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.nominatedTargets").isArray());
  }

  @Test
  @DisplayName("should successfully bind itemFilterType string enum from query parameters")
  void shouldSuccessfullyBindItemFilterTypeStringEnumFromQueryParameters() throws Exception {
    NominatedTargetsPageDto pageDto = buildEmptyPageDto();
    when(delegate.getNominatedTargets(any())).thenReturn(ResponseEntity.ok(pageDto));

    mockMvc
      .perform(
        get("/v1/comparison-tools/nominated-target")
          .param("itemFilterType", "exclude")
          .param("sortFields", "hgnc_symbol")
          .param("sortOrders", "1")
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.nominatedTargets").isArray());
  }

  @Test
  @DisplayName("should successfully bind multiple sort fields and orders")
  void shouldSuccessfullyBindMultipleSortFieldsAndOrders() throws Exception {
    NominatedTargetsPageDto pageDto = buildEmptyPageDto();
    when(delegate.getNominatedTargets(any())).thenReturn(ResponseEntity.ok(pageDto));

    mockMvc
      .perform(
        get("/v1/comparison-tools/nominated-target")
          .param("sortFields", "total_nominations", "hgnc_symbol")
          .param("sortOrders", "-1", "1")
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.nominatedTargets").isArray());
  }

  @Test
  @DisplayName("should successfully bind all query parameters together")
  void shouldSuccessfullyBindAllQueryParametersTogether() throws Exception {
    NominatedTargetsPageDto pageDto = buildPageDtoWithResults();
    when(delegate.getNominatedTargets(any())).thenReturn(ResponseEntity.ok(pageDto));

    mockMvc
      .perform(
        get("/v1/comparison-tools/nominated-target")
          .param("pageNumber", "0")
          .param("pageSize", "20")
          .param("items", "APOE", "TREM2")
          .param("itemFilterType", "include")
          .param("cohortStudies", "ROSMAP")
          .param("programs", "AMP-AD")
          .param("sortFields", "total_nominations")
          .param("sortOrders", "-1")
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.nominatedTargets").isArray())
      .andExpect(jsonPath("$.nominatedTargets[0].hgnc_symbol").value("APOE"));
  }

  @Test
  @DisplayName("should return not found when delegate throws not found exception")
  void shouldReturnNotFoundWhenDelegateThrowsNotFoundException() throws Exception {
    when(delegate.getNominatedTargets(any())).thenThrow(
      new ResponseStatusException(HttpStatus.NOT_FOUND, "Nominated target not found")
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/nominated-target")
          .param("sortFields", "hgnc_symbol")
          .param("sortOrders", "1")
          .accept(MediaType.APPLICATION_JSON)
      )
      .andExpect(status().isNotFound());
  }

  private NominatedTargetsPageDto buildEmptyPageDto() {
    return new NominatedTargetsPageDto()
      .nominatedTargets(List.of())
      .page(
        new PageMetadataDto()
          .number(0)
          .size(100)
          .totalElements(0L)
          .totalPages(0)
          .hasNext(false)
          .hasPrevious(false)
      );
  }

  private NominatedTargetsPageDto buildPageDtoWithResults() {
    NominatedTargetDto target = new NominatedTargetDto()
      .hgncSymbol("APOE")
      .ensemblGeneId("ENSG00000130203")
      .totalNominations(5)
      .initialNomination(2021)
      .nominatingTeams(List.of("Duke"))
      .cohortStudies(List.of("ROSMAP"))
      .inputData(List.of("RNA"))
      .programs(List.of("AMP-AD"))
      .pharosClass("Tbio");

    return new NominatedTargetsPageDto()
      .nominatedTargets(List.of(target))
      .page(
        new PageMetadataDto()
          .number(0)
          .size(100)
          .totalElements(1L)
          .totalPages(1)
          .hasNext(false)
          .hasPrevious(false)
      );
  }
}
