package org.sagebionetworks.qtl.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.qtl.api.model.document.DataVersionDocument;
import org.sagebionetworks.qtl.api.model.dto.DataVersionDto;
import org.sagebionetworks.qtl.api.model.mapper.DataVersionMapper;
import org.sagebionetworks.qtl.api.model.repository.DataVersionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class DataVersionServiceTest {

  @Mock
  private DataVersionRepository repository;

  @Mock
  private DataVersionMapper mapper;

  private DataVersionService service;

  @BeforeEach
  void setUp() {
    service = new DataVersionService(repository, mapper);
  }

  @Test
  @DisplayName("should throw 404 when repository returns empty")
  void shouldThrow404WhenRepositoryReturnsEmpty() {
    // given
    when(repository.findFirstBy()).thenReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> service.loadDataVersion())
      .isInstanceOf(ResponseStatusException.class)
      .hasMessageContaining("Data version not found")
      .extracting(e -> ((ResponseStatusException) e).getStatusCode())
      .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  @DisplayName("should return mapped dto when repository returns document")
  void shouldReturnMappedDtoWhenRepositoryReturnsDocument() {
    // given
    DataVersionDocument document = new DataVersionDocument();
    document.setDataFile("syn12345678");
    document.setDataVersion("42");
    DataVersionDto dto = new DataVersionDto("syn12345678", "42");

    when(repository.findFirstBy()).thenReturn(Optional.of(document));
    when(mapper.toDto(document)).thenReturn(dto);

    // when
    DataVersionDto result = service.loadDataVersion();

    // then
    assertThat(result).isEqualTo(dto);
    verify(repository).findFirstBy();
    verify(mapper).toDto(document);
  }
}
