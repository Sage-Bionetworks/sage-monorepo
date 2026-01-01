package org.sagebionetworks.model.ad.api.next.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.model.dto.DataVersionDto;
import org.sagebionetworks.model.ad.api.next.service.DataVersionService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataVersionApiDelegateImpl implements DataVersionApiDelegate {

  private final DataVersionService dataVersionService;

  @Override
  public ResponseEntity<DataVersionDto> getDataVersion() {
    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    DataVersionDto dataVersion = dataVersionService.loadDataVersion();

    if (attrs != null) {
      HttpServletRequest req = attrs.getRequest();
      log.info("{} {} status={}", req.getMethod(), req.getRequestURI(), HttpStatus.OK.value());
    }

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(dataVersion);
  }
}
