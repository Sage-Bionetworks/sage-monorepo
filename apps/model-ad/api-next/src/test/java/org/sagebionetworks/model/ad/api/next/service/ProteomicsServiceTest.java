package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
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
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.FoldChangeMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.LinkMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.ProteomicsMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ProteomicsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class ProteomicsServiceTest {

  private static final String TISSUE = "Hemibrain";
  private static final String UNIQUE_ID = "ENSMUSG00000000001P27144";
  private static final String MODEL_NAME = "LOAD2";
  private static final String SEX = "Female";
  private static final int DEFAULT_PAGE_SIZE = 100;

  @Mock
  private ProteomicsRepository repository;

  private ProteomicsService service;

  @BeforeEach
  void setUp() {
    service = new ProteomicsService(
      repository,
      new ProteomicsMapper(new LinkMapper(), new FoldChangeMapper())
    );
  }

  @Test
  @DisplayName("should return empty page when repository has no matches")
  void shouldReturnEmptyPageWhenRepositoryHasNoMatches() {
    stubRepository(new PageImpl<>(List.of()), List.of());

    ProteomicsPageDto result = service.loadProteomics(buildQuery().build(), TISSUE);

    assertThat(result.getProteomics()).isEmpty();
    assertThat(result.getPage().getTotalElements()).isZero();
  }

  @Test
  @DisplayName("should map every returned document")
  void shouldMapEveryReturnedDocument() {
    stubRepository(new PageImpl<>(List.of(buildDocument())), List.of());

    ProteomicsPageDto result = service.loadProteomics(buildQuery().build(), TISSUE);

    assertThat(result.getProteomics()).singleElement().satisfies(dto -> {
      assertThat(dto.getCompositeId()).isEqualTo(UNIQUE_ID + "~" + MODEL_NAME + "~" + SEX);
      assertThat(dto.getDisplaySymbol()).isEqualTo("Gnai3 (P27144)");
      assertThat(dto.getTissue()).isEqualTo(TISSUE);
    });
  }

  @Test
  @DisplayName("should drop null items before querying the repository")
  void shouldDropNullItemsBeforeQueryingRepository() {
    String compositeId = UNIQUE_ID + "~" + MODEL_NAME + "~" + SEX;
    stubRepository(new PageImpl<>(List.of(buildDocument())), List.of(compositeId));

    ProteomicsSearchQueryDto query = buildQuery().items(Arrays.asList(compositeId, null)).build();

    service.loadProteomics(query, TISSUE);

    verify(repository).findAll(
      any(Pageable.class),
      any(ProteomicsSearchQueryDto.class),
      eq(List.of(compositeId)),
      eq(TISSUE)
    );
  }

  @Test
  @DisplayName("should use default paging when page number and size are not specified")
  void shouldUseDefaultPagingWhenPageNumberAndSizeAreNotSpecified() {
    stubRepository(new PageImpl<>(List.of()), List.of());

    ProteomicsSearchQueryDto query = buildQuery().pageNumber(null).pageSize(null).build();

    service.loadProteomics(query, TISSUE);

    Pageable pageable = capturePageable();
    assertThat(pageable.getPageNumber()).isZero();
    assertThat(pageable.getPageSize()).isEqualTo(DEFAULT_PAGE_SIZE);
  }

  @Test
  @DisplayName("should translate sort fields and orders into a sort")
  void shouldTranslateSortFieldsAndOrdersIntoSort() {
    stubRepository(new PageImpl<>(List.of()), List.of());

    ProteomicsSearchQueryDto query = buildQuery()
      .sortFields(List.of("24 months", "display_symbol"))
      .sortOrders(
        List.of(
          ProteomicsSearchQueryDto.SortOrdersEnum.fromValue(-1),
          ProteomicsSearchQueryDto.SortOrdersEnum.fromValue(1)
        )
      )
      .build();

    service.loadProteomics(query, TISSUE);

    Sort sort = capturePageable().getSort();
    assertThat(sort.getOrderFor("24 months")).isNotNull();
    assertThat(sort.getOrderFor("24 months").getDirection()).isEqualTo(Sort.Direction.DESC);
    assertThat(sort.getOrderFor("display_symbol").getDirection()).isEqualTo(Sort.Direction.ASC);
  }

  @Test
  @DisplayName("should report page metadata from the repository page")
  void shouldReportPageMetadataFromRepositoryPage() {
    Page<ProteomicsDocument> page = new PageImpl<>(
      List.of(buildDocument()),
      PageRequest.of(1, 10),
      25
    );
    stubRepository(page, List.of());

    ProteomicsPageDto result = service.loadProteomics(
      buildQuery().pageNumber(1).pageSize(10).build(),
      TISSUE
    );

    assertThat(result.getPage().getNumber()).isEqualTo(1);
    assertThat(result.getPage().getSize()).isEqualTo(10);
    assertThat(result.getPage().getTotalElements()).isEqualTo(25);
    assertThat(result.getPage().getTotalPages()).isEqualTo(3);
    assertThat(result.getPage().getHasNext()).isTrue();
    assertThat(result.getPage().getHasPrevious()).isTrue();
  }

  private void stubRepository(Page<ProteomicsDocument> page, List<String> items) {
    when(
      repository.findAll(
        any(Pageable.class),
        any(ProteomicsSearchQueryDto.class),
        eq(items),
        eq(TISSUE)
      )
    ).thenReturn(page);
  }

  private Pageable capturePageable() {
    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(
      captor.capture(),
      any(ProteomicsSearchQueryDto.class),
      any(),
      eq(TISSUE)
    );
    return captor.getValue();
  }

  private ProteomicsSearchQueryDto.Builder buildQuery() {
    return ProteomicsSearchQueryDto.builder()
      .itemFilterType(ItemFilterTypeQueryDto.EXCLUDE)
      .pageNumber(0)
      .pageSize(10);
  }

  private ProteomicsDocument buildDocument() {
    ProteomicsDocument document = new ProteomicsDocument();
    document.setId(new ObjectId());
    document.setEnsemblGeneId("ENSMUSG00000000001");
    document.setGeneSymbol("Gnai3");
    document.setUniprotid("P27144");
    document.setUniqueId(UNIQUE_ID);
    document.setDisplaySymbol("Gnai3 (P27144)");
    document.setBiodomains(List.of("Synapse"));
    document.setName(Link.builder().linkText(MODEL_NAME).linkUrl("models/LOAD2").build());
    document.setMatchedControl("C57BL/6J");
    document.setModelType("Late Onset AD");
    document.setTissue(TISSUE);
    document.setSex(SEX);
    return document;
  }
}
