package org.sagebionetworks.agora.api.next.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.agora.api.next.model.dto.DataVersionDto;
import org.sagebionetworks.agora.api.next.service.DataVersionService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DataVersionApiDelegateImplTest {

  @Mock
  private DataVersionService dataVersionService;

  private DataVersionApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    delegate = new DataVersionApiDelegateImpl(dataVersionService);
  }

  @Test
  @DisplayName("should return data version with ok status when service returns data")
  void shouldReturnDataVersionWithOkStatusWhenServiceReturnsData() {
    // given
    DataVersionDto dto = new DataVersionDto("syn000456", "1.0.0", "syn123456");
    when(dataVersionService.loadDataVersion()).thenReturn(dto);

    // when
    ResponseEntity<DataVersionDto> response = delegate.getDataVersion();

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(dto);
    assertThat(response.getBody().getDataFile()).isEqualTo("syn000456");
    assertThat(response.getBody().getDataVersion()).isEqualTo("1.0.0");
    assertThat(response.getBody().getTeamImagesId()).isEqualTo("syn123456");
    verify(dataVersionService).loadDataVersion();
  }

  @Test
  @DisplayName("should include no-cache headers in response")
  void shouldIncludeNoCacheHeadersInResponse() {
    // given
    DataVersionDto dto = new DataVersionDto("syn000456", "1.0.0", "syn123456");
    when(dataVersionService.loadDataVersion()).thenReturn(dto);

    // when
    ResponseEntity<DataVersionDto> response = delegate.getDataVersion();

    // then
    HttpHeaders headers = response.getHeaders();
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
    assertThat(headers.getPragma()).isEqualTo("no-cache");
    assertThat(headers.getExpires()).isZero();
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
  }

  @Test
  @DisplayName("should handle null team images id")
  void shouldHandleNullTeamImagesId() {
    // given
    DataVersionDto dto = new DataVersionDto("syn000456", "1.0.0", null);
    when(dataVersionService.loadDataVersion()).thenReturn(dto);

    // when
    ResponseEntity<DataVersionDto> response = delegate.getDataVersion();

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getTeamImagesId()).isNull();
  }
}
