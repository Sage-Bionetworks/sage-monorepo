package org.sagebionetworks.model.ad.api.next.api;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionIndividualFilterQueryDto;
import org.sagebionetworks.model.ad.api.next.service.GeneExpressionIndividualService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeneExpressionIndividualApiDelegateImpl
  implements GeneExpressionIndividualApiDelegate {

  private static final Set<String> VALID_QUERY_PARAMS = Set.of(
    "tissue",
    "modelIdentifier",
    "modelIdentifierType",
    "ensemblGeneId"
  );

  private final GeneExpressionIndividualService geneExpressionIndividualService;

  @Override
  public ResponseEntity<List<GeneExpressionIndividualDto>> getGeneExpressionIndividual(
    GeneExpressionIndividualFilterQueryDto query
  ) {
    log.debug("Fetching gene expression individual data with query: {}", query);

    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    List<GeneExpressionIndividualDto> list =
      geneExpressionIndividualService.getGeneExpressionIndividual(query);

    log.debug("Successfully retrieved {} gene expression individual data", list.size());

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(list);
  }
}
