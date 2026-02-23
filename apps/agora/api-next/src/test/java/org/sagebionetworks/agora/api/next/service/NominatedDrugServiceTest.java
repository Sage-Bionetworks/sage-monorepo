package org.sagebionetworks.agora.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.agora.api.next.model.document.NominatedDrugDocument;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugSearchQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugsPageDto;
import org.sagebionetworks.agora.api.next.model.mapper.NominatedDrugMapper;
import org.sagebionetworks.agora.api.next.model.repository.NominatedDrugRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class NominatedDrugServiceTest {

  @Mock
  private NominatedDrugRepository repository;

  private NominatedDrugService service;

  @BeforeEach
  void setUp() {
    service = new NominatedDrugService(repository, new NominatedDrugMapper());
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

    NominatedDrugsPageDto result = service.loadNominatedDrugs(query);

    assertThat(result.getNominatedDrugs()).isEmpty();
    assertThat(result.getPage().getTotalElements()).isZero();
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedDrugSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should return all items when exclude filter has no items")
  void shouldReturnAllItemsWhenExcludeFilterHasNoItems() {
    NominatedDrugDocument doc = buildDocument("Agomelatine");
    Page<NominatedDrugDocument> page = new PageImpl<>(List.of(doc));

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

    NominatedDrugsPageDto result = service.loadNominatedDrugs(query);

    assertThat(result.getNominatedDrugs()).hasSize(1);
    assertThat(result.getNominatedDrugs().get(0).getCommonName()).isEqualTo("Agomelatine");
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedDrugSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should return matching items when include filter has items")
  void shouldReturnMatchingItemsWhenIncludeFilterHasItems() {
    NominatedDrugDocument doc = buildDocument("Agomelatine");
    Page<NominatedDrugDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedDrugSearchQueryDto.class),
        eq(List.of("Agomelatine"))
      )
    ).thenReturn(page);

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .items(List.of("Agomelatine"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedDrugsPageDto result = service.loadNominatedDrugs(query);

    assertThat(result.getNominatedDrugs()).hasSize(1);
    assertThat(result.getNominatedDrugs().get(0).getCommonName()).isEqualTo("Agomelatine");
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedDrugSearchQueryDto.class),
      eq(List.of("Agomelatine"))
    );
  }

  @Test
  @DisplayName("should return non-matching items when exclude filter has items")
  void shouldReturnNonMatchingItemsWhenExcludeFilterHasItems() {
    NominatedDrugDocument doc = buildDocument("Bexarotene");
    Page<NominatedDrugDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedDrugSearchQueryDto.class),
        eq(List.of("Agomelatine"))
      )
    ).thenReturn(page);

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .items(List.of("Agomelatine"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedDrugsPageDto result = service.loadNominatedDrugs(query);

    assertThat(result.getNominatedDrugs()).hasSize(1);
    assertThat(result.getNominatedDrugs().get(0).getCommonName()).isEqualTo("Bexarotene");
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedDrugSearchQueryDto.class),
      eq(List.of("Agomelatine"))
    );
  }

  @Test
  @DisplayName("should perform partial case-insensitive search when exclude filter has search term")
  void shouldPerformPartialCaseInsensitiveSearchWhenExcludeFilterHasSearchTerm() {
    NominatedDrugDocument doc = buildDocument("Agomelatine");
    Page<NominatedDrugDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedDrugSearchQueryDto.class),
        eq(List.of("Bexarotene"))
      )
    ).thenReturn(page);

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .items(List.of("Bexarotene"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("agom")
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedDrugsPageDto result = service.loadNominatedDrugs(query);

    assertThat(result.getNominatedDrugs()).hasSize(1);
    assertThat(result.getNominatedDrugs().get(0).getCommonName()).isEqualTo("Agomelatine");
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedDrugSearchQueryDto.class),
      eq(List.of("Bexarotene"))
    );
  }

  @Test
  @DisplayName(
    "should perform case-insensitive full match search when exclude filter has comma-separated search terms"
  )
  void shouldPerformCaseInsensitiveFullMatchSearchWhenExcludeFilterHasCommaSeparatedSearchTerms() {
    NominatedDrugDocument doc1 = buildDocument("Agomelatine");
    NominatedDrugDocument doc2 = buildDocument("Bexarotene");
    Page<NominatedDrugDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedDrugSearchQueryDto.class),
        eq(List.of("Donepezil"))
      )
    ).thenReturn(page);

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .items(List.of("Donepezil"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("agomelatine,bexarotene")
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedDrugsPageDto result = service.loadNominatedDrugs(query);

    assertThat(result.getNominatedDrugs()).hasSize(2);
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedDrugSearchQueryDto.class),
      eq(List.of("Donepezil"))
    );
  }

  @Test
  @DisplayName("should not use search when include filter is specified")
  void shouldNotUseSearchWhenIncludeFilterIsSpecified() {
    NominatedDrugDocument doc = buildDocument("Agomelatine");
    Page<NominatedDrugDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedDrugSearchQueryDto.class),
        eq(List.of("Agomelatine"))
      )
    ).thenReturn(page);

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .items(List.of("Agomelatine"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .search("agom")
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedDrugsPageDto result = service.loadNominatedDrugs(query);

    assertThat(result.getNominatedDrugs()).hasSize(1);
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedDrugSearchQueryDto.class),
      eq(List.of("Agomelatine"))
    );
  }

  @Test
  @DisplayName("should use default page size when not specified")
  void shouldUseDefaultPageSizeWhenNotSpecified() {
    Page<NominatedDrugDocument> page = new PageImpl<>(List.of());

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedDrugSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(null)
      .pageSize(null)
      .build();

    service.loadNominatedDrugs(query);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(
      pageableCaptor.capture(),
      any(NominatedDrugSearchQueryDto.class),
      eq(List.of())
    );

    Pageable pageable = pageableCaptor.getValue();
    assertThat(pageable.getPageNumber()).isZero();
    assertThat(pageable.getPageSize()).isEqualTo(100);
  }

  @Test
  @DisplayName("should trim whitespace from search term")
  void shouldTrimWhitespaceFromSearchTerm() {
    NominatedDrugDocument doc = buildDocument("Agomelatine");
    Page<NominatedDrugDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedDrugSearchQueryDto.class),
        eq(List.of("Bexarotene"))
      )
    ).thenReturn(page);

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .items(List.of("Bexarotene"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("  agom  ")
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedDrugsPageDto result = service.loadNominatedDrugs(query);

    assertThat(result.getNominatedDrugs()).hasSize(1);
    assertThat(result.getNominatedDrugs().get(0).getCommonName()).isEqualTo("Agomelatine");
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedDrugSearchQueryDto.class),
      eq(List.of("Bexarotene"))
    );
  }

  @Test
  @DisplayName("should apply data filters to repository query")
  void shouldApplyDataFiltersToRepositoryQuery() {
    NominatedDrugDocument doc = buildDocument("Agomelatine");
    Page<NominatedDrugDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(NominatedDrugSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .principalInvestigators(List.of("PI One"))
      .programs(List.of("ACTDRx AD"))
      .totalNominations(List.of(3))
      .yearFirstNominated(List.of(2022))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    NominatedDrugsPageDto result = service.loadNominatedDrugs(query);

    assertThat(result.getNominatedDrugs()).hasSize(1);
    assertThat(result.getNominatedDrugs().get(0).getCommonName()).isEqualTo("Agomelatine");
    verify(repository).findAll(
      any(Pageable.class),
      any(NominatedDrugSearchQueryDto.class),
      eq(List.of())
    );
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
