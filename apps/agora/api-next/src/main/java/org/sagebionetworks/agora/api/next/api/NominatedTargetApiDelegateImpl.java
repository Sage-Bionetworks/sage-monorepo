package org.sagebionetworks.agora.api.next.api;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetsPageDto;
import org.sagebionetworks.agora.api.next.service.NominatedTargetService;
import org.sagebionetworks.agora.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NominatedTargetApiDelegateImpl implements NominatedTargetApiDelegate {

  private static final Set<String> VALID_QUERY_PARAMS = Set.of(
    "pageNumber",
    "pageSize",
    "items",
    "itemFilterType",
    "search",
    "cohortStudies",
    "inputData",
    "initialNomination",
    "nominatingTeams",
    "pharosClass",
    "programs",
    "totalNominations",
    "sortFields",
    "sortOrders"
  );

  private final NominatedTargetService nominatedTargetService;

  @Override
  public ResponseEntity<NominatedTargetsPageDto> getNominatedTargets(
    NominatedTargetSearchQueryDto query
  ) {
    log.debug("Fetching nominated targets with query: {}", query);

    // Validate query parameters
    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    NominatedTargetsPageDto page = nominatedTargetService.loadNominatedTargets(query);

    log.debug("Successfully retrieved {} nominated targets", page.getNominatedTargets().size());

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(page);
  }
}
