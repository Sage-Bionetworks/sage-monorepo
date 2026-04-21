package org.sagebionetworks.agora.api.next.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.model.dto.DrugDto;
import org.sagebionetworks.agora.api.next.service.DrugService;
import org.sagebionetworks.agora.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DrugApiDelegateImpl implements DrugApiDelegate {

  private final DrugService drugService;

  @Override
  public ResponseEntity<DrugDto> getDrug(String chemblId) {
    log.debug("Fetching drug details for ChEMBL ID: {}", chemblId);

    DrugDto drug = drugService.loadDrug(chemblId);

    log.debug("Successfully retrieved drug: {}", drug.getCommonName());

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(drug);
  }
}
