package org.sagebionetworks.bixarena.api.api;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.BattleCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleDto;
import org.sagebionetworks.bixarena.api.model.dto.BattlePageDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.BattleUpdateRequestDto;
import org.sagebionetworks.bixarena.api.service.BattleService;
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
  private final NativeWebRequest request;

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return Optional.ofNullable(request);
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_read:battles')")
  public ResponseEntity<BattlePageDto> listBattles(BattleSearchQueryDto battleSearchQuery) {
    log.info("Listing battles with query: {}", battleSearchQuery);
    return ResponseEntity.ok(battleService.listBattles(battleSearchQuery));
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_read:battles')")
  public ResponseEntity<BattleDto> getBattle(UUID battleId) {
    log.info("Getting battle with ID: {}", battleId);
    return ResponseEntity.ok(battleService.getBattle(battleId));
  }

  @Override
  public ResponseEntity<BattleDto> createBattle(BattleCreateRequestDto battleCreateRequestDto) {
    // Get the authenticated user (SOURCE OF TRUTH from Spring Security)
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info(
      "User {} is creating battle with models: {} vs {}",
      authentication.getName(),
      battleCreateRequestDto.getModel1Id(),
      battleCreateRequestDto.getModel2Id()
    );

    // Pass authentication to service for security validation
    BattleDto createdBattle = battleService.createBattle(battleCreateRequestDto, authentication);
    return ResponseEntity.status(201).body(createdBattle);
  }

  @Override
  // @PreAuthorize("hasAuthority('SCOPE_update:battles')") // Disabled for anonymous access
  public ResponseEntity<BattleDto> updateBattle(
    UUID battleId,
    BattleUpdateRequestDto battleUpdateRequestDto
  ) {
    // Log the authenticated user for audit purposes
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info("User {} is updating battle {}", authentication.getName(), battleId);

    BattleDto updatedBattle = battleService.updateBattle(battleId, battleUpdateRequestDto);
    return ResponseEntity.ok(updatedBattle);
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_delete:battles')")
  public ResponseEntity<Void> deleteBattle(UUID battleId) {
    // Log the authenticated user for audit purposes
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info("User {} is deleting battle: {}", authentication.getName(), battleId);

    battleService.deleteBattle(battleId);
    return ResponseEntity.noContent().build();
  }
}
