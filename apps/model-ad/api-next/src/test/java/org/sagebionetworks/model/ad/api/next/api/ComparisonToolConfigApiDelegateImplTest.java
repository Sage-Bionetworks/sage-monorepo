package org.sagebionetworks.model.ad.api.next.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolPageDto;
import org.sagebionetworks.model.ad.api.next.service.ComparisonToolConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class ComparisonToolConfigApiDelegateImplTest {

  @Mock
  private ComparisonToolConfigService service;

  private ComparisonToolConfigApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    ServletRequestAttributes attributes = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(attributes);

    delegate = new ComparisonToolConfigApiDelegateImpl(service);
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  @DisplayName("should return configs when service returns data")
  void shouldReturnConfigsWhenServiceReturnsData() {
    // given
    ComparisonToolPageDto page = ComparisonToolPageDto.MODEL_OVERVIEW;
    ComparisonToolConfigDto config1 = new ComparisonToolConfigDto();
    config1.setPage(page);
    ComparisonToolConfigDto config2 = new ComparisonToolConfigDto();
    config2.setPage(page);

    when(service.getConfigsByPage(page)).thenReturn(List.of(config1, config2));

    // when
    ResponseEntity<List<ComparisonToolConfigDto>> response = delegate.getComparisonToolConfig(page);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
    assertThat(response.getBody()).containsExactly(config1, config2);
    assertThat(response.getHeaders().getCacheControl()).contains("no-cache");
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);

    verify(service).getConfigsByPage(page);
  }

  @Test
  @DisplayName("should set no-cache headers in response")
  void shouldSetNoCacheHeadersInResponse() {
    // given
    ComparisonToolPageDto page = ComparisonToolPageDto.DISEASE_CORRELATION;
    ComparisonToolConfigDto config = new ComparisonToolConfigDto();
    config.setPage(page);
    when(service.getConfigsByPage(page)).thenReturn(List.of(config));

    // when
    ResponseEntity<List<ComparisonToolConfigDto>> response = delegate.getComparisonToolConfig(page);

    // then
    assertThat(response.getHeaders().getCacheControl())
      .isNotNull()
      .contains("no-cache")
      .contains("no-store")
      .contains("must-revalidate");

    verify(service).getConfigsByPage(page);
  }

  @Test
  @DisplayName("should handle gene expression page")
  void shouldHandleGeneExpressionPage() {
    // given
    ComparisonToolPageDto page = ComparisonToolPageDto.GENE_EXPRESSION;
    ComparisonToolConfigDto config = new ComparisonToolConfigDto();
    config.setPage(page);

    when(service.getConfigsByPage(page)).thenReturn(List.of(config));

    // when
    ResponseEntity<List<ComparisonToolConfigDto>> response = delegate.getComparisonToolConfig(page);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(1);
    assertThat(response.getBody().get(0).getPage()).isEqualTo(page);

    verify(service).getConfigsByPage(page);
  }
}
