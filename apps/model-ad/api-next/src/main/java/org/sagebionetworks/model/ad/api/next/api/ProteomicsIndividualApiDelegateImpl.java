package org.sagebionetworks.model.ad.api.next.api;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualFilterQueryDto;
import org.sagebionetworks.model.ad.api.next.service.ProteomicsIndividualService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProteomicsIndividualApiDelegateImpl implements ProteomicsIndividualApiDelegate {

  private static final Set<String> VALID_QUERY_PARAMS = Set.of(
    "tissue",
    "modelIdentifier",
    "modelIdentifierType",
    "uniqueId"
  );

  private final ProteomicsIndividualService proteomicsIndividualService;

  @Override
  public ResponseEntity<List<ProteomicsIndividualDto>> getProteomicsIndividual(
    ProteomicsIndividualFilterQueryDto query
  ) {
    log.debug("Fetching proteomics individual data with query: {}", query);

    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    List<ProteomicsIndividualDto> list = proteomicsIndividualService.getProteomicsIndividual(query);

    log.debug("Successfully retrieved {} proteomics individual data", list.size());

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(list);
  }
}
