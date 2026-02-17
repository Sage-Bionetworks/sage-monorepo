package org.sagebionetworks.agora.api.next.model.repository;

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
import org.sagebionetworks.agora.api.next.model.document.NominatedTargetDocument;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@ExtendWith(MockitoExtension.class)
class CustomNominatedTargetRepositoryImplTest {

  @Mock
  private MongoTemplate mongoTemplate;

  private CustomNominatedTargetRepositoryImpl repository;

  @BeforeEach
  void setUp() {
    repository = new CustomNominatedTargetRepositoryImpl(mongoTemplate);
    when(mongoTemplate.find(any(Query.class), eq(NominatedTargetDocument.class))).thenReturn(
      List.of()
    );
    when(mongoTemplate.count(any(Query.class), eq(NominatedTargetDocument.class))).thenReturn(0L);
  }

  @Test
  @DisplayName("should build query with data filters using $in operators")
  void shouldBuildQueryWithDataFilters() {
    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .cohortStudies(List.of("ROSMAP"))
      .inputData(List.of("RNA"))
      .initialNomination(List.of(2021))
      .nominatingTeams(List.of("Duke"))
      .pharosClass(List.of("Tbio"))
      .programs(List.of("AMP-AD"))
      .totalNominations(List.of(5))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();

    Pageable pageable = PageRequest.of(0, 10);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).find(queryCaptor.capture(), eq(NominatedTargetDocument.class));

    Query capturedQuery = queryCaptor.getValue();
    String queryString = capturedQuery.toString();

    // Verify all 7 data filter fields are included in the query
    assertTrue(queryString.contains("cohort_studies"), "Query should include cohortStudies field");
    assertTrue(queryString.contains("input_data"), "Query should include inputData field");
    assertTrue(
      queryString.contains("initial_nomination"),
      "Query should include initialNomination field"
    );
    assertTrue(
      queryString.contains("nominating_teams"),
      "Query should include nominatingTeams field"
    );
    assertTrue(queryString.contains("pharos_class"), "Query should include pharosClass field");
    assertTrue(queryString.contains("programs"), "Query should include programs field");
    assertTrue(
      queryString.contains("total_nominations"),
      "Query should include totalNominations field"
    );
  }

  @Test
  @DisplayName("should apply pagination to data query")
  void shouldApplyPaginationToDataQuery() {
    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .cohortStudies(List.of("ROSMAP"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();

    Pageable pageable = PageRequest.of(2, 25);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).find(queryCaptor.capture(), eq(NominatedTargetDocument.class));

    Query dataQuery = queryCaptor.getValue();
    assertEquals(50, dataQuery.getSkip(), "Data query should skip 50 items (page 2 * size 25)");
    assertEquals(25, dataQuery.getLimit(), "Data query should limit to 25 items");
  }

  @Test
  @DisplayName("should execute count query without pagination")
  void shouldExecuteCountQueryWithoutPagination() {
    NominatedTargetSearchQueryDto query = NominatedTargetSearchQueryDto.builder()
      .cohortStudies(List.of("ROSMAP"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .build();

    Pageable pageable = PageRequest.of(2, 25);

    repository.findAll(pageable, query, List.of());

    ArgumentCaptor<Query> countQueryCaptor = ArgumentCaptor.forClass(Query.class);
    verify(mongoTemplate).count(countQueryCaptor.capture(), eq(NominatedTargetDocument.class));

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
