package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.exception.InvalidFilterException;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.DiseaseCorrelationMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.DiseaseCorrelationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class DiseaseCorrelationServiceTest {

  @Mock
  private DiseaseCorrelationRepository repository;

  private DiseaseCorrelationService service;
  private DiseaseCorrelationMapper mapper;

  private static final String CLUSTER = "cluster1";

  @BeforeEach
  void setUp() {
    mapper = new DiseaseCorrelationMapper();
    service = new DiseaseCorrelationService(repository, mapper);
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).isEmpty();
    assertThat(result.getPage().getTotalElements()).isZero();
    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should return all cluster items when exclude filter has no items and no search")
  void shouldReturnAllClusterItemsWhenExcludeFilterHasNoItemsAndNoSearch() {
    DiseaseCorrelationDocument doc = createDiseaseCorrelationDocument("Model1", "12m", "Male");
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc));

    when(repository.findByCluster(eq(CLUSTER), any(PageRequest.class))).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(1);
    assertThat(result.getDiseaseCorrelations().get(0).getName()).isEqualTo("Model1");
    verify(repository).findByCluster(eq(CLUSTER), any(PageRequest.class));
  }

  @Test
  @DisplayName("should respect search term when exclude filter has no items")
  void shouldRespectSearchTermWhenExcludeFilterHasNoItems() {
    DiseaseCorrelationDocument doc = createDiseaseCorrelationDocument("Model1", "12m", "Male");
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByClusterAndNameContaining(
        eq(CLUSTER),
        eq("\\Qodel\\E"),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("odel")
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(1);
    assertThat(result.getDiseaseCorrelations().get(0).getName()).isEqualTo("Model1");
    verify(repository).findByClusterAndNameContaining(
      eq(CLUSTER),
      eq("\\Qodel\\E"),
      any(PageRequest.class)
    );
  }

  @Test
  @DisplayName("should use search-only method for comma-separated search when no items to exclude")
  void shouldUseSearchOnlyMethodForCommaSeparatedSearchWhenNoItemsToExclude() {
    DiseaseCorrelationDocument doc1 = createDiseaseCorrelationDocument("Model1", "12m", "Male");
    DiseaseCorrelationDocument doc2 = createDiseaseCorrelationDocument("Model2", "18m", "Female");
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findByClusterAndNameInIgnoreCase(eq(CLUSTER), anyList(), any(PageRequest.class))
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("model1,model2")
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(2);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Pattern>> patternsCaptor = ArgumentCaptor.forClass(List.class);
    verify(repository).findByClusterAndNameInIgnoreCase(
      eq(CLUSTER),
      patternsCaptor.capture(),
      any(PageRequest.class)
    );

    List<Pattern> patterns = patternsCaptor.getValue();
    assertThat(patterns).hasSize(2);
    assertThat(patterns.get(0).pattern()).isEqualTo("^\\Qmodel1\\E$");
    assertThat(patterns.get(1).pattern()).isEqualTo("^\\Qmodel2\\E$");
  }

  @Test
  @DisplayName("should return matching composite identifiers when include filter has items")
  void shouldReturnMatchingCompositeIdentifiersWhenIncludeFilterHasItems() {
    DiseaseCorrelationDocument doc = createDiseaseCorrelationDocument("Model1", "12m", "Male");
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByClusterAndCompositeIdentifiers(
        eq(CLUSTER),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(List.of("Model1~12m~Male"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(1);
    assertThat(result.getDiseaseCorrelations().get(0).getName()).isEqualTo("Model1");

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Map<String, Object>>> conditionsCaptor = ArgumentCaptor.forClass(
      List.class
    );
    verify(repository).findByClusterAndCompositeIdentifiers(
      eq(CLUSTER),
      conditionsCaptor.capture(),
      any(PageRequest.class)
    );

    List<Map<String, Object>> conditions = conditionsCaptor.getValue();
    assertThat(conditions).hasSize(1);
    verifyCompositeCondition(conditions.get(0), "Model1", "12m", "Male");
  }

  @Test
  @DisplayName("should return non-matching composite identifiers when exclude filter has items")
  void shouldReturnNonMatchingCompositeIdentifiersWhenExcludeFilterHasItems() {
    DiseaseCorrelationDocument doc = createDiseaseCorrelationDocument("Model2", "18m", "Female");
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByClusterExcludingCompositeIdentifiers(
        eq(CLUSTER),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(List.of("Model1~12m~Male"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(1);
    assertThat(result.getDiseaseCorrelations().get(0).getName()).isEqualTo("Model2");

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Map<String, Object>>> conditionsCaptor = ArgumentCaptor.forClass(
      List.class
    );
    verify(repository).findByClusterExcludingCompositeIdentifiers(
      eq(CLUSTER),
      conditionsCaptor.capture(),
      any(PageRequest.class)
    );

    List<Map<String, Object>> conditions = conditionsCaptor.getValue();
    assertThat(conditions).hasSize(1);
    verifyCompositeCondition(conditions.get(0), "Model1", "12m", "Male");
  }

  @Test
  @DisplayName("should perform partial case-insensitive search when exclude filter has search term")
  void shouldPerformPartialCaseInsensitiveSearchWhenExcludeFilterHasSearchTerm() {
    DiseaseCorrelationDocument doc = createDiseaseCorrelationDocument("TestModel", "12m", "Male");
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByClusterAndNameContainingExcludingCompositeIdentifiers(
        eq(CLUSTER),
        any(String.class),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(List.of("Model1~12m~Male"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("test")
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(1);
    assertThat(result.getDiseaseCorrelations().get(0).getName()).isEqualTo("TestModel");

    ArgumentCaptor<String> searchCaptor = ArgumentCaptor.forClass(String.class);
    verify(repository).findByClusterAndNameContainingExcludingCompositeIdentifiers(
      eq(CLUSTER),
      searchCaptor.capture(),
      anyList(),
      any(PageRequest.class)
    );

    // Verify Pattern.quote was used (wraps search term in \Q \E)
    assertThat(searchCaptor.getValue()).isEqualTo("\\Qtest\\E");
  }

  @Test
  @DisplayName(
    "should perform case-insensitive full match search when exclude filter has " +
    "comma-separated search terms"
  )
  void shouldPerformCaseInsensitiveFullMatchSearchWhenExcludeFilterHasCommaSeparatedSearchTerms() {
    DiseaseCorrelationDocument doc1 = createDiseaseCorrelationDocument("Model1", "12m", "Male");
    DiseaseCorrelationDocument doc2 = createDiseaseCorrelationDocument("Model2", "18m", "Female");
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findByClusterAndNameInIgnoreCaseExcludingCompositeIdentifiers(
        eq(CLUSTER),
        anyList(),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(List.of("Model3~12m~Male"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("model1,model2")
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(2);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Pattern>> patternsCaptor = ArgumentCaptor.forClass(List.class);
    verify(repository).findByClusterAndNameInIgnoreCaseExcludingCompositeIdentifiers(
      eq(CLUSTER),
      patternsCaptor.capture(),
      anyList(),
      any(PageRequest.class)
    );

    List<Pattern> patterns = patternsCaptor.getValue();
    assertThat(patterns).hasSize(2);
    assertThat(patterns.get(0).pattern()).isEqualTo("^\\Qmodel1\\E$");
    assertThat(patterns.get(1).pattern()).isEqualTo("^\\Qmodel2\\E$");
    // Verify case-insensitive flag is set
    assertThat(patterns.get(0).flags() & Pattern.CASE_INSENSITIVE).isNotZero();
  }

  @Test
  @DisplayName("should not use search when include filter is specified")
  void shouldNotUseSearchWhenIncludeFilterIsSpecified() {
    DiseaseCorrelationDocument doc = createDiseaseCorrelationDocument("Model1", "12m", "Male");
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByClusterAndCompositeIdentifiers(
        eq(CLUSTER),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(List.of("Model1~12m~Male"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .search("test")
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(1);
    // Should use findByClusterAndCompositeIdentifiers, not search methods
    verify(repository).findByClusterAndCompositeIdentifiers(
      eq(CLUSTER),
      anyList(),
      any(PageRequest.class)
    );
  }

  @Test
  @DisplayName("should handle multiple composite identifiers")
  void shouldHandleMultipleCompositeIdentifiers() {
    DiseaseCorrelationDocument doc1 = createDiseaseCorrelationDocument("Model1", "12m", "Male");
    DiseaseCorrelationDocument doc2 = createDiseaseCorrelationDocument("Model2", "18m", "Female");
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findByClusterAndCompositeIdentifiers(
        eq(CLUSTER),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(List.of("Model1~12m~Male", "Model2~18m~Female"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    DiseaseCorrelationsPageDto result = service.loadDiseaseCorrelations(query, CLUSTER);

    assertThat(result.getDiseaseCorrelations()).hasSize(2);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Map<String, Object>>> conditionsCaptor = ArgumentCaptor.forClass(
      List.class
    );
    verify(repository).findByClusterAndCompositeIdentifiers(
      eq(CLUSTER),
      conditionsCaptor.capture(),
      any(PageRequest.class)
    );

    List<Map<String, Object>> conditions = conditionsCaptor.getValue();
    assertThat(conditions).hasSize(2);
    verifyCompositeCondition(conditions.get(0), "Model1", "12m", "Male");
    verifyCompositeCondition(conditions.get(1), "Model2", "18m", "Female");
  }

  @Test
  @DisplayName("should throw exception when composite identifier is invalid")
  void shouldThrowExceptionWhenCompositeIdentifierIsInvalid() {
    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(List.of("InvalidFormat"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    assertThatThrownBy(() -> service.loadDiseaseCorrelations(query, CLUSTER))
      .isInstanceOf(InvalidFilterException.class)
      .hasMessageContaining("Invalid composite identifier format");

    verifyNoInteractions(repository);
  }

  @Test
  @DisplayName("should trim whitespace from search term")
  void shouldTrimWhitespaceFromSearchTerm() {
    DiseaseCorrelationDocument doc = createDiseaseCorrelationDocument("TestModel", "12m", "Male");
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByClusterAndNameContainingExcludingCompositeIdentifiers(
        eq(CLUSTER),
        any(String.class),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(List.of("Model1~12m~Male"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("  test  ")
      .pageNumber(0)
      .pageSize(100)
      .build();

    service.loadDiseaseCorrelations(query, CLUSTER);

    ArgumentCaptor<String> searchCaptor = ArgumentCaptor.forClass(String.class);
    verify(repository).findByClusterAndNameContainingExcludingCompositeIdentifiers(
      eq(CLUSTER),
      searchCaptor.capture(),
      anyList(),
      any(PageRequest.class)
    );

    // Should be trimmed
    assertThat(searchCaptor.getValue()).isEqualTo("\\Qtest\\E");
  }

  @Test
  @DisplayName("should filter empty names from comma-separated search")
  void shouldFilterEmptyNamesFromCommaSeparatedSearch() {
    DiseaseCorrelationDocument doc = createDiseaseCorrelationDocument("Model1", "12m", "Male");
    Page<DiseaseCorrelationDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findByClusterAndNameInIgnoreCaseExcludingCompositeIdentifiers(
        eq(CLUSTER),
        anyList(),
        anyList(),
        any(PageRequest.class)
      )
    ).thenReturn(page);

    DiseaseCorrelationSearchQueryDto query = DiseaseCorrelationSearchQueryDto.builder()
      .items(List.of("Model2~12m~Male"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("model1, , ,model3")
      .pageNumber(0)
      .pageSize(100)
      .build();

    service.loadDiseaseCorrelations(query, CLUSTER);

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Pattern>> patternsCaptor = ArgumentCaptor.forClass(List.class);
    verify(repository).findByClusterAndNameInIgnoreCaseExcludingCompositeIdentifiers(
      eq(CLUSTER),
      patternsCaptor.capture(),
      anyList(),
      any(PageRequest.class)
    );

    List<Pattern> patterns = patternsCaptor.getValue();
    // Should only have 2 patterns (empty strings filtered out)
    assertThat(patterns).hasSize(2);
    assertThat(patterns.get(0).pattern()).isEqualTo("^\\Qmodel1\\E$");
    assertThat(patterns.get(1).pattern()).isEqualTo("^\\Qmodel3\\E$");
  }

  private DiseaseCorrelationDocument createDiseaseCorrelationDocument(
    String name,
    String age,
    String sex
  ) {
    DiseaseCorrelationDocument document = new DiseaseCorrelationDocument();
    document.setId(new ObjectId());
    document.setCluster(CLUSTER);
    document.setName(name);
    document.setAge(age);
    document.setSex(sex);
    document.setMatchedControl("Control1");
    document.setModelType("Mouse");
    document.setModifiedGenes(List.of());
    return document;
  }

  @SuppressWarnings("unchecked")
  private void verifyCompositeCondition(
    Map<String, Object> condition,
    String expectedName,
    String expectedAge,
    String expectedSex
  ) {
    assertThat(condition).containsKey("$and");
    List<Map<String, Object>> andConditions = (List<Map<String, Object>>) condition.get("$and");
    assertThat(andConditions).hasSize(3);

    assertThat(andConditions.get(0)).containsEntry("name", expectedName);
    assertThat(andConditions.get(1)).containsEntry("age", expectedAge);
    assertThat(andConditions.get(2)).containsEntry("sex", expectedSex);
  }
}
