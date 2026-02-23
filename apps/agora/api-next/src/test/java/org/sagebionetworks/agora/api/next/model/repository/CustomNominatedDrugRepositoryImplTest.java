package org.sagebionetworks.agora.api.next.model.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

@ExtendWith(MockitoExtension.class)
class CustomNominatedDrugRepositoryImplTest {

  private static final String COLLECTION_NAME = "nominateddrugs";

  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private AggregationResults<NominatedDrugDocument> aggregationResults;

  private CustomNominatedDrugRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository = new CustomNominatedDrugRepositoryImpl(mongoTemplate);
  }

  @Test
  @DisplayName("should use aggregation pipeline for queries")
  void shouldUseAggregationPipeline() {
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(5L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(NominatedDrugDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .programs(List.of("ACTDRx AD"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();

    Pageable pageable = PageRequest.of(0, 10);

    Page<NominatedDrugDocument> result = repository.findAll(pageable, query, List.of());

    verify(mongoTemplate).aggregate(
      any(Aggregation.class),
      eq(COLLECTION_NAME),
      eq(NominatedDrugDocument.class)
    );
    verify(mongoTemplate, never()).find(any(Query.class), eq(NominatedDrugDocument.class));

    assertThat(result.getTotalElements()).isEqualTo(5L);
  }

  @Test
  @DisplayName("should add sort fields for array columns")
  void shouldAddSortFieldsForArrayColumns() {
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(0L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(NominatedDrugDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    Sort sort = Sort.by(
      Sort.Order.asc("principal_investigators"),
      Sort.Order.desc("programs")
    );
    Pageable pageable = PageRequest.of(0, 10, sort);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(COLLECTION_NAME),
      eq(NominatedDrugDocument.class)
    );

    String pipelineString = aggregationCaptor.getValue().toString();

    assertThat(pipelineString).contains("principal_investigators_sort");
    assertThat(pipelineString).contains("programs_sort");
  }

  @Test
  @DisplayName("should execute count query without pagination")
  void shouldExecuteCountQueryWithoutPagination() {
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(100L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(NominatedDrugDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .programs(List.of("ACTDRx AD"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();

    Pageable pageable = PageRequest.of(2, 25);

    Page<NominatedDrugDocument> result = repository.findAll(pageable, query, List.of());

    verify(mongoTemplate).count(any(Query.class), eq(COLLECTION_NAME));
    assertThat(result.getTotalElements()).isEqualTo(100L);
  }

  @Test
  @DisplayName("should apply pagination to aggregation pipeline")
  void shouldApplyPaginationToAggregationPipeline() {
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(0L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(NominatedDrugDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();

    Pageable pageable = PageRequest.of(2, 25);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(COLLECTION_NAME),
      eq(NominatedDrugDocument.class)
    );

    String pipelineString = aggregationCaptor.getValue().toString();

    assertThat(pipelineString).contains("$skip");
    assertThat(pipelineString).contains("50"); // page 2 * size 25
    assertThat(pipelineString).contains("$limit");
    assertThat(pipelineString).contains("25");
  }

  @Test
  @DisplayName("should handle parallel array sorting by concatenating array elements")
  void shouldHandleParallelArraySorting() {
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(10L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(NominatedDrugDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    // Sort by both array fields simultaneously — would fail without scalar computation
    Sort sort = Sort.by(
      Sort.Order.asc("principal_investigators"),
      Sort.Order.desc("programs")
    );
    Pageable pageable = PageRequest.of(0, 10, sort);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(COLLECTION_NAME),
      eq(NominatedDrugDocument.class)
    );

    String pipelineString = aggregationCaptor.getValue().toString();

    assertThat(pipelineString)
      .as("Should compute sort field for principal_investigators")
      .contains("principal_investigators_sort");
    assertThat(pipelineString)
      .as("Should compute sort field for programs")
      .contains("programs_sort");
    assertThat(pipelineString)
      .as("Should use $reduce to concatenate elements")
      .contains("$reduce");
  }

  @Test
  @DisplayName("should handle mixed sorting with array and scalar fields")
  void shouldHandleMixedSortingWithArrayAndScalarFields() {
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(0L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(NominatedDrugDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    Sort sort = Sort.by(
      Sort.Order.asc("principal_investigators"), // array
      Sort.Order.asc("common_name"),             // string
      Sort.Order.desc("total_nominations")       // scalar integer
    );
    Pageable pageable = PageRequest.of(0, 10, sort);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(COLLECTION_NAME),
      eq(NominatedDrugDocument.class)
    );

    String pipelineString = aggregationCaptor.getValue().toString();

    assertThat(pipelineString).contains("principal_investigators_sort");
    assertThat(pipelineString).contains("$reduce");

    assertThat(pipelineString)
      .as("String fields should not have _sort suffix")
      .doesNotContain("common_name_sort");
    assertThat(pipelineString)
      .as("Integer fields should not have _sort suffix")
      .doesNotContain("total_nominations_sort");
  }

  @Test
  @DisplayName("should build aggregation with data filters")
  void shouldBuildAggregationWithDataFilters() {
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(0L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(NominatedDrugDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());

    NominatedDrugSearchQueryDto query = NominatedDrugSearchQueryDto.builder()
      .principalInvestigators(List.of("PI One"))
      .programs(List.of("ACTDRx AD"))
      .totalNominations(List.of(3))
      .yearFirstNominated(List.of(2022))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();

    Pageable pageable = PageRequest.of(0, 10);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(COLLECTION_NAME),
      eq(NominatedDrugDocument.class)
    );

    String pipelineString = aggregationCaptor.getValue().toString();

    assertThat(pipelineString).contains("principal_investigators");
    assertThat(pipelineString).contains("programs");
    assertThat(pipelineString).contains("total_nominations");
    assertThat(pipelineString).contains("year_first_nominated");
  }
}
