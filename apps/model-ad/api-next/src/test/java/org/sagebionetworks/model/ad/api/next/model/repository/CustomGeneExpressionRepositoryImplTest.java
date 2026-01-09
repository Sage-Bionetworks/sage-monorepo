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
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@ExtendWith(MockitoExtension.class)
class CustomGeneExpressionRepositoryImplTest {

  @Mock
  private MongoTemplate mongoTemplate;

  private CustomGeneExpressionRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository = new CustomGeneExpressionRepositoryImpl(mongoTemplate);
    when(mongoTemplate.find(any(Query.class), eq(GeneExpressionDocument.class)))
      .thenReturn(List.of());
    when(mongoTemplate.count(any(Query.class), eq(GeneExpressionDocument.class))).thenReturn(0L);
  }

  @Test
  @DisplayName("should build query with data filters using $in operators")
  void shouldBuildQueryWithDataFilters() {
    GeneExpressionSearchQueryDto query = new GeneExpressionSearchQueryDto();
    query.setBiodomains(List.of("Apoptosis"));
    query.setModelType(List.of("Familial AD"));
    query.setName(List.of("3xTg-AD"));
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    Pageable pageable = PageRequest.of(0, 10);

    repository.findAll(pageable, query, List.of(), "Hemibrain", "Females & Males");

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).find(queryCaptor.capture(), eq(GeneExpressionDocument.class));

    Query capturedQuery = queryCaptor.getValue();
    String queryString = capturedQuery.toString();

    // Verify tissue and sex_cohort filters are included
    assertTrue(queryString.contains("tissue"), "Query should include tissue field");
    assertTrue(queryString.contains("sex_cohort"), "Query should include sex_cohort field");

    // Verify all 3 data filter fields are included in the query
    assertTrue(queryString.contains("biodomains"), "Query should include biodomains field");
    assertTrue(queryString.contains("model_type"), "Query should include model_type field");
    assertTrue(queryString.contains("name"), "Query should include name field");
  }

  @Test
  @DisplayName("should apply pagination to data query")
  void shouldApplyPaginationToDataQuery() {
    GeneExpressionSearchQueryDto query = new GeneExpressionSearchQueryDto();
    query.setBiodomains(List.of("Apoptosis"));
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    Pageable pageable = PageRequest.of(2, 25);

    repository.findAll(pageable, query, List.of(), "Hemibrain", "Females & Males");

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).find(queryCaptor.capture(), eq(GeneExpressionDocument.class));

    Query dataQuery = queryCaptor.getValue();
    assertEquals(50, dataQuery.getSkip(), "Data query should skip 50 items (page 2 * size 25)");
    assertEquals(25, dataQuery.getLimit(), "Data query should limit to 25 items");
  }

  @Test
  @DisplayName("should execute count query without pagination")
  void shouldExecuteCountQueryWithoutPagination() {
    GeneExpressionSearchQueryDto query = new GeneExpressionSearchQueryDto();
    query.setBiodomains(List.of("Apoptosis"));
    query.setItemFilterType(ItemFilterTypeQueryDto.INCLUDE);

    Pageable pageable = PageRequest.of(2, 25);

    repository.findAll(pageable, query, List.of(), "Hemibrain", "Females & Males");

    ArgumentCaptor<Query> countQueryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).count(countQueryCaptor.capture(), eq(GeneExpressionDocument.class));

    Query countQuery = countQueryCaptor.getValue();
    assertTrue(
      countQuery.getLimit() <= 0,
      "Count query should have no limit (0 or -1), but was: " + countQuery.getLimit()
    );
    assertTrue(
      countQuery.getSkip() <= 0,
      "Count query should have no skip (0 or -1), but was: " + countQuery.getSkip()
    );
  }
}
