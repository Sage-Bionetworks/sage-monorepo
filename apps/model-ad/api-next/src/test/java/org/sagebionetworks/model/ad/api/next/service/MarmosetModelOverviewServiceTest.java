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
import org.sagebionetworks.model.ad.api.next.model.document.MarmosetModelOverviewDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.LinkMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.MarmosetModelOverviewMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.MarmosetModelOverviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class MarmosetModelOverviewServiceTest {

  @Mock
  private MarmosetModelOverviewRepository repository;

  private MarmosetModelOverviewService service;
  private MarmosetModelOverviewMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new MarmosetModelOverviewMapper(new LinkMapper());
    service = new MarmosetModelOverviewService(repository, mapper);
  }

  @Test
  @DisplayName("should return empty page when include filter has no items")
  void shouldReturnEmptyPageWhenIncludeFilterHasNoItems() {
    Page<MarmosetModelOverviewDocument> page = new PageImpl<>(List.of());

    when(
      repository.findAll(
        any(Pageable.class),
        any(MarmosetModelOverviewSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    MarmosetModelOverviewSearchQueryDto query = MarmosetModelOverviewSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    MarmosetModelOverviewsPageDto result = service.loadMarmosetModelOverviews(query);

    assertThat(result.getMarmosetModelOverviews()).isEmpty();
    assertThat(result.getPage().getTotalElements()).isZero();
    verify(repository).findAll(
      any(Pageable.class),
      any(MarmosetModelOverviewSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should return all items when exclude filter has no items")
  void shouldReturnAllItemsWhenExcludeFilterHasNoItems() {
    MarmosetModelOverviewDocument doc = createDocument("Model1");
    Page<MarmosetModelOverviewDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(MarmosetModelOverviewSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    MarmosetModelOverviewSearchQueryDto query = MarmosetModelOverviewSearchQueryDto.builder()
      .items(null)
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    MarmosetModelOverviewsPageDto result = service.loadMarmosetModelOverviews(query);

    assertThat(result.getMarmosetModelOverviews()).hasSize(1);
    assertThat(result.getMarmosetModelOverviews().get(0).getName()).isEqualTo("Model1");
    verify(repository).findAll(
      any(Pageable.class),
      any(MarmosetModelOverviewSearchQueryDto.class),
      eq(List.of())
    );
  }

  @Test
  @DisplayName("should return matching items when include filter has items")
  void shouldReturnMatchingItemsWhenIncludeFilterHasItems() {
    MarmosetModelOverviewDocument doc = createDocument("Model1");
    Page<MarmosetModelOverviewDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(MarmosetModelOverviewSearchQueryDto.class),
        eq(List.of("Model1"))
      )
    ).thenReturn(page);

    MarmosetModelOverviewSearchQueryDto query = MarmosetModelOverviewSearchQueryDto.builder()
      .items(List.of("Model1"))
      .itemFilterType(ItemFilterTypeQueryDto.INCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    MarmosetModelOverviewsPageDto result = service.loadMarmosetModelOverviews(query);

    assertThat(result.getMarmosetModelOverviews()).hasSize(1);
    assertThat(result.getMarmosetModelOverviews().get(0).getName()).isEqualTo("Model1");
    verify(repository).findAll(
      any(Pageable.class),
      any(MarmosetModelOverviewSearchQueryDto.class),
      eq(List.of("Model1"))
    );
  }

  @Test
  @DisplayName("should return non-matching items when exclude filter has items")
  void shouldReturnNonMatchingItemsWhenExcludeFilterHasItems() {
    MarmosetModelOverviewDocument doc = createDocument("Model2");
    Page<MarmosetModelOverviewDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(MarmosetModelOverviewSearchQueryDto.class),
        eq(List.of("Model1"))
      )
    ).thenReturn(page);

    MarmosetModelOverviewSearchQueryDto query = MarmosetModelOverviewSearchQueryDto.builder()
      .items(List.of("Model1"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(100)
      .build();

    MarmosetModelOverviewsPageDto result = service.loadMarmosetModelOverviews(query);

    assertThat(result.getMarmosetModelOverviews()).hasSize(1);
    assertThat(result.getMarmosetModelOverviews().get(0).getName()).isEqualTo("Model2");
    verify(repository).findAll(
      any(Pageable.class),
      any(MarmosetModelOverviewSearchQueryDto.class),
      eq(List.of("Model1"))
    );
  }

  @Test
  @DisplayName("should perform partial case-insensitive search when exclude filter has search term")
  void shouldPerformPartialCaseInsensitiveSearchWhenExcludeFilterHasSearchTerm() {
    MarmosetModelOverviewDocument doc = createDocument("TestModel");
    Page<MarmosetModelOverviewDocument> page = new PageImpl<>(List.of(doc));

    when(
      repository.findAll(
        any(Pageable.class),
        any(MarmosetModelOverviewSearchQueryDto.class),
        eq(List.of("Model1"))
      )
    ).thenReturn(page);

    MarmosetModelOverviewSearchQueryDto query = MarmosetModelOverviewSearchQueryDto.builder()
      .items(List.of("Model1"))
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .search("test")
      .pageNumber(0)
      .pageSize(100)
      .build();

    MarmosetModelOverviewsPageDto result = service.loadMarmosetModelOverviews(query);

    assertThat(result.getMarmosetModelOverviews()).hasSize(1);
    assertThat(result.getMarmosetModelOverviews().get(0).getName()).isEqualTo("TestModel");
    verify(repository).findAll(
      any(Pageable.class),
      any(MarmosetModelOverviewSearchQueryDto.class),
      eq(List.of("Model1"))
    );
  }

  @Test
  @DisplayName("should use default page size when not specified")
  void shouldUseDefaultPageSizeWhenNotSpecified() {
    Page<MarmosetModelOverviewDocument> page = new PageImpl<>(List.of());

    when(
      repository.findAll(
        any(Pageable.class),
        any(MarmosetModelOverviewSearchQueryDto.class),
        eq(List.of())
      )
    ).thenReturn(page);

    MarmosetModelOverviewSearchQueryDto query = MarmosetModelOverviewSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(null)
      .pageSize(null)
      .build();

    service.loadMarmosetModelOverviews(query);

    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(
      pageableCaptor.capture(),
      any(MarmosetModelOverviewSearchQueryDto.class),
      eq(List.of())
    );

    Pageable pageable = pageableCaptor.getValue();
    assertThat(pageable.getPageNumber()).isZero();
    assertThat(pageable.getPageSize()).isEqualTo(100);
  }

  private MarmosetModelOverviewDocument createDocument(String name) {
    Link requiredLink = Link.builder().linkText("Link").linkUrl("https://example.org").build();

    MarmosetModelOverviewDocument document = new MarmosetModelOverviewDocument();
    document.setId(new ObjectId());
    document.setName(name);
    document.setModelType("Marmoset");
    document.setModifiedGenes(List.of());
    document.setAvailableData(List.of());
    document.setStudyData(requiredLink);
    return document;
  }
}
