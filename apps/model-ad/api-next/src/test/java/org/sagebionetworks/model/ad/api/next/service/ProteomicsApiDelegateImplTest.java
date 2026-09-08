package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.api.ProteomicsApiDelegateImpl;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;
import org.sagebionetworks.model.ad.api.next.model.document.FoldChangeResult;
import org.sagebionetworks.model.ad.api.next.model.document.Link;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.FoldChangeMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.LinkMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.ProteomicsMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ProteomicsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class ProteomicsApiDelegateImplTest {

  private static final String PROTEOMICS_CATEGORY = "PROTEIN - DIFFERENTIAL EXPRESSION";
  private static final String TISSUE_HEMIBRAIN = "Hemibrain";
  private static final String TISSUE_CORTEX = "Cortex";
  private static final String UNIQUE_ID = "ENSMUSG00000000001P27144";
  private static final String COMPOSITE_ID = UNIQUE_ID + "~LOAD2~Female";

  @Mock
  private ProteomicsRepository repository;

  private ProteomicsApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    // Mock the request context for validation
    MockHttpServletRequest request = new MockHttpServletRequest();
    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    ProteomicsService queryService = new ProteomicsService(
      repository,
      new ProteomicsMapper(new LinkMapper(), new FoldChangeMapper())
    );
    delegate = new ProteomicsApiDelegateImpl(queryService);
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  @DisplayName("should throw bad request when categories array has less than 2 values")
  void shouldThrowBadRequestWhenCategoriesArrayHasLessThan2Values() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .categories(List.of(PROTEOMICS_CATEGORY))
      .build();

    assertThatThrownBy(() -> delegate.getProteomics(query))
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessageContaining("Expected at least 2 category values");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw bad request when main category is the transcriptomics label")
  void shouldThrowBadRequestWhenMainCategoryIsTranscriptomicsLabel() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .categories(List.of("RNA - DIFFERENTIAL EXPRESSION", "Tissue - Hemibrain"))
      .build();

    assertThatThrownBy(() -> delegate.getProteomics(query))
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessageContaining("Invalid main category");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw bad request when tissue is missing the Tissue prefix")
  void shouldThrowBadRequestWhenTissueIsMissingTissuePrefix() {
    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .categories(List.of(PROTEOMICS_CATEGORY, "InvalidFormat"))
      .build();

    assertThatThrownBy(() -> delegate.getProteomics(query))
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessageContaining("Invalid tissue format");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    stubRepository(new PageImpl<>(List.of()), TISSUE_HEMIBRAIN);

    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .categories(List.of(PROTEOMICS_CATEGORY, "Tissue - Hemibrain"))
      .items(List.of())
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(10)
      .build();

    ResponseEntity<ProteomicsPageDto> response = delegate.getProteomics(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ProteomicsPageDto body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getProteomics()).isEmpty();
    assertThat(body.getPage().getTotalElements()).isZero();

    assertResponseHeaders(response.getHeaders());
  }

  @Test
  @DisplayName("should return mapped results when items provided")
  void shouldReturnMappedResultsWhenItemsProvided() {
    stubRepository(new PageImpl<>(List.of(buildDocument())), TISSUE_HEMIBRAIN);

    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .categories(List.of(PROTEOMICS_CATEGORY, "Tissue - Hemibrain"))
      .items(List.of(COMPOSITE_ID))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(10)
      .build();

    ResponseEntity<ProteomicsPageDto> response = delegate.getProteomics(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    ProteomicsPageDto body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getProteomics()).hasSize(1);
    assertThat(body.getPage().getTotalElements()).isEqualTo(1);

    var dto = body.getProteomics().get(0);
    assertThat(dto.getCompositeId()).isEqualTo(COMPOSITE_ID);
    assertThat(dto.getUniqueId()).isEqualTo(UNIQUE_ID);
    assertThat(dto.getUniprotid()).isEqualTo("P27144");
    assertThat(dto.getDisplaySymbol()).isEqualTo("Gnai3 (P27144)");
    assertThat(dto.getName()).isNotNull();
    assertThat(dto.getName().getLinkText()).isEqualTo("LOAD2");
    assertThat(dto.getTissue()).isEqualTo(TISSUE_HEMIBRAIN);
    assertThat(dto.getSex().getValue()).isEqualTo("Female");
    assertThat(dto.get24months()).isNotNull();
    assertThat(dto.get24months().getLog2Fc()).isEqualTo(BigDecimal.valueOf(0.01167d));

    verify(repository).findAll(
      any(Pageable.class),
      any(ProteomicsSearchQueryDto.class),
      anyList(),
      eq(TISSUE_HEMIBRAIN)
    );
  }

  @Test
  @DisplayName("should pass the requested tissue through to the repository")
  void shouldPassRequestedTissueThroughToRepository() {
    stubRepository(new PageImpl<>(List.of(buildDocument())), TISSUE_CORTEX);

    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .categories(List.of(PROTEOMICS_CATEGORY, "Tissue - Cortex"))
      .items(List.of())
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(10)
      .build();

    ResponseEntity<ProteomicsPageDto> response = delegate.getProteomics(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    verify(repository).findAll(
      any(Pageable.class),
      any(ProteomicsSearchQueryDto.class),
      anyList(),
      eq(TISSUE_CORTEX)
    );
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when invalid query parameter provided")
  void shouldThrowExceptionWhenInvalidQueryParameterProvided() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("invalidField", "someValue");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    ProteomicsSearchQueryDto query = ProteomicsSearchQueryDto.builder()
      .categories(List.of(PROTEOMICS_CATEGORY, "Tissue - Hemibrain"))
      .pageNumber(0)
      .pageSize(100)
      .build();

    assertThatThrownBy(() -> delegate.getProteomics(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Unknown query parameter: invalidField");
  }

  private void stubRepository(Page<ProteomicsDocument> page, String tissue) {
    when(
      repository.findAll(
        any(Pageable.class),
        any(ProteomicsSearchQueryDto.class),
        anyList(),
        eq(tissue)
      )
    ).thenReturn(page);
  }

  private void assertResponseHeaders(HttpHeaders headers) {
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
    assertThat(headers.getPragma()).contains("no-cache");
    assertThat(headers.getExpires()).isZero();
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
  }

  private ProteomicsDocument buildDocument() {
    ProteomicsDocument document = new ProteomicsDocument();
    document.setId(new ObjectId());
    document.setEnsemblGeneId("ENSMUSG00000000001");
    document.setGeneSymbol("Gnai3");
    document.setUniprotid("P27144");
    document.setUniqueId(UNIQUE_ID);
    document.setDisplaySymbol("Gnai3 (P27144)");
    document.setBiodomains(List.of("Apoptosis", "Synapse"));
    document.setName(Link.builder().linkText("LOAD2").linkUrl("models/LOAD2").build());
    document.setMatchedControl("C57BL/6J");
    document.setModelType("Late Onset AD");
    document.setTissue(TISSUE_HEMIBRAIN);
    document.setSex("Female");
    document.setTwentyFourMonths(
      FoldChangeResult.builder().log2Fc(0.01167d).adjPVal(0.7812d).build()
    );
    return document;
  }
}
