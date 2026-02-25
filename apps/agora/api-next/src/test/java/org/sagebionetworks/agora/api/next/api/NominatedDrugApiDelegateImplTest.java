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
import org.sagebionetworks.agora.api.next.model.document.NominatedDrugDocument;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugSearchQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugsPageDto;
import org.sagebionetworks.agora.api.next.model.mapper.NominatedDrugMapper;
import org.sagebionetworks.agora.api.next.model.repository.NominatedDrugRepository;
import org.sagebionetworks.agora.api.next.service.NominatedDrugService;
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
class NominatedDrugApiDelegateImplTest {

  @Mock
  private NominatedDrugRepository repository;

  private NominatedDrugApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    NominatedDrugService service = new NominatedDrugService(
      repository,
      new NominatedDrugMapper()
    );
    delegate = new NominatedDrugApiDelegateImpl(service);
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    Page<NominatedDrugDocument> page = new PageImpl<>(List.of());
    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedDrugSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<NominatedDrugsPageDto> response = delegate.getNominatedDrugs(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getNominatedDrugs()).isEmpty();
    assertThat(response.getBody().getPage().getTotalElements()).isZero();

    HttpHeaders headers = response.getHeaders();
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
    assertThat(headers.getPragma()).contains("no-cache");
    assertThat(headers.getExpires()).isZero();
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedDrugSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should return mapped results when items provided")
  void shouldReturnMappedResultsWhenItemsProvided() {
    String commonName = "Agomelatine";
    NominatedDrugDocument document = buildDocument(commonName);
    Page<NominatedDrugDocument> page = new PageImpl<>(
      List.of(document),
      PageRequest.of(0, 100),
      1
    );

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedDrugSearchQueryDto.class),
        eq(List.of(commonName))
      )
    ).thenReturn(page);

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .items(List.of(commonName))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<NominatedDrugsPageDto> response = delegate.getNominatedDrugs(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getNominatedDrugs()).hasSize(1);
    assertThat(response.getBody().getPage().getTotalElements()).isEqualTo(1);
    assertThat(response.getBody().getPage().getTotalPages()).isEqualTo(1);
    assertThat(response.getBody().getPage().getNumber()).isZero();
    assertThat(response.getBody().getPage().getSize()).isEqualTo(100);
    assertThat(response.getBody().getPage().getHasNext()).isFalse();
    assertThat(response.getBody().getPage().getHasPrevious()).isFalse();

    NominatedDrugDto dto = response.getBody().getNominatedDrugs().get(0);
    assertThat(dto.getCommonName()).isEqualTo(commonName);
    assertThat(dto.getTotalNominations()).isEqualTo(3);
    assertThat(dto.getYearFirstNominated()).isEqualTo(2022);
    assertThat(dto.getPrincipalInvestigators()).containsExactly("PI One", "PI Two");
    assertThat(dto.getPrograms()).containsExactly("ACTDRx AD", "Community");

    HttpHeaders headers = response.getHeaders();
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
  }

  @Test
  @DisplayName("should return all items when exclude filter has no items")
  void shouldReturnAllItemsWhenExcludeFilterHasNoItems() {
    NominatedDrugDocument document1 = buildDocument("Agomelatine");
    NominatedDrugDocument document2 = buildDocument("Bexarotene");
    Page<NominatedDrugDocument> page = new PageImpl<>(
      List.of(document1, document2),
      PageRequest.of(0, 100),
      2
    );

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedDrugSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<NominatedDrugsPageDto> response = delegate.getNominatedDrugs(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getNominatedDrugs()).hasSize(2);
    assertThat(response.getBody().getPage().getTotalElements()).isEqualTo(2);

    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedDrugSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should exclude specified items when exclude filter has items")
  void shouldExcludeSpecifiedItemsWhenExcludeFilterHasItems() {
    String excludedName = "Bexarotene";
    String includedName = "Agomelatine";
    NominatedDrugDocument includedDocument = buildDocument(includedName);
    Page<NominatedDrugDocument> page = new PageImpl<>(
      List.of(includedDocument),
      PageRequest.of(0, 100),
      1
    );

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedDrugSearchQueryDto.class),
        eq(List.of(excludedName))
      )
    ).thenReturn(page);

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .items(List.of(excludedName))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ResponseEntity<NominatedDrugsPageDto> response = delegate.getNominatedDrugs(query);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getNominatedDrugs()).hasSize(1);
    NominatedDrugDto dto = response.getBody().getNominatedDrugs().get(0);
    assertThat(dto.getCommonName()).isEqualTo(includedName);

    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedDrugSearchQueryDto.class),
      eq(List.of(excludedName))
    );
  }

  @Test
  @DisplayName("should throw IllegalArgumentException when invalid query parameter provided")
  void shouldThrowExceptionWhenInvalidQueryParameterProvided() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter("invalidField", "someValue");
    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .pageNumber(0)
      .pageSize(100)
      .build();

    assertThatThrownBy(() -> delegate.getNominatedDrugs(query))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Unknown query parameter: invalidField");
  }

  private NominatedDrugDocument buildDocument(String commonName) {
    NominatedDrugDocument document = new NominatedDrugDocument();
    document.setId(new ObjectId());
    document.setCommonName(commonName);
    document.setTotalNominations(3);
    document.setYearFirstNominated(2022);
    document.setPrincipalInvestigators(List.of("PI One", "PI Two"));
    document.setPrograms(List.of("ACTDRx AD", "Community"));
    return document;
  }
}
