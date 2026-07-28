package org.sagebionetworks.model.ad.api.next.api;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;
import org.sagebionetworks.model.ad.api.next.service.ModelService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModelApiDelegateImpl implements ModelApiDelegate {

  private final ModelService modelService;

  @Override
  public ResponseEntity<ModelDto> getModelByName(ModelOrganismDto modelOrganism, String name) {
    // Manually decode the URL-encoded name parameter
    // This is necessary because we use pass-through for encoded slashes
    String decodedName = URLDecoder.decode(name, StandardCharsets.UTF_8);

    log.debug("Fetching {} model by name: {} (decoded from: {})", modelOrganism, decodedName, name);
    ModelDto model = modelService.getModelByName(modelOrganism, decodedName);

    log.debug("Successfully retrieved {} model: {}", modelOrganism, decodedName);
    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(model);
  }
}
