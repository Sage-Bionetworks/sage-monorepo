package org.sagebionetworks.bixarena.api.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.bixarena.api.model.dto.QuestCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.entity.QuestEntity;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.QuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class QuestApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private QuestRepository questRepository;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  @MockitoBean
  private BattleRepository battleRepository;

  @BeforeEach
  void setUp() {
    // Stub native queries that are not compatible with H2
    when(battleRepository.countCompletedByDateRange(any(), any())).thenReturn(0L);
  }

  private QuestEntity seedQuest(String questId) {
    QuestEntity quest = QuestEntity.builder()
        .questId(questId)
        .title("Test Quest")
        .description("A test quest")
        .goal(100)
        .startDate(OffsetDateTime.now().minusDays(1))
        .endDate(OffsetDateTime.now().plusDays(30))
        .activePostIndex(0)
        .build();
    return questRepository.save(quest);
  }

  private QuestCreateOrUpdateDto buildCreateDto(String questId) {
    return QuestCreateOrUpdateDto.builder()
        .questId(questId)
        .title("New Quest")
        .description("A new quest")
        .goal(200)
        .startDate(OffsetDateTime.now().minusDays(1))
        .endDate(OffsetDateTime.now().plusDays(30))
        .activePostIndex(0)
        .build();
  }

  @Test
  @DisplayName("should return 200 when getting quest anonymously")
  void shouldReturn200WhenGetQuestAnonymously() throws Exception {
    // given
    seedQuest("public-quest");

    // when/then
    mockMvc.perform(get("/v1/quests/public-quest"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.questId").value("public-quest"));
  }

  @Test
  @DisplayName("should return 403 when creating quest anonymously")
  void shouldReturn403WhenCreateQuestAnonymously() throws Exception {
    // given
    QuestCreateOrUpdateDto dto = buildCreateDto("new-quest");

    // when/then — /v1/quests is permitAll, so anonymous passes the filter chain
    // but @PreAuthorize("hasRole('ADMIN')") denies access → 403
    mockMvc.perform(post("/v1/quests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("should return 403 when creating quest as non-admin user")
  @WithMockUser(roles = "USER")
  void shouldReturn403WhenCreateQuestAsUser() throws Exception {
    // given
    QuestCreateOrUpdateDto dto = buildCreateDto("new-quest");

    // when/then
    mockMvc.perform(post("/v1/quests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("should return 201 when creating quest as admin")
  @WithMockUser(roles = "ADMIN")
  void shouldReturn201WhenCreateQuestAsAdmin() throws Exception {
    // given
    QuestCreateOrUpdateDto dto = buildCreateDto("admin-quest");

    // when/then
    mockMvc.perform(post("/v1/quests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.questId").value("admin-quest"));
  }

  @Test
  @DisplayName("should return 204 when deleting quest as admin")
  @WithMockUser(roles = "ADMIN")
  void shouldReturn204WhenDeleteQuestAsAdmin() throws Exception {
    // given
    seedQuest("delete-me");

    // when/then
    mockMvc.perform(delete("/v1/quests/delete-me"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("should return 403 when deleting quest anonymously")
  void shouldReturn403WhenDeleteQuestAnonymously() throws Exception {
    // given
    seedQuest("protected-quest");

    // when/then — same as create: permitAll path + @PreAuthorize → 403
    mockMvc.perform(delete("/v1/quests/protected-quest"))
        .andExpect(status().isForbidden());
  }
}
