package org.sagebionetworks.bixarena.api.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.exception.BattleNotFoundException;
import org.sagebionetworks.bixarena.api.exception.DuplicateEvaluationException;
import org.sagebionetworks.bixarena.api.model.dto.EvaluationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.EvaluationDto;
import org.sagebionetworks.bixarena.api.model.dto.EvaluationOutcomeDto;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
import org.sagebionetworks.bixarena.api.model.entity.EvaluationEntity;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.EvaluationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to handle Evaluation operations and persist Evaluation entities.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class EvaluationService {

  private final EvaluationRepository evaluationRepository;
  private final BattleRepository battleRepository;

  @Transactional
  public EvaluationDto createEvaluation(UUID battleId, EvaluationCreateRequestDto request) {
    log.info("Creating evaluation for battle ID: {}", battleId);

    // Validate battle exists
    getBattleEntity(battleId);

    // Map outcome to EvaluationEntity.Outcome
    EvaluationEntity.Outcome outcome = mapOutcomeToEntityOutcome(request.getOutcome());

    EvaluationEntity entity = EvaluationEntity.builder()
      .battleId(battleId)
      .outcome(outcome)
      .build();

    try {
      EvaluationEntity saved = evaluationRepository.save(entity);
      evaluationRepository.flush();

      // Map saved entity to EvaluationDto
      EvaluationOutcomeDto outcomeDto = mapEntityOutcomeToDto(saved.getOutcome());

      return EvaluationDto.builder()
        .id(saved.getId())
        .outcome(outcomeDto)
        .createdAt(saved.getCreatedAt())
        .build();
    } catch (DataIntegrityViolationException e) {
      if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique")) {
        // Best-effort detect unique constraint violation for duplicate evaluation
        throw new DuplicateEvaluationException(battleId);
      }
      throw e;
    }
  }

  private BattleEntity getBattleEntity(UUID battleId) {
    return battleRepository
      .findById(battleId)
      .orElseThrow(() ->
        new BattleNotFoundException(String.format("Battle not found with ID: %s", battleId))
      );
  }

  private EvaluationEntity.Outcome mapOutcomeToEntityOutcome(EvaluationOutcomeDto outcome) {
    if (outcome == null) return null;
    return switch (outcome) {
      case MODEL1 -> EvaluationEntity.Outcome.MODEL_1;
      case MODEL2 -> EvaluationEntity.Outcome.MODEL_2;
      case TIE -> EvaluationEntity.Outcome.TIE;
    };
  }

  private EvaluationOutcomeDto mapEntityOutcomeToDto(EvaluationEntity.Outcome outcome) {
    if (outcome == null) return null;
    return switch (outcome) {
      case MODEL_1 -> EvaluationOutcomeDto.MODEL1;
      case MODEL_2 -> EvaluationOutcomeDto.MODEL2;
      case TIE -> EvaluationOutcomeDto.TIE;
    };
  }
}
