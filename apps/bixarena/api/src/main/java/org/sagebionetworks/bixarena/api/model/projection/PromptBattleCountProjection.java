package org.sagebionetworks.bixarena.api.model.projection;

import java.util.UUID;

/**
 * Projection interface for example-prompt battle count query results.
 * Used by ExamplePromptRepository.findBattleCountsByPromptIds()
 */
public interface PromptBattleCountProjection {

  UUID getPromptId();

  Integer getBattleCount();
}
