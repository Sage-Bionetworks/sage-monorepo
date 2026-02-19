package org.sagebionetworks.agora.api.next.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugDto;
import org.sagebionetworks.agora.api.next.service.NominatedDrugService;
import org.sagebionetworks.agora.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NominatedDrugApiDelegateImpl implements NominatedDrugApiDelegate {

  private final NominatedDrugService nominatedDrugService;

  @Override
  public ResponseEntity<List<NominatedDrugDto>> listNominatedDrugs() {
    log.debug("Fetching nominated drugs");
    List<NominatedDrugDto> drugs = nominatedDrugService.listNominatedDrugs();

    log.debug("Successfully retrieved {} nominated drugs", drugs.size());
    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(drugs);
  }
}
