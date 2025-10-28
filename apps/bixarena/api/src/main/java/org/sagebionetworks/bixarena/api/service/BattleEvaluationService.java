package org.sagebionetworks.bixarena.api.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.exception.BattleNotFoundException;
import org.sagebionetworks.bixarena.api.exception.DuplicateBattleEvaluationException;
import org.sagebionetworks.bixarena.api.model.dto.BattleEvaluationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleEvaluationDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleEvaluationOutcomeDto;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
import org.sagebionetworks.bixarena.api.model.entity.BattleEvaluationEntity;
import org.sagebionetworks.bixarena.api.model.mapper.BattleEvaluationMapper;
import org.sagebionetworks.bixarena.api.model.repository.BattleEvaluationRepository;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to handle BattleEvaluation operations and persist BattleEvaluation entities.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class BattleEvaluationService {

  private final BattleEvaluationRepository battleEvaluationRepository;
  private final BattleRepository battleRepository;
  private final BattleEvaluationMapper battleEvaluationMapper = new BattleEvaluationMapper();

  @Transactional
  public BattleEvaluationDto createBattleEvaluation(
    UUID battleId,
    BattleEvaluationCreateRequestDto request
  ) {
    log.info("Creating battle evaluation for battle ID: {}", battleId);

    // Validate battle exists
    getBattleEntity(battleId);

    BattleEvaluationEntity entity = BattleEvaluationEntity.builder()
      .battleId(battleId)
      .outcome(mapOutcomeToEntityOutcome(request.getOutcome()))
      .build();

    try {
      BattleEvaluationEntity saved = battleEvaluationRepository.save(entity);
      battleEvaluationRepository.flush();

      return battleEvaluationMapper.convertToDto(saved);
    } catch (DataIntegrityViolationException e) {
      // Detect unique constraint violation for duplicate battle evaluation and translate
      if (e.getMessage() != null && e.getMessage().contains("unique_battle_evaluation")) {
        throw new DuplicateBattleEvaluationException(battleId);
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

  private String mapOutcomeToEntityOutcome(BattleEvaluationOutcomeDto outcome) {
    if (outcome == null) return null;
    return outcome.getValue();
  }
}
