package org.sagebionetworks.agora.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.agora.api.next.model.document.DataVersionDocument;
import org.sagebionetworks.agora.api.next.model.dto.DataVersionDto;
import org.sagebionetworks.agora.api.next.model.mapper.DataVersionMapper;
import org.sagebionetworks.agora.api.next.model.repository.DataVersionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class DataVersionServiceTest {

  @Mock
  private DataVersionRepository repository;

  private DataVersionService service;
  private DataVersionMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new DataVersionMapper();
    service = new DataVersionService(repository, mapper);
  }

  @Test
  @DisplayName("should return data version when repository returns document")
  void shouldReturnDataVersionWhenRepositoryReturnsDocument() {
    // given
    DataVersionDocument document = createDataVersionDocument();
    when(repository.findFirstBy()).thenReturn(Optional.of(document));

    // when
    DataVersionDto result = service.loadDataVersion();

    // then
    assertThat(result).isNotNull();
    assertThat(result.getDataFile()).isEqualTo("syn000456");
    assertThat(result.getDataVersion()).isEqualTo("1.0.0");
    assertThat(result.getTeamImagesId()).isEqualTo("syn123456");
    verify(repository).findFirstBy();
  }

  @Test
  @DisplayName("should throw not found exception when repository returns empty")
  void shouldThrowNotFoundExceptionWhenRepositoryReturnsEmpty() {
    // given
    when(repository.findFirstBy()).thenReturn(Optional.empty());

    // when/then
    assertThatThrownBy(() -> service.loadDataVersion())
      .isInstanceOf(ResponseStatusException.class)
      .hasMessageContaining("Data version not found")
      .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
      .isEqualTo(HttpStatus.NOT_FOUND);

    verify(repository).findFirstBy();
  }

  @Test
  @DisplayName("should handle null fields in document")
  void shouldHandleNullFieldsInDocument() {
    // given
    DataVersionDocument document = new DataVersionDocument();
    document.setId(new ObjectId());
    document.setDataFile(null);
    document.setDataVersion(null);
    document.setTeamImagesId(null);
    when(repository.findFirstBy()).thenReturn(Optional.of(document));

    // when
    DataVersionDto result = service.loadDataVersion();

    // then
    assertThat(result).isNotNull();
    assertThat(result.getDataFile()).isNull();
    assertThat(result.getDataVersion()).isNull();
    assertThat(result.getTeamImagesId()).isNull();
  }

  private DataVersionDocument createDataVersionDocument() {
    DataVersionDocument document = new DataVersionDocument();
    document.setId(new ObjectId());
    document.setDataFile("syn000456");
    document.setDataVersion("1.0.0");
    document.setTeamImagesId("syn123456");
    return document;
  }
}
