package org.sagebionetworks.model.ad.api.next.api;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsIndividualFilterQueryDto;
import org.sagebionetworks.model.ad.api.next.service.TranscriptomicsIndividualService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TranscriptomicsIndividualApiDelegateImpl
  implements TranscriptomicsIndividualApiDelegate {

  private static final Set<String> VALID_QUERY_PARAMS = Set.of(
    "tissue",
    "modelIdentifier",
    "modelIdentifierType",
    "ensemblGeneId"
  );

  private final TranscriptomicsIndividualService transcriptomicsIndividualService;

  @Override
  public ResponseEntity<List<TranscriptomicsIndividualDto>> getTranscriptomicsIndividual(
    TranscriptomicsIndividualFilterQueryDto query
  ) {
    log.debug("Fetching transcriptomics individual data with query: {}", query);

    ApiHelper.validateQueryParameters(VALID_QUERY_PARAMS);

    List<TranscriptomicsIndividualDto> list =
      transcriptomicsIndividualService.getTranscriptomicsIndividual(query);

    log.debug("Successfully retrieved {} transcriptomics individual data", list.size());

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(list);
  }
}
