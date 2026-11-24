package org.sagebionetworks.qtl.api.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sagebionetworks.qtl.api.exception.EqtlPhenotypeNotFoundException;
import org.sagebionetworks.qtl.api.exception.GlobalExceptionHandler;
import org.sagebionetworks.qtl.api.model.dto.EqtlPhenotypeDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlPhenotypeQueryDto;
import org.sagebionetworks.qtl.api.service.EqtlPhenotypeService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class EqtlPhenotypeApiControllerWebTest {

  private static final String EQTL_PHENOTYPE_PATH = "/v1/eqtl-phenotype";
  private static final String VARIANT = "rs2120461";
  private static final String ENSEMBL_GENE_ID = "ENSG00000142599";
  private static final String CELL_TYPE_ID = "astro";

  private EqtlPhenotypeService service;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    service = mock(EqtlPhenotypeService.class);
    var controller = new EqtlPhenotypeApiController(new EqtlPhenotypeApiDelegateImpl(service));
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setControllerAdvice(new GlobalExceptionHandler())
      .build();
  }

  @Test
  @DisplayName("should return ok with the phenotype and no-cache headers for the requested eQTL")
  void shouldReturnOkWithThePhenotypeAndNoCacheHeadersForTheRequestedEqtl() throws Exception {
    // given
    when(service.getEqtlPhenotype(any())).thenReturn(
      EqtlPhenotypeDto.builder()
        .variant(VARIANT)
        .ensemblGeneId(ENSEMBL_GENE_ID)
        .cellTypeId(CELL_TYPE_ID)
        .build()
    );

    // when
    mockMvc
      .perform(eqtlPhenotypeRequest())
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
      .andExpect(jsonPath("$.variant").value(VARIANT))
      .andExpect(jsonPath("$.ensembl_gene_id").value(ENSEMBL_GENE_ID))
      .andExpect(jsonPath("$.cell_type_id").value(CELL_TYPE_ID));

    // then
    ArgumentCaptor<EqtlPhenotypeQueryDto> captor = ArgumentCaptor.forClass(
      EqtlPhenotypeQueryDto.class
    );
    verify(service).getEqtlPhenotype(captor.capture());
    assertThat(captor.getValue()).isEqualTo(
      EqtlPhenotypeQueryDto.builder()
        .variant(VARIANT)
        .ensemblGeneId(ENSEMBL_GENE_ID)
        .cellTypeId(CELL_TYPE_ID)
        .build()
    );
  }

  @Test
  @DisplayName("should return not found problem when no phenotype exists for the eQTL")
  void shouldReturnNotFoundProblemWhenNoPhenotypeExistsForTheEqtl() throws Exception {
    // given
    when(service.getEqtlPhenotype(any())).thenThrow(
      new EqtlPhenotypeNotFoundException(VARIANT, ENSEMBL_GENE_ID, CELL_TYPE_ID)
    );

    // when & then
    mockMvc
      .perform(eqtlPhenotypeRequest())
      .andExpect(status().isNotFound())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Entity not found"))
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(
        jsonPath("$.detail").value(
          "eQTL phenotype not found for variant: rs2120461, ensemblGeneId: ENSG00000142599, " +
          "cellTypeId: astro"
        )
      )
      .andExpect(jsonPath("$.instance").value(EQTL_PHENOTYPE_PATH));
  }

  @Test
  @DisplayName("should return bad request problem when a query param is unknown")
  void shouldReturnBadRequestProblemWhenAQueryParamIsUnknown() throws Exception {
    mockMvc
      .perform(eqtlPhenotypeRequest().param("gene", ENSEMBL_GENE_ID))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.detail").value("Unknown query parameter: gene"));

    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("should return bad request problem when a query param is missing")
  void shouldReturnBadRequestProblemWhenAQueryParamIsMissing() throws Exception {
    mockMvc
      .perform(
        get(EQTL_PHENOTYPE_PATH).param("variant", VARIANT).param("ensemblGeneId", ENSEMBL_GENE_ID)
      )
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

    verifyNoInteractions(service);
  }

  private static MockHttpServletRequestBuilder eqtlPhenotypeRequest() {
    return get(EQTL_PHENOTYPE_PATH)
      .param("variant", VARIANT)
      .param("ensemblGeneId", ENSEMBL_GENE_ID)
      .param("cellTypeId", CELL_TYPE_ID);
  }
}
