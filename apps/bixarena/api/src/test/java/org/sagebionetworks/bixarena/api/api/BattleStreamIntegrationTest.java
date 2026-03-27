package org.sagebionetworks.bixarena.api.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.bixarena.api.model.entity.BattleEntity;
import org.sagebionetworks.bixarena.api.model.entity.BattleRoundEntity;
import org.sagebionetworks.bixarena.api.model.entity.MessageEntity;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.BattleRoundRepository;
import org.sagebionetworks.bixarena.api.model.repository.MessageRepository;
import org.sagebionetworks.bixarena.api.model.repository.ModelRepository;
import org.sagebionetworks.bixarena.api.service.ServiceTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration")
@Transactional
class BattleStreamIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private BattleRepository battleRepository;

  @Autowired
  private BattleRoundRepository battleRoundRepository;

  @Autowired
  private ModelRepository modelRepository;

  @Autowired
  private MessageRepository messageRepository;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  @MockitoBean
  private ServiceTokenProvider serviceTokenProvider;

  private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

  private ModelEntity model1;
  private BattleEntity battle;
  private BattleRoundEntity round;

  @BeforeEach
  void setUp() {
    model1 = modelRepository.save(ModelEntity.builder()
      .slug("test-model-1")
      .name("Test Model 1")
      .organization("TestOrg")
      .license("commercial")
      .active(true)
      .externalLink("https://example.com/model1")
      .apiModelName("test/model-1")
      .apiBase("https://openrouter.ai/api/v1")
      .build());

    ModelEntity model2 = modelRepository.save(ModelEntity.builder()
      .slug("test-model-2")
      .name("Test Model 2")
      .organization("TestOrg")
      .license("commercial")
      .active(true)
      .externalLink("https://example.com/model2")
      .apiModelName("test/model-2")
      .apiBase("https://openrouter.ai/api/v1")
      .build());

    MessageEntity promptMessage = messageRepository.save(MessageEntity.builder()
      .role("user")
      .content("What genes are associated with Alzheimer disease?")
      .build());

    battle = battleRepository.save(BattleEntity.builder()
      .userId(USER_ID)
      .model1Id(model1.getId())
      .model2Id(model2.getId())
      .build());

    round = battleRoundRepository.save(BattleRoundEntity.builder()
      .battleId(battle.getId())
      .roundNumber(1)
      .promptMessageId(promptMessage.getId())
      .build());
  }

  @Test
  @DisplayName("should return 403 when streaming another user's battle")
  @WithMockUser(username = "00000000-0000-0000-0000-000000000001", roles = "USER")
  void shouldReturn403ForOtherUserBattle() throws Exception {
    mockMvc.perform(
        post("/v1/battles/{battleId}/rounds/{roundId}/stream", battle.getId(), round.getId())
          .param("modelId", model1.getId().toString()))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.status").value(403));
  }

  @Test
  @DisplayName("should return 404 when model does not belong to battle")
  @WithMockUser(username = "11111111-1111-1111-1111-111111111111", roles = "USER")
  void shouldReturn404ForInvalidModel() throws Exception {
    UUID fakeModelId = UUID.randomUUID();
    mockMvc.perform(
        post("/v1/battles/{battleId}/rounds/{roundId}/stream", battle.getId(), round.getId())
          .param("modelId", fakeModelId.toString()))
      .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("should return 404 when battle does not exist")
  @WithMockUser(username = "11111111-1111-1111-1111-111111111111", roles = "USER")
  void shouldReturn404ForNonexistentBattle() throws Exception {
    UUID fakeBattleId = UUID.randomUUID();
    mockMvc.perform(
        post("/v1/battles/{battleId}/rounds/{roundId}/stream", fakeBattleId, round.getId())
          .param("modelId", model1.getId().toString()))
      .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("should return 401 when not authenticated")
  void shouldReturn401WhenNotAuthenticated() throws Exception {
    mockMvc.perform(
        post("/v1/battles/{battleId}/rounds/{roundId}/stream", battle.getId(), round.getId())
          .param("modelId", model1.getId().toString()))
      .andExpect(status().isUnauthorized());
  }
}
