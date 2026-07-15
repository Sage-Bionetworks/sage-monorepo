package org.sagebionetworks.model.ad.api.next.model.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.sagebionetworks.model.ad.api.next.model.document.MarmosetModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewSearchQueryDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;

@ExtendWith(MockitoExtension.class)
class CustomMarmosetModelOverviewRepositoryImplTest {

  private static final String COLLECTION_NAME = "marmo_overview";

  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private AggregationResults<MarmosetModelOverviewDocument> aggregationResults;

  private CustomMarmosetModelOverviewRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository = new CustomMarmosetModelOverviewRepositoryImpl(mongoTemplate);
    when(mongoTemplate.count(any(Query.class), eq(COLLECTION_NAME))).thenReturn(0L);
    when(
      mongoTemplate.aggregate(
        any(Aggregation.class),
        eq(COLLECTION_NAME),
        eq(MarmosetModelOverviewDocument.class)
      )
    ).thenReturn(aggregationResults);
    when(aggregationResults.getMappedResults()).thenReturn(List.of());
  }

  @Test
  @DisplayName("should build aggregation with data filters using $in operators")
  void shouldBuildAggregationWithDataFilters() {
    MarmosetModelOverviewSearchQueryDto query = new MarmosetModelOverviewSearchQueryDto();
    query.setAvailableData(List.of("Plasma Biomarkers"));
    query.setModelTypes(List.of("test-type"));
    query.setModifiedGenes(List.of("test-gene"));
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    Pageable pageable = PageRequest.of(0, 10);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(COLLECTION_NAME),
      eq(MarmosetModelOverviewDocument.class)
    );

    String pipelineString = aggregationCaptor.getValue().toString();

    // Verify all 3 data filter fields are included in the pipeline
    assertThat(pipelineString)
      .as("Pipeline should include available_data field")
      .contains("available_data");
    assertThat(pipelineString)
      .as("Pipeline should include model_type field")
      .contains("model_type");
    assertThat(pipelineString)
      .as("Pipeline should include modified_genes field")
      .contains("modified_genes");
  }

  @Test
  @DisplayName("should apply pagination via $skip and $limit stages")
  void shouldApplyPaginationToAggregation() {
    MarmosetModelOverviewSearchQueryDto query = new MarmosetModelOverviewSearchQueryDto();
    query.setAvailableData(List.of("Plasma Biomarkers"));
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    Pageable pageable = PageRequest.of(2, 25);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(COLLECTION_NAME),
      eq(MarmosetModelOverviewDocument.class)
    );

    String pipelineString = aggregationCaptor.getValue().toString();

    // Page 2 with size 25 → skip 50, limit 25
    assertThat(pipelineString).contains("$skip").contains("50");
    assertThat(pipelineString).contains("$limit").contains("25");
  }

  @Test
  @DisplayName("should compute modified_genes_sort via $reduce when sorting by modified_genes")
  void shouldComputeModifiedGenesSortWhenSortingByModifiedGenes() {
    MarmosetModelOverviewSearchQueryDto query = new MarmosetModelOverviewSearchQueryDto();
    query.setItemFilterType(ItemFilterTypeQueryDto.EXCLUDE);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("modified_genes")));

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(COLLECTION_NAME),
      eq(MarmosetModelOverviewDocument.class)
    );

    String pipelineString = aggregationCaptor.getValue().toString();
    assertThat(pipelineString)
      .as("Should compute a sort key via $reduce for modified_genes array")
      .contains("modified_genes_sort")
      .contains("$reduce");
    assertThat(pipelineString)
      .as("$sort should use the computed key, not the raw array field")
      .contains("\"modified_genes_sort\" : 1");
  }

  @Test
  @DisplayName("should compute available_data_sort via $reduce when sorting by available_data")
  void shouldComputeAvailableDataSortWhenSortingByAvailableData() {
    MarmosetModelOverviewSearchQueryDto query = new MarmosetModelOverviewSearchQueryDto();
    query.setItemFilterType(ItemFilterTypeQueryDto.EXCLUDE);

    Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("available_data")));

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Aggregation> aggregationCaptor = ArgumentCaptor.forClass(Aggregation.class);
    verify(mongoTemplate).aggregate(
      aggregationCaptor.capture(),
      eq(COLLECTION_NAME),
      eq(MarmosetModelOverviewDocument.class)
    );

    String pipelineString = aggregationCaptor.getValue().toString();
    assertThat(pipelineString)
      .as("Should compute a sort key via $reduce for available_data array")
      .contains("available_data_sort")
      .contains("$reduce");
    assertThat(pipelineString)
      .as("$sort should use the computed key, not the raw array field")
      .contains("\"available_data_sort\" : 1");
  }

  @Test
  @DisplayName("should use direct count instead of aggregation for total")
  void shouldUseDirectCountForTotal() {
    MarmosetModelOverviewSearchQueryDto query = new MarmosetModelOverviewSearchQueryDto();
    query.setAvailableData(List.of("Plasma Biomarkers"));
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    repository.findAll(PageRequest.of(2, 25), query, List.of());

    // Count is via mongoTemplate.count(Query, collection), not via aggregation
    verify(mongoTemplate).count(any(Query.class), eq(COLLECTION_NAME));
  }
}
