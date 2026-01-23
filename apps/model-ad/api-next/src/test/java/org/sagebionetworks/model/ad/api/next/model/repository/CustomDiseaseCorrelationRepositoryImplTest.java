package org.sagebionetworks.model.ad.api.next.model.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

@ExtendWith(MockitoExtension.class)
class CustomDiseaseCorrelationRepositoryImplTest {

  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private AggregationResults<DiseaseCorrelationDocument> aggregationResults;

  @Mock
  private AggregationResults<Document> countResults;

  private CustomDiseaseCorrelationRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository = new CustomDiseaseCorrelationRepositoryImpl(mongoTemplate);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());
    when(mongoTemplate.count(any(), eq("disease_correlation"))).thenReturn(0L);
  }

  @Test
  @DisplayName("should build aggregation with data filters using $in operators")
  void shouldBuildAggregationWithDataFilters() {
    DiseaseCorrelationSearchQueryDto query = new DiseaseCorrelationSearchQueryDto();
    query.setAge(List.of("4 months"));
    query.setModelType(List.of("Familial AD"));
    query.setName(List.of("3xTg-AD"));
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    Pageable pageable = PageRequest.of(0, 10);

    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq("disease_correlation"),
        eq(DiseaseCorrelationDocument.class)
      )
    ).thenReturn(aggregationResults);

    repository.findAll(pageable, query, List.of(), "Cluster A");

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq("disease_correlation"),
      eq(DiseaseCorrelationDocument.class)
    );

    Aggregation capturedAggregation = aggregationCaptor.getValue();
    String aggregationString = capturedAggregation.toString();

    // Verify cluster filter is included
    assertTrue(aggregationString.contains("cluster"), "Aggregation should include cluster field");

    // Verify all 3 data filter fields are included in the aggregation
    assertTrue(aggregationString.contains("age"), "Aggregation should include age field");
    assertTrue(
      aggregationString.contains("model_type"),
      "Aggregation should include model_type field"
    );
    assertTrue(aggregationString.contains("name"), "Aggregation should include name field");
  }

  @Test
  @DisplayName("should apply pagination to data aggregation")
  void shouldApplyPaginationToDataAggregation() {
    DiseaseCorrelationSearchQueryDto query = new DiseaseCorrelationSearchQueryDto();
    query.setAge(List.of("4 months"));
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    Pageable pageable = PageRequest.of(2, 25);

    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq("disease_correlation"),
        eq(DiseaseCorrelationDocument.class)
      )
    ).thenReturn(aggregationResults);

    repository.findAll(pageable, query, List.of(), "Cluster A");

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq("disease_correlation"),
      eq(DiseaseCorrelationDocument.class)
    );

    Aggregation capturedAggregation = aggregationCaptor.getValue();
    String aggregationString = capturedAggregation.toString();

    // Verify skip and limit are applied
    assertTrue(aggregationString.contains("$skip"), "Aggregation should include $skip");
    assertTrue(
      aggregationString.contains("50"),
      "Aggregation should skip 50 items (page 2 * size 25)"
    );
    assertTrue(aggregationString.contains("$limit"), "Aggregation should include $limit");
    assertTrue(aggregationString.contains("25"), "Aggregation should limit to 25 items");
  }

  @Test
  @DisplayName("should use direct count instead of aggregation for performance")
  void shouldUseDirectCountInsteadOfAggregation() {
    DiseaseCorrelationSearchQueryDto query = new DiseaseCorrelationSearchQueryDto();
    query.setAge(List.of("4 months"));
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    Pageable pageable = PageRequest.of(2, 25);

    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq("disease_correlation"),
        eq(DiseaseCorrelationDocument.class)
      )
    ).thenReturn(aggregationResults);

    repository.findAll(pageable, query, List.of(), "Cluster A");

    // Verify mongoTemplate.count() is called for counting
    verify(mongoTemplate).count(any(), eq("disease_correlation"));
    verify(mongoTemplate, times(1)).aggregate(
      any(Aggregation.class),
      eq("disease_correlation"),
      eq(DiseaseCorrelationDocument.class)
    );
  }

  @Test
  @DisplayName("should sort by age_numeric when age sort is requested")
  void shouldSortByAgeNumericWhenAgeSortIsRequested() {
    // given
    DiseaseCorrelationSearchQueryDto query = new DiseaseCorrelationSearchQueryDto();
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    Pageable pageable = PageRequest.of(0, 10, org.springframework.data.domain.Sort.by("age"));

    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq("disease_correlation"),
        eq(DiseaseCorrelationDocument.class)
      )
    ).thenReturn(aggregationResults);

    // when
    repository.findAll(pageable, query, List.of(), "Cluster A");

    // then
    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq("disease_correlation"),
      eq(DiseaseCorrelationDocument.class)
    );

    Aggregation capturedAggregation = aggregationCaptor.getValue();
    String aggregationString = capturedAggregation.toString();

    assertTrue(
      aggregationString.contains("age_numeric"),
      "Aggregation should sort by age_numeric field"
    );
    assertFalse(
      aggregationString.contains("age_lower"),
      "Aggregation should not create age_lower field"
    );
  }
}
