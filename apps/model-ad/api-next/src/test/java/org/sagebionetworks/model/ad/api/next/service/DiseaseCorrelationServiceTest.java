package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.DiseaseCorrelationMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.DiseaseCorrelationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class DiseaseCorrelationServiceTest {

  @Mock
  private DiseaseCorrelationRepository repository;

  private DiseaseCorrelationService service;
  private DiseaseCorrelationMapper mapper;

  private static final String CLUSTER = "Cluster A";

  @BeforeEach
  void setUp() {
    mapper = new DiseaseCorrelationMapper();
    service = new DiseaseCorrelationService(repository, mapper);
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of());

    when(
      repository.findAll(
        any(Pageable.class),
        any(DiseaseCorrelationSearchQueryDto.class),
        eq(List.of()),
        eq(CLUSTER)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).isEmpty();
    assertThat(result.getPage().getTotalElements()).isZero();
    verify(repository).findAll(
      any(Pageable.class),
      any(DiseaseCorrelationSearchQueryDto.class),
      eq(List.of()),
      eq(CLUSTER)
    );
  }

  @Test
  @DisplayName("should return all cluster items when exclude filter has no items")
  void shouldReturnAllClusterItemsWhenExcludeFilterHasNoItems() {
    DiseaseCorrelationDocument doc = createDiseaseCorrelationDocument(
      "Model1",
      new BigDecimal(12),
      "Male"
    );
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(DiseaseCorrelationSearchQueryDto.class),
        eq(List.of()),
        eq(CLUSTER)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(1);
    assertThat(result.getDiseaseCorrelations().get(0).getName()).isEqualTo("Model1");
    verify(repository).findAll(
      any(Pageable.class),
      any(DiseaseCorrelationSearchQueryDto.class),
      eq(List.of()),
      eq(CLUSTER)
    );
  }

  @Test
  @DisplayName("should respect search term when exclude filter has no items")
  void shouldRespectSearchTermWhenExcludeFilterHasNoItems() {
    DiseaseCorrelationDocument doc = createDiseaseCorrelationDocument(
      "TestModel",
      new BigDecimal(12),
      "Male"
    );
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(DiseaseCorrelationSearchQueryDto.class),
        eq(List.of()),
        eq(CLUSTER)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("Test")
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(1);
    assertThat(result.getDiseaseCorrelations().get(0).getName()).isEqualTo("TestModel");
    verify(repository).findAll(
      any(Pageable.class),
      any(DiseaseCorrelationSearchQueryDto.class),
      eq(List.of()),
      eq(CLUSTER)
    );
  }

  @Test
  @DisplayName("should return matching composite identifiers when include filter has items")
  void shouldReturnMatchingCompositeIdentifiersWhenIncludeFilterHasItems() {
    DiseaseCorrelationDocument doc = createDiseaseCorrelationDocument(
      "Model1",
      new BigDecimal(12),
      "Male"
    );
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(DiseaseCorrelationSearchQueryDto.class),
        any(),
        eq(CLUSTER)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(List.of("Model1~12 months~Male"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(1);
    assertThat(result.getDiseaseCorrelations().get(0).getName()).isEqualTo("Model1");
    verify(repository).findAll(
      any(Pageable.class),
      any(DiseaseCorrelationSearchQueryDto.class),
      any(),
      eq(CLUSTER)
    );
  }

  @Test
  @DisplayName("should return non-matching composite identifiers when exclude filter has items")
  void shouldReturnNonMatchingCompositeIdentifiersWhenExcludeFilterHasItems() {
    DiseaseCorrelationDocument doc = createDiseaseCorrelationDocument(
      "Model2",
      new BigDecimal(18),
      "Female"
    );
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(DiseaseCorrelationSearchQueryDto.class),
        any(),
        eq(CLUSTER)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(List.of("Model1~12 months~Male"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(1);
    assertThat(result.getDiseaseCorrelations().get(0).getName()).isEqualTo("Model2");
    verify(repository).findAll(
      any(Pageable.class),
      any(DiseaseCorrelationSearchQueryDto.class),
      any(),
      eq(CLUSTER)
    );
  }

  @Test
  @DisplayName("should apply data filters to repository query")
  void shouldApplyDataFiltersToRepositoryQuery() {
    DiseaseCorrelationDocument doc = createDiseaseCorrelationDocument(
      "Model1",
      new BigDecimal(4),
      "Female"
    );
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(DiseaseCorrelationSearchQueryDto.class),
        eq(List.of()),
        eq(CLUSTER)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .age(List.of("4 months"))
      .modelType(List.of("Familial AD"))
      .sex(List.of("Female"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(1);
    assertThat(result.getDiseaseCorrelations().get(0).getName()).isEqualTo("Model1");
    verify(repository).findAll(
      any(Pageable.class),
      any(DiseaseCorrelationSearchQueryDto.class),
      eq(List.of()),
      eq(CLUSTER)
    );
  }

  @Test
  @DisplayName("should handle multiple composite identifiers")
  void shouldHandleMultipleCompositeIdentifiers() {
    DiseaseCorrelationDocument doc1 = createDiseaseCorrelationDocument(
      "Model1",
      new BigDecimal(12),
      "Male"
    );
    DiseaseCorrelationDocument doc2 = createDiseaseCorrelationDocument(
      "Model2",
      new BigDecimal(18),
      "Female"
    );
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findAll(
        any(Pageable.class),
        any(DiseaseCorrelationSearchQueryDto.class),
        any(),
        eq(CLUSTER)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(List.of("Model1~12 months~Male", "Model2~18 months~Female"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(2);
    verify(repository).findAll(
      any(Pageable.class),
      any(DiseaseCorrelationSearchQueryDto.class),
      any(),
      eq(CLUSTER)
    );
  }

  @Test
  @DisplayName("should use default page size when not specified")
  void shouldUseDefaultPageSizeWhenNotSpecified() {
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of());

    when(
      repository.findAll(
        any(Pageable.class),
        any(DiseaseCorrelationSearchQueryDto.class),
        eq(List.of()),
        eq(CLUSTER)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(null)
      .pageSize(null)
      .build();

    service.loadDiseaseCorrelations(query, CLUSTER);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(
      pageableCaptor.capture(),
      any(DiseaseCorrelationSearchQueryDto.class),
      eq(List.of()),
      eq(CLUSTER)
    );

    Pageable pageable = pageableCaptor.getValue();
    assertThat(pageable.getPageNumber()).isZero();
    assertThat(pageable.getPageSize()).isEqualTo(100);
  }

  private DiseaseCorrelationDocument createDiseaseCorrelationDocument(
    String name,
    BigDecimal ageNumeric,
    String sex
  ) {
    DiseaseCorrelationDocument document = new DiseaseCorrelationDocument();
    document.setId(new ObjectId());
    document.setCluster(CLUSTER);
    document.setName(name);
    document.setAge(ageNumeric + " months");
    document.setAgeNumeric(ageNumeric);
    document.setSex(sex);
    document.setMatchedControl("Control1");
    document.setModelType("Familial AD");
    document.setModifiedGenes(List.of());
    return document;
  }
}
