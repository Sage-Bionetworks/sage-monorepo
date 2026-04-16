package org.sagebionetworks.bixarena.api.api;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptCategorizationCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptCategorizationResponseDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptPageDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSearchQueryDto;
import org.sagebionetworks.bixarena.api.service.ExamplePromptCategorizationService;
import org.sagebionetworks.bixarena.api.service.ExamplePromptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExamplePromptApiDelegateImpl implements ExamplePromptApiDelegate {

  private final ExamplePromptService examplePromptService;
  private final ExamplePromptCategorizationService categorizationService;

  @Override
  public ResponseEntity<ExamplePromptPageDto> listExamplePrompts(
    ExamplePromptSearchQueryDto examplePromptSearchQuery
  ) {
    return ResponseEntity.ok(examplePromptService.listExamplePrompts(examplePromptSearchQuery));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ExamplePromptDto> createExamplePrompt(
    ExamplePromptCreateRequestDto request
  ) {
    UUID createdBy = currentUserId();
    log.info("Admin {} creating example prompt", createdBy);
    ExamplePromptDto dto = examplePromptService.createExamplePrompt(request, createdBy);
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @Override
  public ResponseEntity<ExamplePromptDto> getExamplePrompt(UUID examplePromptId) {
    return ResponseEntity.ok(examplePromptService.getExamplePrompt(examplePromptId));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ExamplePromptDto> updateExamplePrompt(
    UUID examplePromptId,
    ExamplePromptCreateRequestDto request
  ) {
    UUID updatedBy = currentUserId();
    log.info("Admin {} updating example prompt {}", updatedBy, examplePromptId);
    ExamplePromptDto dto = examplePromptService.updateExamplePrompt(
      examplePromptId,
      request,
      updatedBy
    );
    return ResponseEntity.ok(dto);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteExamplePrompt(UUID examplePromptId) {
    log.info("Admin {} deleting example prompt {}", currentUserId(), examplePromptId);
    examplePromptService.deleteExamplePrompt(examplePromptId);
    return ResponseEntity.noContent().build();
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<ExamplePromptCategorizationResponseDto>> listExamplePromptCategorizations(
    UUID examplePromptId
  ) {
    return ResponseEntity.ok(categorizationService.listCategorizations(examplePromptId));
  }

  @Override
  public ResponseEntity<ExamplePromptCategorizationResponseDto> getExamplePromptEffectiveCategorization(
    UUID examplePromptId
  ) {
    return ResponseEntity.ok(categorizationService.getEffectiveCategorization(examplePromptId));
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ExamplePromptCategorizationResponseDto> createExamplePromptCategorization(
    UUID examplePromptId,
    ExamplePromptCategorizationCreateRequestDto request
  ) {
    UUID categorizedBy = currentUserId();
    log.info("Admin {} creating categorization override for prompt {}", categorizedBy, examplePromptId);
    ExamplePromptCategorizationResponseDto dto = categorizationService.createManualCategorization(
      examplePromptId,
      request,
      categorizedBy
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ExamplePromptCategorizationResponseDto> runExamplePromptCategorization(
    UUID examplePromptId
  ) {
    log.info("Admin {} triggering AI categorization for prompt {}", currentUserId(), examplePromptId);
    ExamplePromptCategorizationResponseDto dto = categorizationService.categorizePrompt(
      examplePromptId
    );
    return ResponseEntity.status(HttpStatus.CREATED).body(dto);
  }

  private UUID currentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return UUID.fromString(authentication.getName());
  }
}
