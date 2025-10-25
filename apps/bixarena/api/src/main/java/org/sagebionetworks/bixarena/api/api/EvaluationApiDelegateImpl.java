package org.sagebionetworks.bixarena.api.api;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.EvaluationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.EvaluationDto;
import org.sagebionetworks.bixarena.api.service.EvaluationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class EvaluationApiDelegateImpl implements EvaluationApiDelegate {

  private final EvaluationService evaluationService;
  private final NativeWebRequest request;

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<EvaluationDto> createEvaluation(
    UUID battleId,
    EvaluationCreateRequestDto evaluationCreateRequestDto
  ) {
    log.info("Creating evaluation for battle ID: {}", battleId);
    EvaluationDto created = evaluationService.createEvaluation(
      battleId,
      evaluationCreateRequestDto
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }
}
