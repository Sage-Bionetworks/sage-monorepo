package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.exception.ComparisonToolConfigNotFoundException;
import org.sagebionetworks.model.ad.api.next.model.document.ComparisonToolConfigDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolPageDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.ComparisonToolConfigMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ComparisonToolConfigRepository;

@ExtendWith(MockitoExtension.class)
class ComparisonToolConfigServiceTest {

  @Mock
  private ComparisonToolConfigRepository repository;

  @Mock
  private ComparisonToolConfigMapper mapper;

  private ComparisonToolConfigService service;

  @BeforeEach
  void setUp() {
    service = new ComparisonToolConfigService(repository, mapper);
  }

  @Test
  @DisplayName("should throw exception when repository returns null")
  void shouldThrowExceptionWhenRepositoryReturnsNull() {
    // given
    ComparisonToolPageDto page = ComparisonToolPageDto.GENE_EXPRESSION;
    when(repository.findByPage(page.getValue())).thenReturn(null);

    // when & then
    assertThatThrownBy(() -> service.getConfigsByPage(page))
      .isInstanceOf(ComparisonToolConfigNotFoundException.class)
      .hasMessage("Comparison Tool config not found for page: Gene Expression");

    verify(repository).findByPage("Gene Expression");
  }

  @Test
  @DisplayName("should throw exception when repository returns empty list")
  void shouldThrowExceptionWhenRepositoryReturnsEmptyList() {
    // given
    ComparisonToolPageDto page = ComparisonToolPageDto.MODEL_OVERVIEW;
    when(repository.findByPage(page.getValue())).thenReturn(List.of());

    // when & then
    assertThatThrownBy(() -> service.getConfigsByPage(page))
      .isInstanceOf(ComparisonToolConfigNotFoundException.class)
      .hasMessage("Comparison Tool config not found for page: Model Overview");

    verify(repository).findByPage("Model Overview");
  }

  @Test
  @DisplayName("should convert enum to string value when calling repository")
  void shouldConvertEnumToStringValueWhenCallingRepository() {
    // given
    ComparisonToolPageDto page = ComparisonToolPageDto.DISEASE_CORRELATION;
    ComparisonToolConfigDocument document = createDocument(page.getValue());
    ComparisonToolConfigDto dto = new ComparisonToolConfigDto();

    when(repository.findByPage(page.getValue())).thenReturn(List.of(document));
    when(mapper.toDto(document)).thenReturn(dto);

    // when
    List<ComparisonToolConfigDto> result = service.getConfigsByPage(page);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(dto);
    verify(repository).findByPage("Disease Correlation");
    verify(mapper).toDto(document);
  }

  @Test
  @DisplayName("should return mapped configs when repository returns documents")
  void shouldReturnMappedConfigsWhenRepositoryReturnsDocuments() {
    // given
    ComparisonToolPageDto page = ComparisonToolPageDto.GENE_EXPRESSION;
    ComparisonToolConfigDocument doc1 = createDocument(page.getValue());
    ComparisonToolConfigDocument doc2 = createDocument(page.getValue());
    ComparisonToolConfigDto dto1 = new ComparisonToolConfigDto();
    ComparisonToolConfigDto dto2 = new ComparisonToolConfigDto();

    when(repository.findByPage(page.getValue())).thenReturn(List.of(doc1, doc2));
    when(mapper.toDto(doc1)).thenReturn(dto1);
    when(mapper.toDto(doc2)).thenReturn(dto2);

    // when
    List<ComparisonToolConfigDto> result = service.getConfigsByPage(page);

    // then
    assertThat(result).hasSize(2);
    assertThat(result).containsExactly(dto1, dto2);
    verify(repository).findByPage("Gene Expression");
    verify(mapper).toDto(doc1);
    verify(mapper).toDto(doc2);
  }

  private ComparisonToolConfigDocument createDocument(String page) {
    ComparisonToolConfigDocument doc = new ComparisonToolConfigDocument();
    doc.setId(new ObjectId());
    doc.setPage(page);
    doc.setDropdowns(List.of());
    doc.setRowCount("10");
    doc.setColumns(List.of());
    doc.setFilters(List.of());
    return doc;
  }
}
