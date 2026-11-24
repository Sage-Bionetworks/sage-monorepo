package org.sagebionetworks.qtl.api.api;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.explorers.ApiHelper;
import org.sagebionetworks.qtl.api.model.dto.EqtlPhenotypeDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlPhenotypeQueryDto;
import org.sagebionetworks.qtl.api.service.EqtlPhenotypeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EqtlPhenotypeApiDelegateImpl implements EqtlPhenotypeApiDelegate {

  private static final Set<String> VALID_QUERY_PARAMS = Set.of(
    "variant",
    "ensemblGeneId",
    "cellTypeId"
  );

  private final EqtlPhenotypeService eqtlPhenotypeService;

  @Override
  public ResponseEntity<EqtlPhenotypeDto> getEqtlPhenotype(EqtlPhenotypeQueryDto query) {
    log.debug("Fetching eQTL phenotype with query: {}", query);

    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    EqtlPhenotypeDto phenotype = eqtlPhenotypeService.getEqtlPhenotype(query);

    log.debug("Successfully retrieved eQTL phenotype for query: {}", query);

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(phenotype);
  }
}
