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
import org.sagebionetworks.model.ad.api.next.api.GeneExpressionApiDelegateImpl;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument.FoldChangeResult;
import org.sagebionetworks.model.ad.api.next.model.document.Link;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.GeneExpressionMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.LinkMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.GeneExpressionRepository;
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
class GeneExpressionApiDelegateImplTest {

  private static final String TISSUE_HEMIBRAIN = "Hemibrain";
  private static final String TISSUE_CORTEX = "Cortex";
  private static final String SEX_COHORT_FEMALES = "Females";
  private static final String SEX_COHORT_MALES = "Males";
  private static final String SEX_COHORT_FEMALES_AND_MALES = "Females & Males";

  @Mock
  private GeneExpressionRepository repository;

  private GeneExpressionApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    // Mock the request context for validation
    MockHttpServletRequest request = new MockHttpServletRequest();
    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    GeneExpressionService queryService = new GeneExpressionService(
      repository,
      new GeneExpressionMapper(new LinkMapper())
    );
    delegate = new GeneExpressionApiDelegateImpl(queryService);
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  @DisplayName("should throw bad request when categories array has less than 3 values")
  void shouldThrowBadRequestWhenCategoriesStringHasLessThan3Values() {
    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .categories(List.of("RNA - DIFFERENTIAL EXPRESSION", "Tissue - Hemibrain"))
      .build();

    assertThatThrownBy(() -> delegate.getGeneExpressions(query))
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessageContaining("Expected at least 3 category values");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw bad request when main category unsupported")
  void shouldThrowBadRequestWhenMainCategoryUnsupported() {
    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .categories(List.of("OTHER", "Tissue - Hemibrain", "Sex - Females"))
      .build();

    assertThatThrownBy(() -> delegate.getGeneExpressions(query))
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessageContaining("Invalid main category");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw bad request when tissue format invalid")
  void shouldThrowBadRequestWhenTissueFormatInvalid() {
    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .categories(List.of("RNA - DIFFERENTIAL EXPRESSION", "InvalidFormat", "Sex - Females"))
      .build();

    assertThatThrownBy(() -> delegate.getGeneExpressions(query))
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessageContaining("Invalid tissue format");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should throw bad request when sex cohort format invalid")
  void shouldThrowBadRequestWhenSexFormatInvalid() {
    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .categories(List.of("RNA - DIFFERENTIAL EXPRESSION", "Tissue - Hemibrain", "InvalidFormat"))
      .build();

    assertThatThrownBy(() -> delegate.getGeneExpressions(query))
      .isInstanceOf(InvalidCategoryException.class)
      .hasMessageContaining("Invalid sex_cohort format");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of());
    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        eq(List.of()),
        eq(TISSUE_HEMIBRAIN),
        eq(SEX_COHORT_FEMALES)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .categories(List.of("RNA - DIFFERENTIAL EXPRESSION", "Tissue - Hemibrain", "Sex - Females"))
      .items(List.of())
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(10)
      .build();

    ResponseEntity<GeneExpressionsPageDto> response = delegate.getGeneExpressions(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    GeneExpressionsPageDto body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getGeneExpressions()).isEmpty();
    assertThat(body.getPage().getTotalElements()).isZero();

    assertResponseHeaders(response.getHeaders());
    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      eq(List.of()),
      eq(TISSUE_HEMIBRAIN),
      eq(SEX_COHORT_FEMALES)
    );
  }

  @Test
  @DisplayName("should return mapped results when items provided")
  void shouldReturnMappedResultsWhenItemsProvided() {
    ObjectId objectId = new ObjectId();
    GeneExpressionDocument document = buildDocument(objectId);
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(document));

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        anyList(),
        eq(TISSUE_HEMIBRAIN),
        eq(SEX_COHORT_FEMALES)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .categories(List.of("RNA - DIFFERENTIAL EXPRESSION", "Tissue - Hemibrain", "Sex - Females"))
      .items(List.of("ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(10)
      .build();

    ResponseEntity<GeneExpressionsPageDto> response = delegate.getGeneExpressions(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    GeneExpressionsPageDto body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getGeneExpressions()).hasSize(1);
    assertThat(body.getPage().getTotalElements()).isEqualTo(1);

    var dto = body.getGeneExpressions().get(0);
    assertThat(dto.getCompositeId()).contains("ENSMUSG00000000001");
    assertThat(dto.getEnsemblGeneId()).isEqualTo("ENSMUSG00000000001");
    assertThat(dto.getGeneSymbol()).isEqualTo("Gnai3");
    assertThat(dto.getName()).isNotNull();
    assertThat(dto.getName().getLinkText()).isEqualTo("5xFAD (Jax/IU/Pitt)");
    assertThat(dto.getTissue()).isEqualTo("Hemibrain");
    assertThat(dto.get4months()).isNotNull();
    assertThat(dto.get4months().getLog2Fc()).isEqualTo(BigDecimal.valueOf(0.01167d));
    assertThat(dto.getSexCohort().getValue()).isEqualTo("Females");

    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      anyList(),
      eq(TISSUE_HEMIBRAIN),
      eq(SEX_COHORT_FEMALES)
    );
  }

  @Test
  @DisplayName("should include tissue and sex cohort filter when exclude filter has no items")
  void shouldIncludeTissueAndSexFilterWhenExcludeFilterHasNoItems() {
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(buildDocument(new ObjectId())));
    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        eq(List.of()),
        eq(TISSUE_CORTEX),
        eq(SEX_COHORT_MALES)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .categories(List.of("RNA - DIFFERENTIAL EXPRESSION", "Tissue - Cortex", "Sex - Males"))
      .items(List.of())
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(10)
      .build();

    ResponseEntity<GeneExpressionsPageDto> response = delegate.getGeneExpressions(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getGeneExpressions()).hasSize(1);

    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      eq(List.of()),
      eq(TISSUE_CORTEX),
      eq(SEX_COHORT_MALES)
    );
  }

  @Test
  @DisplayName("should omit fold change data when values incomplete")
  void shouldOmitFoldChangeDataWhenValuesIncomplete() {
    ObjectId objectId = new ObjectId();
    Page<GeneExpressionDocument> page = new PageImpl<>(
      List.of(buildDocumentWithPartialFoldChange(objectId))
    );

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        anyList(),
        eq(TISSUE_HEMIBRAIN),
        eq(SEX_COHORT_FEMALES)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .categories(List.of("RNA - DIFFERENTIAL EXPRESSION", "Tissue - Hemibrain", "Sex - Females"))
      .items(List.of("ENSMUSG00000000002~APOE4"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(10)
      .build();

    ResponseEntity<GeneExpressionsPageDto> response = delegate.getGeneExpressions(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getGeneExpressions()).hasSize(1);
    assertThat(response.getBody().getGeneExpressions().get(0).get4months()).isNull();
  }

  @Test
  @DisplayName("should exclude specified items when exclude filter has items")
  void shouldExcludeSpecifiedItemsWhenExcludeFilterHasItems() {
    ObjectId includedId = new ObjectId();
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(buildDocument(includedId)));

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        anyList(),
        eq(TISSUE_HEMIBRAIN),
        eq(SEX_COHORT_FEMALES)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .categories(List.of("RNA - DIFFERENTIAL EXPRESSION", "Tissue - Hemibrain", "Sex - Females"))
      .items(List.of("ENSMUSG00000099999~ExcludedModel"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(10)
      .build();

    ResponseEntity<GeneExpressionsPageDto> response = delegate.getGeneExpressions(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    GeneExpressionsPageDto body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getGeneExpressions()).hasSize(1);
    assertThat(body.getGeneExpressions().get(0).getCompositeId()).contains("ENSMUSG00000000001");

    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      anyList(),
      eq(TISSUE_HEMIBRAIN),
      eq(SEX_COHORT_FEMALES)
    );
  }

  @Test
  @DisplayName("should handle multiple biodomains")
  void shouldHandleMultipleBiodomains() {
    ObjectId objectId = new ObjectId();
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(buildDocument(objectId)));

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        anyList(),
        eq(TISSUE_HEMIBRAIN),
        eq(SEX_COHORT_FEMALES)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .categories(List.of("RNA - DIFFERENTIAL EXPRESSION", "Tissue - Hemibrain", "Sex - Females"))
      .items(List.of("ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(10)
      .build();

    ResponseEntity<GeneExpressionsPageDto> response = delegate.getGeneExpressions(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getGeneExpressions()).hasSize(1);

    var dto = response.getBody().getGeneExpressions().get(0);
    assertThat(dto.getBiodomains()).containsExactlyInAnyOrder("Apoptosis", "Autophagy", "Synapse");
  }

  @Test
  @DisplayName("should handle compound sex cohort values like Females & Males")
  void shouldHandleCompoundSexValues() {
    ObjectId objectId = new ObjectId();
    Page<GeneExpressionDocument> page = new PageImpl<>(List.of(buildDocument(objectId)));

    when(
      repository.findAll(
        any(Pageable.class),
        any(GeneExpressionSearchQueryDto.class),
        anyList(),
        eq(TISSUE_HEMIBRAIN),
        eq(SEX_COHORT_FEMALES_AND_MALES)
      )
    ).thenReturn(page);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .categories(
        List.of("RNA - DIFFERENTIAL EXPRESSION", "Tissue - Hemibrain", "Sex - Females & Males")
      )
      .items(List.of("ENSMUSG00000000001~5xFAD (Jax/IU/Pitt)"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(10)
      .build();

    ResponseEntity<GeneExpressionsPageDto> response = delegate.getGeneExpressions(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    verify(repository).findAll(
      any(Pageable.class),
      any(GeneExpressionSearchQueryDto.class),
      anyList(),
      eq(TISSUE_HEMIBRAIN),
      eq(SEX_COHORT_FEMALES_AND_MALES)
    );
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when invalid query parameter provided")
  void shouldThrowExceptionWhenInvalidQueryParameterProvided() {
    // Setup request with invalid parameter
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("invalidField", "someValue");
    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    GeneExpressionSearchQueryDto query = GeneExpressionSearchQueryDto.builder()
      .categories(List.of("RNA - DIFFERENTIAL EXPRESSION", "Tissue - Hemibrain", "Sex - Females"))
      .pageNumber(0)
      .pageSize(100)
      .build();

    // Should throw IllegalArgumentException for invalid field
    assertThatThrownBy(() -> delegate.getGeneExpressions(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Unknown query parameter: invalidField");
  }

  private void assertResponseHeaders(HttpHeaders headers) {
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
    assertThat(headers.getPragma()).contains("no-cache");
    assertThat(headers.getExpires()).isZero();
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
  }

  private GeneExpressionDocument buildDocument(ObjectId objectId) {
    FoldChangeResult foldChange = FoldChangeResult.builder()
      .log2Fc(0.01167d)
      .adjPVal(0.7812d)
      .build();

    GeneExpressionDocument document = new GeneExpressionDocument();
    document.setId(objectId);
    document.setEnsemblGeneId("ENSMUSG00000000001");
    document.setGeneSymbol("Gnai3");
    document.setBiodomains(List.of("Apoptosis", "Autophagy", "Synapse"));
    document.setName(Link.builder().linkText("5xFAD (Jax/IU/Pitt)").linkUrl("").build());
    document.setMatchedControl("C57BL/6J");
    document.setModelGroup(null);
    document.setModelType("Familial AD");
    document.setTissue("Hemibrain");
    document.setSexCohort("Females");
    document.setFourMonths(foldChange);
    return document;
  }

  private GeneExpressionDocument buildDocumentWithPartialFoldChange(ObjectId objectId) {
    FoldChangeResult foldChange = FoldChangeResult.builder().log2Fc(0.5d).build();

    GeneExpressionDocument document = new GeneExpressionDocument();
    document.setId(objectId);
    document.setEnsemblGeneId("ENSMUSG00000000002");
    document.setGeneSymbol("Apoe");
    document.setBiodomains(List.of("Lipid Metabolism"));
    document.setName(Link.builder().linkText("APOE4").linkUrl("").build());
    document.setMatchedControl("C57BL/6J");
    document.setModelGroup(null);
    document.setModelType("Familial AD");
    document.setTissue("Hemibrain");
    document.setSexCohort("Females");
    document.setFourMonths(foldChange);
    return document;
  }
}
