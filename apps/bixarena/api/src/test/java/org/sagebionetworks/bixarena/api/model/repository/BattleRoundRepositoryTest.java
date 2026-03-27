package org.sagebionetworks.bixarena.api.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
import org.sagebionetworks.bixarena.api.model.entity.BattleRoundEntity;
import org.sagebionetworks.bixarena.api.model.entity.MessageEntity;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;
import org.sagebionetworks.bixarena.api.service.ServiceTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Tag("integration")
@Transactional
class BattleRoundRepositoryTest {

  @Autowired
  private BattleRoundRepository battleRoundRepository;

  @Autowired
  private ModelRepository modelRepository;

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private BattleRepository battleRepository;

  @Autowired
  private EntityManager entityManager;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  @MockitoBean
  private ServiceTokenProvider serviceTokenProvider;

  @Test
  @DisplayName("setModel1MessageId and setModel2MessageId should not overwrite each other")
  void targetedUpdatesShouldNotOverwrite() {
    ModelEntity model1 = modelRepository.save(ModelEntity.builder()
      .slug("race-model-1")
      .name("Model 1")
      .organization("TestOrg")
      .license("commercial")
      .active(true)
      .externalLink("https://example.com/1")
      .apiModelName("test/model-1")
      .apiBase("https://openrouter.ai/api/v1")
      .build());

    ModelEntity model2 = modelRepository.save(ModelEntity.builder()
      .slug("race-model-2")
      .name("Model 2")
      .organization("TestOrg")
      .license("commercial")
      .active(true)
      .externalLink("https://example.com/2")
      .apiModelName("test/model-2")
      .apiBase("https://openrouter.ai/api/v1")
      .build());

    MessageEntity prompt = messageRepository.save(MessageEntity.builder()
      .role("user")
      .content("test prompt")
      .build());

    MessageEntity msg1 = messageRepository.save(MessageEntity.builder()
      .role("assistant")
      .content("response from model 1")
      .build());

    MessageEntity msg2 = messageRepository.save(MessageEntity.builder()
      .role("assistant")
      .content("response from model 2")
      .build());

    BattleEntity battle = battleRepository.save(BattleEntity.builder()
      .userId(UUID.randomUUID())
      .model1Id(model1.getId())
      .model2Id(model2.getId())
      .build());

    BattleRoundEntity round = battleRoundRepository.save(BattleRoundEntity.builder()
      .battleId(battle.getId())
      .roundNumber(1)
      .promptMessageId(prompt.getId())
      .build());

    entityManager.flush();
    entityManager.clear();

    // Simulate two concurrent writes — each only updates its own column
    battleRoundRepository.setModel1MessageId(round.getId(), msg1.getId());
    battleRoundRepository.setModel2MessageId(round.getId(), msg2.getId());

    entityManager.flush();
    entityManager.clear();

    // Verify both columns are set — neither overwrote the other
    BattleRoundEntity result = battleRoundRepository.findById(round.getId()).orElseThrow();
    assertThat(result.getModel1MessageId()).isEqualTo(msg1.getId());
    assertThat(result.getModel2MessageId()).isEqualTo(msg2.getId());
  }
}
