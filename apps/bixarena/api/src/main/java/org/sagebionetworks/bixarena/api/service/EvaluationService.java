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
import org.sagebionetworks.bixarena.api.model.mapper.EvaluationMapper;
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
  private final EvaluationMapper evaluationMapper = new EvaluationMapper();

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
      .isValid(false)
      .validationError(null)
      .build();

    try {
      EvaluationEntity saved = evaluationRepository.save(entity);
      evaluationRepository.flush();

      return evaluationMapper.convertToDto(saved);
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
}
