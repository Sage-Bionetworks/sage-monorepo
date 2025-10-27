package org.sagebionetworks.bixarena.api.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.exception.BattleNotFoundException;
import org.sagebionetworks.bixarena.api.exception.BattleRoundNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.BattleRoundDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleRoundPayloadDto;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
import org.sagebionetworks.bixarena.api.model.entity.BattleRoundEntity;
import org.sagebionetworks.bixarena.api.model.mapper.BattleRoundMapper;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.BattleRoundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BattleRoundService {

  private final BattleRoundRepository battleRoundRepository;
  private final BattleRepository battleRepository;
  private final MessageService messageService;
  private final BattleRoundMapper battleRoundMapper = new BattleRoundMapper();

  @Transactional
  public BattleRoundDto createBattleRound(UUID battleId, BattleRoundPayloadDto payload) {
    log.info("Creating battle round for battle {}", battleId);

    BattleEntity battle = battleRepository
      .findById(battleId)
      .orElseThrow(() ->
        new BattleNotFoundException(
          String.format("The battle with ID %s does not exist.", battleId)
        )
      );

    // Create messages as needed
    UUID promptId = payload.getPromptMessage() != null
      ? messageService.createMessage(payload.getPromptMessage())
      : null;
    UUID r1 = payload.getModel1Message() != null
      ? messageService.createMessage(payload.getModel1Message())
      : null;
    UUID r2 = payload.getModel2Message() != null
      ? messageService.createMessage(payload.getModel2Message())
      : null;

    Integer nextRoundNumber = battleRoundRepository
      .findFirstByBattleIdOrderByRoundNumberDesc(battleId)
      .map(BattleRoundEntity::getRoundNumber)
      .map(last -> last + 1)
      .orElse(1);

    BattleRoundEntity entity = BattleRoundEntity.builder()
      .battleId(battle.getId())
      .roundNumber(nextRoundNumber)
      .promptMessageId(promptId)
      .model1MessageId(r1)
      .model2MessageId(r2)
      .build();

    BattleRoundEntity saved = battleRoundRepository.save(entity);
    battleRoundRepository.flush();

    log.info("Created battle round {} for battle {}", saved.getId(), battleId);

    return battleRoundMapper.convertToDto(saved);
  }

  @Transactional
  public BattleRoundDto updateBattleRound(
    UUID battleId,
    UUID roundId,
    BattleRoundPayloadDto payload
  ) {
    log.info("Updating battle round {} for battle {}", roundId, battleId);

    // Verify battle exists
    battleRepository
      .findById(battleId)
      .orElseThrow(() ->
        new BattleNotFoundException(
          String.format("The battle with ID %s does not exist.", battleId)
        )
      );

    BattleRoundEntity existing = battleRoundRepository
      .findById(roundId)
      .orElseThrow(() ->
        new BattleRoundNotFoundException(
          String.format("The battle round with ID %s does not exist.", roundId)
        )
      );

    // Ensure the round belongs to the provided battle
    if (!existing.getBattleId().equals(battleId)) {
      throw new BattleRoundNotFoundException(
        String.format("The battle with ID %s does not contain round %s.", battleId, roundId)
      );
    }

    // If payload contains new messages, create them and set the corresponding ids
    if (payload.getPromptMessage() != null) {
      existing.setPromptMessageId(messageService.createMessage(payload.getPromptMessage()));
    }
    if (payload.getModel1Message() != null) {
      existing.setModel1MessageId(messageService.createMessage(payload.getModel1Message()));
    }
    if (payload.getModel2Message() != null) {
      existing.setModel2MessageId(messageService.createMessage(payload.getModel2Message()));
    }

    BattleRoundEntity saved = battleRoundRepository.save(existing);
    log.info("Updated battle round {}", saved.getId());

    return battleRoundMapper.convertToDto(saved);
  }
}
