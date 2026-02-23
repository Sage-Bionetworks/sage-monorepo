package org.sagebionetworks.agora.api.next.api;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugSearchQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugsPageDto;
import org.sagebionetworks.agora.api.next.service.NominatedDrugService;
import org.sagebionetworks.agora.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NominatedDrugApiDelegateImpl implements NominatedDrugApiDelegate {

  private static final Set<String> VALID_QUERY_PARAMS = Set.of(
    "pageNumber",
    "pageSize",
    "items",
    "itemFilterType",
    "search",
    "principalInvestigators",
    "totalNominations",
    "sortFields",
    "sortOrders"
  );

  private final NominatedDrugService nominatedDrugService;

  @Override
  public ResponseEntity<NominatedDrugsPageDto> getNominatedDrugs(
    NominatedDrugSearchQueryDto query
  ) {
    log.debug("Fetching nominated drugs with query: {}", query);

    // Validate query parameters
    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    NominatedDrugsPageDto page = nominatedDrugService.loadNominatedDrugs(query);

    log.debug("Successfully retrieved {} nominated drugs", page.getNominatedDrugs().size());

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(page);
  }
}
