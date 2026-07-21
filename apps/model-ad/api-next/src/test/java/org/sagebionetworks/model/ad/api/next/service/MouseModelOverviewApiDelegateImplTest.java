package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.api.*;
import org.sagebionetworks.model.ad.api.next.model.document.Link;
import org.sagebionetworks.model.ad.api.next.model.document.MouseModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.LinkMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.MouseModelOverviewMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.MouseModelOverviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class MouseModelOverviewApiDelegateImplTest {

  @Mock
  private MouseModelOverviewRepository repository;

  private MouseModelOverviewApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    // Mock the request context for validation
    MockHttpServletRequest request = new MockHttpServletRequest();
    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    MouseModelOverviewService queryService = new MouseModelOverviewService(
      repository,
      new MouseModelOverviewMapper(new LinkMapper())
    );
    delegate = new MouseModelOverviewApiDelegateImpl(queryService);
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    Page<MouseModelOverviewDocument> page = new PageImpl<>(List.of());
    when(
      repository.findAll(
        any(Pageable.class),
        any(MouseModelOverviewSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    MouseModelOverviewSearchQueryDto query = MouseModelOverviewSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<MouseModelOverviewsPageDto> response = delegate.getMouseModelOverviews(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getMouseModelOverviews()).isEmpty();
    assertThat(response.getBody().getPage().getTotalElements()).isZero();

    HttpHeaders headers = response.getHeaders();
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
    assertThat(headers.getPragma()).contains("no-cache");
    assertThat(headers.getExpires()).isZero();
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

    verify(repository).findAll(
      any(Pageable.class),
      any(MouseModelOverviewSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should return mapped results when items provided")
  void shouldReturnMappedResultsWhenItemsProvided() {
    String modelName = "Model A";
    MouseModelOverviewDocument document = buildDocument(modelName);
    Page<MouseModelOverviewDocument> page = new PageImpl<>(
      List.of(document),
      PageRequest.of(0, 100),
      1
    );

    when(
      repository.findAll(
        any(Pageable.class),
        any(MouseModelOverviewSearchQueryDto.class),
        eq(List.of(modelName))
      )
    ).thenReturn(page);

    MouseModelOverviewSearchQueryDto query = MouseModelOverviewSearchQueryDto.builder()
      .items(List.of(modelName))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<MouseModelOverviewsPageDto> response = delegate.getMouseModelOverviews(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getMouseModelOverviews()).hasSize(1);
    assertThat(response.getBody().getPage().getTotalElements()).isEqualTo(1);
    assertThat(response.getBody().getPage().getTotalPages()).isEqualTo(1);
    assertThat(response.getBody().getPage().getNumber()).isZero();
    assertThat(response.getBody().getPage().getSize()).isEqualTo(100);
    assertThat(response.getBody().getPage().getHasNext()).isFalse();
    assertThat(response.getBody().getPage().getHasPrevious()).isFalse();

    MouseModelOverviewDto dto = response.getBody().getMouseModelOverviews().get(0);
    assertThat(dto.getName()).isEqualTo(modelName);
    assertThat(dto.getModelType()).isEqualTo("Late Onset AD");
    assertThat(dto.getMatchedControls()).containsExactly("Control 1");
    assertThat(dto.getAvailableData()).containsExactly(
      MouseModelOverviewDto.AvailableDataEnum.TRANSCRIPTOMICS,
      MouseModelOverviewDto.AvailableDataEnum.PATHOLOGY
    );
    assertThat(dto.getStudyData().getLinkUrl()).isEqualTo("https://example.org/study");
    assertThat(dto.getTranscriptomics()).isNotNull();

    HttpHeaders headers = response.getHeaders();
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
  }

  @Test
  @DisplayName("should return all items when exclude filter has no items")
  void shouldReturnAllItemsWhenExcludeFilterHasNoItems() {
    String modelName1 = "Model A";
    String modelName2 = "Model B";
    MouseModelOverviewDocument document1 = buildDocument(modelName1);
    MouseModelOverviewDocument document2 = buildDocument(modelName2);
    Page<MouseModelOverviewDocument> page = new PageImpl<>(
      List.of(document1, document2),
      PageRequest.of(0, 100),
      2
    );

    when(
      repository.findAll(
        any(Pageable.class),
        any(MouseModelOverviewSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    MouseModelOverviewSearchQueryDto query = MouseModelOverviewSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<MouseModelOverviewsPageDto> response = delegate.getMouseModelOverviews(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getMouseModelOverviews()).hasSize(2);
    assertThat(response.getBody().getPage().getTotalElements()).isEqualTo(2);

    verify(repository).findAll(
      any(Pageable.class),
      any(MouseModelOverviewSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should exclude specified items when exclude filter has items")
  void shouldExcludeSpecifiedItemsWhenExcludeFilterHasItems() {
    String excludedName = "Excluded Model";
    String includedName = "Included Model";
    MouseModelOverviewDocument includedDocument = buildDocument(includedName);
    Page<MouseModelOverviewDocument> page = new PageImpl<>(
      List.of(includedDocument),
      PageRequest.of(0, 100),
      1
    );

    when(
      repository.findAll(
        any(Pageable.class),
        any(MouseModelOverviewSearchQueryDto.class),
        eq(List.of(excludedName))
      )
    ).thenReturn(page);

    MouseModelOverviewSearchQueryDto query = MouseModelOverviewSearchQueryDto.builder()
      .items(List.of(excludedName))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<MouseModelOverviewsPageDto> response = delegate.getMouseModelOverviews(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getMouseModelOverviews()).hasSize(1);
    MouseModelOverviewDto dto = response.getBody().getMouseModelOverviews().get(0);
    assertThat(dto.getName()).isEqualTo(includedName);

    verify(repository).findAll(
      any(Pageable.class),
      any(MouseModelOverviewSearchQueryDto.class),
      eq(List.of(excludedName))
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

    MouseModelOverviewSearchQueryDto query = MouseModelOverviewSearchQueryDto.builder()
      .pageNumber(0)
      .pageSize(100)
      .build();

    // Should throw IllegalArgumentException for invalid field
    assertThatThrownBy(() -> delegate.getMouseModelOverviews(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Unknown query parameter: invalidField");
  }

  private MouseModelOverviewDocument buildDocument(String name) {
    Link requiredLink = Link.builder()
      .linkText("Study")
      .linkUrl("https://example.org/study")
      .build();

    Link optionalLink = Link.builder()
      .linkText("Transcriptomics")
      .linkUrl("https://example.org/gene")
      .build();

    MouseModelOverviewDocument document = new MouseModelOverviewDocument();
    document.setId(new ObjectId());
    document.setName(name);
    document.setModelType("Late Onset AD");
    document.setMatchedControls(List.of("Control 1"));
    document.setTranscriptomics(optionalLink);
    document.setDiseaseCorrelation(optionalLink);
    document.setBiomarkers(optionalLink);
    document.setPathology(optionalLink);
    document.setStudyData(requiredLink);
    document.setJaxStrain(requiredLink);
    document.setCenter("IU/Jax/Pitt");
    document.setModifiedGenes(List.of("Gene 1"));
    document.setAvailableData(List.of("Transcriptomics", "Pathology"));
    return document;
  }
}
