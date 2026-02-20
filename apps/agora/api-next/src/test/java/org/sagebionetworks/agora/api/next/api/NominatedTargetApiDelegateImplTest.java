package org.sagebionetworks.agora.api.next.api;

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
import org.sagebionetworks.agora.api.next.model.document.NominatedTargetDocument;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetsPageDto;
import org.sagebionetworks.agora.api.next.model.mapper.NominatedTargetMapper;
import org.sagebionetworks.agora.api.next.model.repository.NominatedTargetRepository;
import org.sagebionetworks.agora.api.next.service.NominatedTargetService;
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
class NominatedTargetApiDelegateImplTest {

  @Mock
  private NominatedTargetRepository repository;

  private NominatedTargetApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    // Mock the request context for validation
    MockHttpServletRequest request = new MockHttpServletRequest();
    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    NominatedTargetService service = new NominatedTargetService(
      repository,
      new NominatedTargetMapper()
    );
    delegate = new NominatedTargetApiDelegateImpl(service);
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    Page<NominatedTargetDocument> page = new PageImpl<>(List.of());
    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<NominatedTargetsPageDto> response = delegate.getNominatedTargets(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getNominatedTargets()).isEmpty();
    assertThat(response.getBody().getPage().getTotalElements()).isZero();

    HttpHeaders headers = response.getHeaders();
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
    assertThat(headers.getPragma()).contains("no-cache");
    assertThat(headers.getExpires()).isZero();
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedTargetSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should return mapped results when items provided")
  void shouldReturnMappedResultsWhenItemsProvided() {
    String hgncSymbol = "TP53";
    NominatedTargetDocument document = buildDocument(hgncSymbol);
    Page<NominatedTargetDocument> page = new PageImpl<>(
      List.of(document),
      PageRequest.of(0, 100),
      1
    );

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of(hgncSymbol))
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .items(List.of(hgncSymbol))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<NominatedTargetsPageDto> response = delegate.getNominatedTargets(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getNominatedTargets()).hasSize(1);
    assertThat(response.getBody().getPage().getTotalElements()).isEqualTo(1);
    assertThat(response.getBody().getPage().getTotalPages()).isEqualTo(1);
    assertThat(response.getBody().getPage().getNumber()).isZero();
    assertThat(response.getBody().getPage().getSize()).isEqualTo(100);
    assertThat(response.getBody().getPage().getHasNext()).isFalse();
    assertThat(response.getBody().getPage().getHasPrevious()).isFalse();

    NominatedTargetDto dto = response.getBody().getNominatedTargets().get(0);
    assertThat(dto.getHgncSymbol()).isEqualTo(hgncSymbol);
    assertThat(dto.getEnsemblGeneId()).isEqualTo("ENSG00000141510");
    assertThat(dto.getTotalNominations()).isEqualTo(5);
    assertThat(dto.getInitialNomination()).isEqualTo(2021);
    assertThat(dto.getNominatingTeams()).containsExactly("Duke", "Emory");
    assertThat(dto.getCohortStudies()).containsExactly("ROSMAP", "Mayo");
    assertThat(dto.getInputData()).containsExactly("RNA", "Protein");
    assertThat(dto.getPrograms()).containsExactly("AMP-AD", "Community");
    assertThat(dto.getPharosClass()).isEqualTo("Tbio");

    HttpHeaders headers = response.getHeaders();
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
  }

  @Test
  @DisplayName("should return all items when exclude filter has no items")
  void shouldReturnAllItemsWhenExcludeFilterHasNoItems() {
    String hgncSymbol1 = "APOE";
    String hgncSymbol2 = "TREM2";
    NominatedTargetDocument document1 = buildDocument(hgncSymbol1);
    NominatedTargetDocument document2 = buildDocument(hgncSymbol2);
    Page<NominatedTargetDocument> page = new PageImpl<>(
      List.of(document1, document2),
      PageRequest.of(0, 100),
      2
    );

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<NominatedTargetsPageDto> response = delegate.getNominatedTargets(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getNominatedTargets()).hasSize(2);
    assertThat(response.getBody().getPage().getTotalElements()).isEqualTo(2);

    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedTargetSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should exclude specified items when exclude filter has items")
  void shouldExcludeSpecifiedItemsWhenExcludeFilterHasItems() {
    String excludedSymbol = "BIN1";
    String includedSymbol = "APOE";
    NominatedTargetDocument includedDocument = buildDocument(includedSymbol);
    Page<NominatedTargetDocument> page = new PageImpl<>(
      List.of(includedDocument),
      PageRequest.of(0, 100),
      1
    );

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedTargetSearchQueryDto.class),
        eq(List.of(excludedSymbol))
      )
    ).thenReturn(page);

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .items(List.of(excludedSymbol))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<NominatedTargetsPageDto> response = delegate.getNominatedTargets(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getNominatedTargets()).hasSize(1);
    NominatedTargetDto dto = response.getBody().getNominatedTargets().get(0);
    assertThat(dto.getHgncSymbol()).isEqualTo(includedSymbol);

    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedTargetSearchQueryDto.class),
      eq(List.of(excludedSymbol))
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

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .pageNumber(0)
      .pageSize(100)
      .build();

    // Should throw IllegalArgumentException for invalid field
    assertThatThrownBy(() -> delegate.getNominatedTargets(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Unknown query parameter: invalidField");
  }

  private NominatedTargetDocument buildDocument(String hgncSymbol) {
    NominatedTargetDocument document = new NominatedTargetDocument();
    document.setId(new ObjectId());
    document.setEnsemblGeneId("ENSG00000141510");
    document.setHgncSymbol(hgncSymbol);
    document.setTotalNominations(5);
    document.setInitialNomination(2021);
    document.setNominatingTeams(List.of("Duke", "Emory"));
    document.setCohortStudies(List.of("ROSMAP", "Mayo"));
    document.setInputData(List.of("RNA", "Protein"));
    document.setPrograms(List.of("AMP-AD", "Community"));
    document.setPharosClass("Tbio");
    return document;
  }
}
