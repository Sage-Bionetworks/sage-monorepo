package org.sagebionetworks.qtl.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.explorers.CtPage;
import org.sagebionetworks.qtl.api.model.document.EqtlDocument;
import org.sagebionetworks.qtl.api.model.dto.EqtlDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlSearchQueryDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlSearchQueryDto.SortOrdersEnum;
import org.sagebionetworks.qtl.api.model.dto.EqtlsPageDto;
import org.sagebionetworks.qtl.api.model.dto.PageMetadataDto;
import org.sagebionetworks.qtl.api.model.mapper.EqtlMapper;
import org.sagebionetworks.qtl.api.model.repository.EqtlRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class EqtlServiceTest {

  private static final long TOTAL_ROWS = 381;

  @Mock
  private EqtlRepository repository;

  @Mock
  private EqtlMapper mapper;

  private EqtlService service;

  @BeforeEach
  void setUp() {
    service = new EqtlService(repository, mapper);
  }

  @Test
  @DisplayName("should request the first default page sorted as asked when paging is unset")
  void shouldRequestTheFirstDefaultPageSortedAsAskedWhenPagingIsUnset() {
    // given
    EqtlSearchQueryDto query = sortedQuery();
    query.setPageNumber(null);
    query.setPageSize(null);
    when(repository.findAll(any(), eq(query), eq(List.of()))).thenReturn(
      new CtPage<>(List.of(), Pageable.unpaged(), 0, null)
    );

    // when
    service.loadEqtls(query);

    // then
    Pageable pageable = capturePageable(query, List.of());
    assertThat(pageable.getPageNumber()).isEqualTo(EqtlService.DEFAULT_PAGE_NUMBER);
    assertThat(pageable.getPageSize()).isEqualTo(EqtlService.DEFAULT_PAGE_SIZE);
    assertThat(pageable.getSort()).isEqualTo(
      Sort.by(Sort.Order.asc("adj_p_val"), Sort.Order.desc("variant"))
    );
  }

  @Test
  @DisplayName("should pass the requested page and the pins without nulls to the repository")
  void shouldPassTheRequestedPageAndThePinsWithoutNullsToTheRepository() {
    // given
    EqtlSearchQueryDto query = sortedQuery();
    query.setPageNumber(2);
    query.setPageSize(25);
    query.setItems(Arrays.asList("rs1~ENSG1~astro", null));
    List<String> sanitizedItems = List.of("rs1~ENSG1~astro");
    when(repository.findAll(any(), eq(query), eq(sanitizedItems))).thenReturn(
      new CtPage<>(List.of(), Pageable.unpaged(), 0, null)
    );

    // when
    service.loadEqtls(query);

    // then
    Pageable pageable = capturePageable(query, sanitizedItems);
    assertThat(pageable.getPageNumber()).isEqualTo(2);
    assertThat(pageable.getPageSize()).isEqualTo(25);
  }

  @Test
  @DisplayName("should map each row and describe the page")
  void shouldMapEachRowAndDescribeThePage() {
    // given
    EqtlSearchQueryDto query = sortedQuery();
    query.setPageNumber(1);
    query.setPageSize(2);
    EqtlDocument first = new EqtlDocument();
    EqtlDocument second = new EqtlDocument();
    EqtlDto firstDto = EqtlDto.builder().compositeId("rs1~ENSG1~astro").build();
    EqtlDto secondDto = EqtlDto.builder().compositeId("rs2~ENSG2~astro").build();
    when(mapper.toDto(first)).thenReturn(firstDto);
    when(mapper.toDto(second)).thenReturn(secondDto);
    when(repository.findAll(any(), eq(query), eq(List.of()))).thenReturn(
      new CtPage<>(List.of(first, second), PageRequest.of(1, 2), TOTAL_ROWS, null)
    );

    // when
    EqtlsPageDto page = service.loadEqtls(query);

    // then
    assertThat(page.getEqtls()).containsExactly(firstDto, secondDto);
    assertThat(page.getPage()).isEqualTo(
      PageMetadataDto.builder()
        .number(1)
        .size(2)
        .totalElements(TOTAL_ROWS)
        .totalPages(191)
        .hasNext(true)
        .hasPrevious(true)
        .build()
    );
  }

  @Test
  @DisplayName("should pass on whether the selected row is in the results")
  void shouldPassOnWhetherTheSelectedRowIsInTheResults() {
    // given
    EqtlSearchQueryDto query = sortedQuery();
    query.setSelectedRowId("rs1~ENSG1~astro");
    when(repository.findAll(any(), eq(query), eq(List.of()))).thenReturn(
      new CtPage<>(List.of(), Pageable.unpaged(), 0, null, false)
    );

    // when
    EqtlsPageDto page = service.loadEqtls(query);

    // then
    assertThat(page.getSelectedRowInResults()).isFalse();
  }

  private static EqtlSearchQueryDto sortedQuery() {
    EqtlSearchQueryDto query = new EqtlSearchQueryDto();
    query.setSortFields(List.of("adj_p_val", "variant"));
    query.setSortOrders(List.of(SortOrdersEnum.NUMBER_1, SortOrdersEnum.NUMBER_MINUS_1));
    return query;
  }

  private Pageable capturePageable(EqtlSearchQueryDto query, List<String> items) {
    ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
    verify(repository).findAll(captor.capture(), eq(query), eq(items));
    return captor.getValue();
  }
}
