package org.sagebionetworks.bixarena.api.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.BattleCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleCreateResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleEvaluationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleEvaluationDto;
import org.sagebionetworks.bixarena.api.model.dto.BattlePageDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleRoundCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleRoundDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleRoundUpdateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleUpdateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleValidationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.SetEffectiveValidationRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleValidationResponseDto;

import org.sagebionetworks.bixarena.api.model.dto.ModelChatCompletionChunkDto;
import org.sagebionetworks.bixarena.api.model.entity.BattleValidationEntity;
import org.sagebionetworks.bixarena.api.service.BattleEvaluationService;
import org.sagebionetworks.bixarena.api.service.BattleRoundService;
import org.sagebionetworks.bixarena.api.service.BattleService;
import org.sagebionetworks.bixarena.api.service.BattleValidationService;
import org.sagebionetworks.bixarena.api.service.ChatCompletionStreamService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class BattleApiDelegateImpl implements BattleApiDelegate {

  private final BattleService battleService;
  private final BattleRoundService battleRoundService;
  private final BattleEvaluationService battleEvaluationService;
  private final BattleValidationService battleValidationService;
  private final ChatCompletionStreamService chatCompletionStreamService;
  private final NativeWebRequest request;

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BattlePageDto> listBattles(BattleSearchQueryDto battleSearchQuery) {
    log.info("Listing battles with query: {}", battleSearchQuery);
    return ResponseEntity.ok(battleService.listBattles(battleSearchQuery));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BattleDto> getBattle(UUID battleId) {
    log.info("Getting battle with ID: {}", battleId);
    return ResponseEntity.ok(battleService.getBattle(battleId));
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<BattleCreateResponseDto> createBattle(
    BattleCreateRequestDto battleCreateRequestDto
  ) {
    // Get the authenticated user (SOURCE OF TRUTH from Spring Security)
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info(
      "User {} is creating battle with randomly selected models",
      authentication.getName()
    );

    // Pass authentication to service for security validation
    BattleCreateResponseDto createdBattle = battleService.createBattle(
      battleCreateRequestDto,
      authentication
    );
    return ResponseEntity.status(201).body(createdBattle);
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<BattleEvaluationDto> createBattleEvaluation(
    UUID battleId,
    BattleEvaluationCreateRequestDto battleEvaluationCreateRequestDto
  ) {
    log.info("Creating battle evaluation for battle {}", battleId);
    BattleEvaluationDto created = battleEvaluationService.createBattleEvaluation(
      battleId,
      battleEvaluationCreateRequestDto
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<BattleRoundDto> createBattleRound(
    UUID battleId,
    BattleRoundCreateRequestDto battleRoundCreateRequestDto
  ) {
    // Create the round (and its messages) and return DTO
    log.info("Creating round for battle {}", battleId);
    BattleRoundDto created = battleRoundService.createBattleRound(
      battleId,
      battleRoundCreateRequestDto
    );
    return ResponseEntity.status(201).body(created);
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<BattleDto> updateBattle(
    UUID battleId,
    BattleUpdateRequestDto battleUpdateRequestDto
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UUID callerId = UUID.fromString(authentication.getName());
    log.info("User {} is updating battle {}", callerId, battleId);

    BattleDto updatedBattle = battleService.updateBattle(
      battleId, battleUpdateRequestDto, callerId
    );
    return ResponseEntity.ok(updatedBattle);
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<BattleRoundDto> updateBattleRound(
    UUID battleId,
    UUID roundId,
    BattleRoundUpdateRequestDto battleRoundUpdateRequestDto
  ) {
    log.info("Updating round {} for battle {}", roundId, battleId);
    BattleRoundDto updated = battleRoundService.updateBattleRound(
      battleId,
      roundId,
      battleRoundUpdateRequestDto
    );
    return ResponseEntity.ok(updated);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BattleValidationResponseDto> createBattleValidation(
    UUID battleId,
    BattleValidationCreateRequestDto battleValidationCreateRequestDto
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UUID validatorId = UUID.fromString(authentication.getName());
    log.info("Admin {} creating battle validation for battle {}", validatorId, battleId);

    BattleValidationResponseDto dto = battleValidationService.createHumanValidation(
      battleId, battleValidationCreateRequestDto, validatorId
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<BattleValidationResponseDto>> listBattleValidations(UUID battleId) {
    log.info("Listing battle validations for battle {}", battleId);
    return ResponseEntity.ok(battleValidationService.listValidations(battleId));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BattleValidationResponseDto> runBattleValidation(
    UUID battleId
  ) {
    log.info("Admin triggering automated validation for battle {}", battleId);
    BattleValidationEntity entity = battleValidationService.validateAndPersistBattle(battleId);
    return ResponseEntity.status(HttpStatus.CREATED).body(battleValidationService.toDto(entity));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<BattleDto> setEffectiveValidation(
    UUID battleId,
    SetEffectiveValidationRequestDto request
  ) {
    log.info(
      "Admin setting effective validation for battle {}: {}",
      battleId, request.getValidationId()
    );
    BattleDto updated = battleService.setEffectiveValidation(
      battleId, request.getValidationId()
    );
    return ResponseEntity.ok(updated);
  }

  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<ModelChatCompletionChunkDto> streamBattleRoundCompletion(
    UUID battleId,
    UUID roundId,
    UUID modelId
  ) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UUID callerId = UUID.fromString(authentication.getName());
    log.info("User {} stream requested: battle={}, round={}, model={}", callerId, battleId, roundId, modelId);
    HttpServletResponse servletResponse = request.getNativeResponse(HttpServletResponse.class);
    chatCompletionStreamService.streamCompletion(battleId, roundId, modelId, callerId, servletResponse);
    // Response already written directly to HttpServletResponse
    return null;
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteBattle(UUID battleId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info("User {} is deleting battle: {}", authentication.getName(), battleId);

    battleService.deleteBattle(battleId);
    return ResponseEntity.noContent().build();
  }
}
