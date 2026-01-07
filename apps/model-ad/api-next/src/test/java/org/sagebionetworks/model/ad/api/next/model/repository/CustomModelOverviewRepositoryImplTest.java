package org.sagebionetworks.model.ad.api.next.model.repository;

import static org.junit.jupiter.api.Assertions.*;
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
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@ExtendWith(MockitoExtension.class)
class CustomModelOverviewRepositoryImplTest {

  @Mock
  private MongoTemplate mongoTemplate;

  private CustomModelOverviewRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository = new CustomModelOverviewRepositoryImpl(mongoTemplate);
    when(mongoTemplate.find(any(Query.class), eq(ModelOverviewDocument.class)))
      .thenReturn(List.of());
    when(mongoTemplate.count(any(Query.class), eq(ModelOverviewDocument.class))).thenReturn(0L);
  }

  @Test
  @DisplayName("should build query with data filters using $in operators")
  void shouldBuildQueryWithDataFilters() {
    ModelOverviewSearchQueryDto query = new ModelOverviewSearchQueryDto();
    query.setAvailableData(List.of("test-data"));
    query.setCenter(List.of("test-center"));
    query.setModelType(List.of("test-type"));
    query.setModifiedGenes(List.of("test-gene"));
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    Pageable pageable = PageRequest.of(0, 10);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).find(queryCaptor.capture(), eq(ModelOverviewDocument.class));

    Query capturedQuery = queryCaptor.getValue();
    String queryString = capturedQuery.toString();

    // Verify all 4 data filter fields are included in the query
    assertTrue(queryString.contains("available_data"), "Query should include availableData field");
    assertTrue(queryString.contains("center.link_text"), "Query should include center field");
    assertTrue(queryString.contains("model_type"), "Query should include modelType field");
    assertTrue(queryString.contains("modified_genes"), "Query should include modifiedGenes field");
  }

  @Test
  @DisplayName("should apply pagination to data query")
  void shouldApplyPaginationToDataQuery() {
    ModelOverviewSearchQueryDto query = new ModelOverviewSearchQueryDto();
    query.setAvailableData(List.of("behavior"));
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    Pageable pageable = PageRequest.of(2, 25);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).find(queryCaptor.capture(), eq(ModelOverviewDocument.class));

    Query dataQuery = queryCaptor.getValue();
    assertEquals(50, dataQuery.getSkip(), "Data query should skip 50 items (page 2 * size 25)");
    assertEquals(25, dataQuery.getLimit(), "Data query should limit to 25 items");
  }

  @Test
  @DisplayName("should execute count query without pagination")
  void shouldExecuteCountQueryWithoutPagination() {
    ModelOverviewSearchQueryDto query = new ModelOverviewSearchQueryDto();
    query.setAvailableData(List.of("behavior"));
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    Pageable pageable = PageRequest.of(2, 25);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Query> countQueryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).count(countQueryCaptor.capture(), eq(ModelOverviewDocument.class));

    Query countQuery = countQueryCaptor.getValue();
    assertEquals(0, countQuery.getLimit(), "Count query should have no limit");
    assertEquals(0, countQuery.getSkip(), "Count query should have no skip");
  }
}
