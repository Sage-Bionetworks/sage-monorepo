package org.sagebionetworks.bixarena.api.api;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.BattleEvaluationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleEvaluationDto;
import org.sagebionetworks.bixarena.api.service.BattleEvaluationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class BattleEvaluationApiDelegateImpl implements BattleEvaluationApiDelegate {

  private final BattleEvaluationService battleEvaluationService;
  private final NativeWebRequest request;

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  public ResponseEntity<BattleEvaluationDto> createBattleEvaluation(
    UUID battleId,
    BattleEvaluationCreateRequestDto battleEvaluationCreateRequestDto
  ) {
    log.info("Creating battle evaluation for battle ID: {}", battleId);
    BattleEvaluationDto created = battleEvaluationService.createBattleEvaluation(
      battleId,
      battleEvaluationCreateRequestDto
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }
}
