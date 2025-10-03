package org.sagebionetworks.bixarena.api.api;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptPageDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSearchQueryDto;
import org.sagebionetworks.bixarena.api.service.ExamplePromptService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExamplePromptApiDelegateImpl implements ExamplePromptApiDelegate {

  private final ExamplePromptService examplePromptService;

  @Override
  public ResponseEntity<ExamplePromptPageDto> listExamplePrompts(
    ExamplePromptSearchQueryDto examplePromptSearchQuery
  ) {
    return ResponseEntity.ok(examplePromptService.listExamplePrompts(examplePromptSearchQuery));
  }
}
