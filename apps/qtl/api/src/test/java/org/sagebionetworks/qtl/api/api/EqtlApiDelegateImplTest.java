package org.sagebionetworks.qtl.api.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.qtl.api.model.dto.EqtlSearchQueryDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlsPageDto;
import org.sagebionetworks.qtl.api.service.EqtlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class EqtlApiDelegateImplTest {

  @Mock
  private EqtlService service;

  private MockHttpServletRequest request;
  private EqtlApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    delegate = new EqtlApiDelegateImpl(service);
  }

  @AfterEach
  void tearDown() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  @DisplayName("should return the service's page with no-cache headers when every param is known")
  void shouldReturnTheServicesPageWithNoCacheHeadersWhenEveryParamIsKnown() {
    // given
    request.setParameter("sortFields", "adj_p_val");
    request.setParameter("sortOrders", "1");
    request.setParameter("cellClass", "Astrocyte");
    EqtlSearchQueryDto query = new EqtlSearchQueryDto();
    EqtlsPageDto page = EqtlsPageDto.builder().eqtls(List.of()).build();
    when(service.loadEqtls(query)).thenReturn(page);

    // when
    ResponseEntity<EqtlsPageDto> response = delegate.getEqtls(query);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isSameAs(page);
    assertThat(response.getHeaders().getCacheControl()).isEqualTo(
      "no-cache, no-store, must-revalidate"
    );
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
  }

  @Test
  @DisplayName("should reject the request before querying when a param is unknown")
  void shouldRejectTheRequestBeforeQueryingWhenAParamIsUnknown() {
    // given
    request.setParameter("class", "Astrocyte");

    // when & then
    assertThatThrownBy(() -> delegate.getEqtls(new EqtlSearchQueryDto()))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Unknown query parameter: class");
    verifyNoInteractions(service);
  }
}
