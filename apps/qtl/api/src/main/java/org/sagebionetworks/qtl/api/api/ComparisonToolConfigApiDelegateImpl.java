package org.sagebionetworks.qtl.api.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolPageDto;
import org.sagebionetworks.qtl.api.service.ComparisonToolConfigService;
import org.sagebionetworks.qtl.api.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ComparisonToolConfigApiDelegateImpl implements ComparisonToolConfigApiDelegate {

  private final ComparisonToolConfigService comparisonToolConfigService;

  @Override
  public ResponseEntity<List<ComparisonToolConfigDto>> getComparisonToolConfig(
    ComparisonToolPageDto page
  ) {
    log.debug("Fetching comparison tool config by page: {}", page);
    List<ComparisonToolConfigDto> config = comparisonToolConfigService.getConfigsByPage(page);

    log.debug("Successfully retrieved comparison tool config for page: {}", page);
    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(config);
  }
}
