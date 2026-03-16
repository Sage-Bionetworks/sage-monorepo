package org.sagebionetworks.bixarena.api.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.bixarena.api.model.entity.QuestEntity;
import org.sagebionetworks.bixarena.api.model.entity.QuestPostEntity;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.QuestPostRepository;
import org.sagebionetworks.bixarena.api.model.repository.QuestRepository;
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
class AdminApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private QuestRepository questRepository;

  @Autowired
  private QuestPostRepository questPostRepository;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  @MockitoBean
  private BattleRepository battleRepository;

  @BeforeEach
  void setUp() {
    when(battleRepository.countCompletedByDateRange(any(), any())).thenReturn(0L);
  }

  private QuestEntity seedQuestWithLockedPost() {
    QuestEntity quest = QuestEntity.builder()
        .questId("admin-quest")
        .title("Admin Quest")
        .description("A quest for admin testing")
        .goal(100)
        .startDate(OffsetDateTime.now().minusDays(1))
        .endDate(OffsetDateTime.now().plusDays(30))
        .activePostIndex(0)
        .build();
    quest = questRepository.save(quest);

    // Public post
    questPostRepository.save(QuestPostEntity.builder()
        .questId(quest.getId())
        .postIndex(0)
        .date(LocalDate.now())
        .title("Public Post")
        .description("Visible to everyone")
        .images("[]")
        .build());

    // Progress-gated post (requires 500 blocks, totalBlocks is 0)
    questPostRepository.save(QuestPostEntity.builder()
        .questId(quest.getId())
        .postIndex(1)
        .date(LocalDate.now())
        .title("Locked Post")
        .description("Secret content")
        .images("[\"https://example.com/secret.png\"]")
        .requiredProgress(500)
        .build());

    return quest;
  }

  @Test
  @DisplayName("should return all posts ungated when admin")
  @WithMockUser(roles = "ADMIN")
  void shouldReturnAllPostsUngatedWhenAdmin() throws Exception {
    // given
    seedQuestWithLockedPost();

    // when/then — admin endpoint returns all posts with full content, no gating
    mockMvc.perform(get("/v1/admin/quests/admin-quest"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.posts.length()").value(2))
        .andExpect(jsonPath("$.posts[0].title").value("Public Post"))
        .andExpect(jsonPath("$.posts[0].locked").value(false))
        .andExpect(jsonPath("$.posts[1].title").value("Locked Post"))
        .andExpect(jsonPath("$.posts[1].locked").value(false))
        .andExpect(jsonPath("$.posts[1].description").value("Secret content"));
  }

  @Test
  @DisplayName("should return 401 when getting admin quest anonymously")
  void shouldReturn401WhenGetAdminQuestAnonymously() throws Exception {
    // given
    seedQuestWithLockedPost();

    // when/then — /v1/admin/** is not permitAll → 401
    mockMvc.perform(get("/v1/admin/quests/admin-quest"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("should return 403 when getting admin quest as non-admin user")
  @WithMockUser(roles = "USER")
  void shouldReturn403WhenGetAdminQuestAsUser() throws Exception {
    // given
    seedQuestWithLockedPost();

    // when/then — authenticated but not admin → 403
    mockMvc.perform(get("/v1/admin/quests/admin-quest"))
        .andExpect(status().isForbidden());
  }
}
