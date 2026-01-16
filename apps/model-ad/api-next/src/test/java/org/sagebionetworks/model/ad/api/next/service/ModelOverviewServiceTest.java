package org.sagebionetworks.model.ad.api.next.service;

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
import org.sagebionetworks.model.ad.api.next.model.document.Link;
import org.sagebionetworks.model.ad.api.next.model.document.ModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.LinkMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.ModelOverviewMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ModelOverviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ModelOverviewServiceTest {

  @Mock
  private ModelOverviewRepository repository;

  private ModelOverviewService service;
  private ModelOverviewMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ModelOverviewMapper(new LinkMapper());
    service = new ModelOverviewService(repository, mapper);
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    Page<ModelOverviewDocument> page = new PageImpl<>(List.of());

    when(
      repository.findAll(any(Pageable.class), any(ModelOverviewSearchQueryDto.class), eq(List.of()))
    ).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ModelOverviewsPageDto result = service.loadModelOverviews(query);

    assertThat(result.getModelOverviews()).isEmpty();
    assertThat(result.getPage().getTotalElements()).isZero();
    verify(repository).findAll(
      any(Pageable.class),
      any(ModelOverviewSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should return all items when exclude filter has no items")
  void shouldReturnAllItemsWhenExcludeFilterHasNoItems() {
    ModelOverviewDocument doc = createModelOverviewDocument("Model1");
    Page<ModelOverviewDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(any(Pageable.class), any(ModelOverviewSearchQueryDto.class), eq(List.of()))
    ).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ModelOverviewsPageDto result = service.loadModelOverviews(query);

    assertThat(result.getModelOverviews()).hasSize(1);
    assertThat(result.getModelOverviews().get(0).getName()).isEqualTo("Model1");
    verify(repository).findAll(
      any(Pageable.class),
      any(ModelOverviewSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should return matching items when include filter has items")
  void shouldReturnMatchingItemsWhenIncludeFilterHasItems() {
    ModelOverviewDocument doc = createModelOverviewDocument("Model1");
    Page<ModelOverviewDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(ModelOverviewSearchQueryDto.class),
        eq(List.of("Model1"))
      )
    ).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(List.of("Model1"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ModelOverviewsPageDto result = service.loadModelOverviews(query);

    assertThat(result.getModelOverviews()).hasSize(1);
    assertThat(result.getModelOverviews().get(0).getName()).isEqualTo("Model1");
    verify(repository).findAll(
      any(Pageable.class),
      any(ModelOverviewSearchQueryDto.class),
      eq(List.of("Model1"))
    );
  }

  @Test
  @DisplayName("should return non-matching items when exclude filter has items")
  void shouldReturnNonMatchingItemsWhenExcludeFilterHasItems() {
    ModelOverviewDocument doc = createModelOverviewDocument("Model2");
    Page<ModelOverviewDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(ModelOverviewSearchQueryDto.class),
        eq(List.of("Model1"))
      )
    ).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(List.of("Model1"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    ModelOverviewsPageDto result = service.loadModelOverviews(query);

    assertThat(result.getModelOverviews()).hasSize(1);
    assertThat(result.getModelOverviews().get(0).getName()).isEqualTo("Model2");
    verify(repository).findAll(
      any(Pageable.class),
      any(ModelOverviewSearchQueryDto.class),
      eq(List.of("Model1"))
    );
  }

  @Test
  @DisplayName("should perform partial case-insensitive search when exclude filter has search term")
  void shouldPerformPartialCaseInsensitiveSearchWhenExcludeFilterHasSearchTerm() {
    ModelOverviewDocument doc = createModelOverviewDocument("TestModel");
    Page<ModelOverviewDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(ModelOverviewSearchQueryDto.class),
        eq(List.of("Model1"))
      )
    ).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(List.of("Model1"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("test")
      .pageNumber(0)
      .pageSize(100)
      .build();

    ModelOverviewsPageDto result = service.loadModelOverviews(query);

    assertThat(result.getModelOverviews()).hasSize(1);
    assertThat(result.getModelOverviews().get(0).getName()).isEqualTo("TestModel");
    verify(repository).findAll(
      any(Pageable.class),
      any(ModelOverviewSearchQueryDto.class),
      eq(List.of("Model1"))
    );
  }

  @Test
  @DisplayName(
    "should perform case-insensitive full match search when exclude filter has comma-separated search terms"
  )
  void shouldPerformCaseInsensitiveFullMatchSearchWhenExcludeFilterHasCommaSeparatedSearchTerms() {
    ModelOverviewDocument doc1 = createModelOverviewDocument("Model1");
    ModelOverviewDocument doc2 = createModelOverviewDocument("Model2");
    Page<ModelOverviewDocument> page = new PageImpl<>(List.of(doc1, doc2));

    when(
      repository.findAll(
        any(Pageable.class),
        any(ModelOverviewSearchQueryDto.class),
        eq(List.of("Model3"))
      )
    ).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(List.of("Model3"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("model1,model2")
      .pageNumber(0)
      .pageSize(100)
      .build();

    ModelOverviewsPageDto result = service.loadModelOverviews(query);

    assertThat(result.getModelOverviews()).hasSize(2);
    verify(repository).findAll(
      any(Pageable.class),
      any(ModelOverviewSearchQueryDto.class),
      eq(List.of("Model3"))
    );
  }

  @Test
  @DisplayName("should not use search when include filter is specified")
  void shouldNotUseSearchWhenIncludeFilterIsSpecified() {
    ModelOverviewDocument doc = createModelOverviewDocument("Model1");
    Page<ModelOverviewDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(ModelOverviewSearchQueryDto.class),
        eq(List.of("Model1"))
      )
    ).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(List.of("Model1"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .search("test")
      .pageNumber(0)
      .pageSize(100)
      .build();

    ModelOverviewsPageDto result = service.loadModelOverviews(query);

    assertThat(result.getModelOverviews()).hasSize(1);
    verify(repository).findAll(
      any(Pageable.class),
      any(ModelOverviewSearchQueryDto.class),
      eq(List.of("Model1"))
    );
  }

  @Test
  @DisplayName("should use default page size when not specified")
  void shouldUseDefaultPageSizeWhenNotSpecified() {
    Page<ModelOverviewDocument> page = new PageImpl<>(List.of());

    when(
      repository.findAll(any(Pageable.class), any(ModelOverviewSearchQueryDto.class), eq(List.of()))
    ).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(null)
      .pageSize(null)
      .build();

    service.loadModelOverviews(query);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(
      pageableCaptor.capture(),
      any(ModelOverviewSearchQueryDto.class),
      eq(List.of())
    );

    Pageable pageable = pageableCaptor.getValue();
    assertThat(pageable.getPageNumber()).isZero();
    assertThat(pageable.getPageSize()).isEqualTo(100);
  }

  @Test
  @DisplayName("should trim whitespace from search term")
  void shouldTrimWhitespaceFromSearchTerm() {
    ModelOverviewDocument doc = createModelOverviewDocument("TestModel");
    Page<ModelOverviewDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(ModelOverviewSearchQueryDto.class),
        eq(List.of("Model1"))
      )
    ).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(List.of("Model1"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("  test  ")
      .pageNumber(0)
      .pageSize(100)
      .build();

    ModelOverviewsPageDto result = service.loadModelOverviews(query);

    assertThat(result.getModelOverviews()).hasSize(1);
    assertThat(result.getModelOverviews().get(0).getName()).isEqualTo("TestModel");
    verify(repository).findAll(
      any(Pageable.class),
      any(ModelOverviewSearchQueryDto.class),
      eq(List.of("Model1"))
    );
  }

  @Test
  @DisplayName("should filter empty names from comma-separated search")
  void shouldFilterEmptyNamesFromCommaSeparatedSearch() {
    ModelOverviewDocument doc = createModelOverviewDocument("Model1");
    Page<ModelOverviewDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(ModelOverviewSearchQueryDto.class),
        eq(List.of("Model2"))
      )
    ).thenReturn(page);

    ModelOverviewSearchQueryDto query = ModelOverviewSearchQueryDto.builder()
      .items(List.of("Model2"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("model1, , ,model3")
      .pageNumber(0)
      .pageSize(100)
      .build();

    ModelOverviewsPageDto result = service.loadModelOverviews(query);

    assertThat(result.getModelOverviews()).hasSize(1);
    assertThat(result.getModelOverviews().get(0).getName()).isEqualTo("Model1");
    verify(repository).findAll(
      any(Pageable.class),
      any(ModelOverviewSearchQueryDto.class),
      eq(List.of("Model2"))
    );
  }

  private ModelOverviewDocument createModelOverviewDocument(String name) {
    Link requiredLink = Link.builder().linkText("Link").linkUrl("https://example.org").build();

    ModelOverviewDocument document = new ModelOverviewDocument();
    document.setId(new ObjectId());
    document.setName(name);
    document.setModelType("Mouse");
    document.setMatchedControls(List.of());
    document.setModifiedGenes(List.of());
    document.setAvailableData(List.of());
    document.setStudyData(requiredLink);
    document.setJaxStrain(requiredLink);
    document.setCenter(requiredLink);
    return document;
  }
}
