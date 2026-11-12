package org.sagebionetworks.qtl.api.api;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.qtl.api.model.dto.EqtlSearchQueryDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlsPageDto;
import org.sagebionetworks.qtl.api.service.EqtlService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EqtlApiDelegateImpl implements EqtlApiDelegate {

  private static final Set<String> VALID_QUERY_PARAMS = Set.of(
    "pageNumber",
    "pageSize",
    "remainingBudget",
    "items",
    "itemFilterType",
    "search",
    "variant",
    "gene",
    "cellClass",
    "cellSubclass",
    "sortFields",
    "sortOrders"
  );

  private final EqtlService eqtlService;

  @Override
  public ResponseEntity<EqtlsPageDto> getEqtls(EqtlSearchQueryDto query) {
    log.debug("Fetching eQTLs with query: {}", query);

    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    EqtlsPageDto results = eqtlService.loadEqtls(query);

    log.debug("Successfully retrieved {} eQTLs", results.getEqtls().size());

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(results);
  }
}
