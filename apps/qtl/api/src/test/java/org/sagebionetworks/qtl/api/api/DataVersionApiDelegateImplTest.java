package org.sagebionetworks.qtl.api.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.qtl.api.model.dto.DataVersionDto;
import org.sagebionetworks.qtl.api.service.DataVersionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DataVersionApiDelegateImplTest {

  @Mock
  private DataVersionService service;

  private DataVersionApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    delegate = new DataVersionApiDelegateImpl(service);
  }

  @Test
  @DisplayName("should return data version with no-cache headers")
  void shouldReturnDataVersionWithNoCacheHeaders() {
    // given
    DataVersionDto dto = new DataVersionDto("syn12345678", "42");
    when(service.loadDataVersion()).thenReturn(dto);

    // when
    ResponseEntity<DataVersionDto> response = delegate.getDataVersion();

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(dto);
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    assertThat(response.getHeaders().getCacheControl())
      .isNotNull()
      .contains("no-cache")
      .contains("no-store")
      .contains("must-revalidate");

    verify(service).loadDataVersion();
  }
}
