package org.sagebionetworks.model.ad.api.next.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDto;
import org.sagebionetworks.model.ad.api.next.service.ModelService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModelApiDelegateImpl implements ModelApiDelegate {

  private final ModelService modelService;

  @Override
  public ResponseEntity<ModelDto> getModelByName(String name) {
    // Manually decode the URL-encoded name parameter
    // This is necessary because we use pass-through for encoded slashes
    String decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8);

    log.debug("Fetching model by name: {} (decoded from: {})", decodedName, name);
    ModelDto model = modelService.getModelByName(decodedName);

    ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (model == null) {
      log.debug("Model not found: {}", decodedName);
      if (attrs != null) {
        HttpServletRequest req = attrs.getRequest();
        log.info("{} {} status={}", req.getMethod(), req.getRequestURI(), HttpStatus.NOT_FOUND.value());
      }
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    log.debug("Successfully retrieved model: {}", decodedName);
    if (attrs != null) {
      HttpServletRequest req = attrs.getRequest();
      log.info("{} {} status={}", req.getMethod(), req.getRequestURI(), HttpStatus.OK.value());
    }
    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(model);
  }
}
