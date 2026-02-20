package org.sagebionetworks.agora.api.next.model.repository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.agora.api.next.model.document.NominatedTargetDocument;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

@ExtendWith(MockitoExtension.class)
class CustomNominatedTargetRepositoryImplTest {

  private static final String COLLECTION_NAME = "nominatedtargets";

  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private AggregationResults<NominatedTargetDocument> aggregationResults;

  private CustomNominatedTargetRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository = new CustomNominatedTargetRepositoryImpl(mongoTemplate);
  }

  @Test
  @DisplayName("should use aggregation pipeline for queries")
  void shouldUseAggregationPipeline() {
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(5L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(NominatedTargetDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .cohortStudies(List.of("ROSMAP"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();

    Pageable pageable = PageRequest.of(0, 10);

    Page<NominatedTargetDocument> result = repository.findAll(pageable, query, List.of());

    // Verify aggregation was used (not find)
    verify(mongoTemplate).aggregate(
      any(Aggregation.class),
      eq(COLLECTION_NAME),
      eq(NominatedTargetDocument.class)
    );
    verify(mongoTemplate, never()).find(any(Query.class), eq(NominatedTargetDocument.class));

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
        eq(NominatedTargetDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    // Sort by array fields
    Sort sort = Sort.by(Sort.Order.desc("nominating_teams"), Sort.Order.desc("cohort_studies"));
    Pageable pageable = PageRequest.of(0, 10, sort);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(COLLECTION_NAME),
      eq(NominatedTargetDocument.class)
    );

    // Verify the aggregation pipeline contains sorting operations
    Aggregation aggregation = aggregationCaptor.getValue();
    String pipelineString = aggregation.toString();

    // $addFields should compute _sort fields for arrays
    assertThat(pipelineString).contains("nominating_teams_sort");
    assertThat(pipelineString).contains("cohort_studies_sort");
  }

  @Test
  @DisplayName("should execute count query without pagination")
  void shouldExecuteCountQueryWithoutPagination() {
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(100L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(NominatedTargetDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .cohortStudies(List.of("ROSMAP"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();

    Pageable pageable = PageRequest.of(2, 25);

    Page<NominatedTargetDocument> result = repository.findAll(pageable, query, List.of());

    // Count query should be executed with simple Query, not aggregation
    verify(mongoTemplate).count(any(Query.class), eq(COLLECTION_NAME));

    // Total should reflect the count result
    assertThat(result.getTotalElements()).isEqualTo(100L);
  }

  @Test
  @DisplayName("should handle parallel array sorting by concatenating array elements")
  void shouldHandleParallelArraySorting() {
    // This test verifies the fix for MongoDB's "cannot sort with keys that are parallel arrays" error.
    // When sorting by multiple array fields (e.g., nominating_teams AND cohort_studies),
    // we concatenate array elements into a string, and sort by that string.

    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(10L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(NominatedTargetDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    // Sort by THREE array fields simultaneously - this would fail without scalar computation
    Sort sort = Sort.by(
      Sort.Order.desc("nominating_teams"),
      Sort.Order.asc("cohort_studies"),
      Sort.Order.desc("programs")
    );
    Pageable pageable = PageRequest.of(0, 10, sort);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(COLLECTION_NAME),
      eq(NominatedTargetDocument.class)
    );

    String pipelineString = aggregationCaptor.getValue().toString();

    // Verify $addFields computes sort fields for all array fields
    assertThat(pipelineString)
      .as("Should compute sort field for nominating_teams")
      .contains("nominating_teams_sort");
    assertThat(pipelineString)
      .as("Should compute sort field for cohort_studies")
      .contains("cohort_studies_sort");
    assertThat(pipelineString)
      .as("Should compute sort field for programs")
      .contains("programs_sort");

    // Verify $reduce is used to concatenate elements
    assertThat(pipelineString).as("Should use $reduce to concatenate elements").contains("$reduce");
  }

  @Test
  @DisplayName("should handle mixed sorting with arrays, strings, and scalar fields")
  void shouldHandleMixedSorting() {
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(0L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(NominatedTargetDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());

    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .build();

    // Mix of array field, string field, and scalar field
    Sort sort = Sort.by(
      Sort.Order.desc("nominating_teams"), // array
      Sort.Order.asc("hgnc_symbol"), // string
      Sort.Order.desc("total_nominations") // scalar (integer)
    );
    Pageable pageable = PageRequest.of(0, 10, sort);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(COLLECTION_NAME),
      eq(NominatedTargetDocument.class)
    );

    String pipelineString = aggregationCaptor.getValue().toString();

    // Array field should use $reduce for concatenation
    assertThat(pipelineString).contains("nominating_teams_sort");
    assertThat(pipelineString).contains("$reduce");

    // String fields (hgnc_symbol) are sorted directly without transformation
    assertThat(pipelineString)
      .as("String fields should not have _sort suffix")
      .doesNotContain("hgnc_symbol_sort");

    // Scalar integer field (total_nominations) should NOT have a _sort suffix
    // because integers don't need transformation for sorting
    assertThat(pipelineString)
      .as("Integer fields should not have _sort suffix")
      .doesNotContain("total_nominations_sort");
  }
}
