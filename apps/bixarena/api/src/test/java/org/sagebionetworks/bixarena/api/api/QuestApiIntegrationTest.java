package org.sagebionetworks.bixarena.api.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.bixarena.api.model.dto.QuestCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostCreateOrUpdateDto;
import org.sagebionetworks.bixarena.api.model.dto.QuestPostReorderDto;
import org.sagebionetworks.bixarena.api.model.entity.QuestEntity;
import org.sagebionetworks.bixarena.api.model.entity.QuestPostEntity;
import org.sagebionetworks.bixarena.api.model.projection.ContributorProjection;
import org.sagebionetworks.bixarena.api.model.repository.BattleRepository;
import org.sagebionetworks.bixarena.api.model.repository.QuestPostRepository;
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

  @Autowired
  private QuestPostRepository questPostRepository;

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

  private QuestPostEntity seedPost(QuestEntity quest, int postIndex, String title) {
    QuestPostEntity post = QuestPostEntity.builder()
        .questId(quest.getId())
        .postIndex(postIndex)
        .date(LocalDate.now())
        .title(title)
        .description("Description for " + title)
        .images("[]")
        .build();
    return questPostRepository.save(post);
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

  private QuestPostCreateOrUpdateDto buildPostDto(String title) {
    return QuestPostCreateOrUpdateDto.builder()
        .title(title)
        .description("Post description")
        .date(LocalDate.now())
        .images(List.of(URI.create("https://example.com/img.png")))
        .build();
  }

  // --- Milestone 5: Security tests ---

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
  @DisplayName("should return 401 when creating quest anonymously")
  void shouldReturn401WhenCreateQuestAnonymously() throws Exception {
    // given
    QuestCreateOrUpdateDto dto = buildCreateDto("new-quest");

    // when/then — only GET is permitAll, so anonymous POST hits .authenticated() → 401
    mockMvc.perform(post("/v1/quests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isUnauthorized());
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
  @DisplayName("should return 401 when deleting quest anonymously")
  void shouldReturn401WhenDeleteQuestAnonymously() throws Exception {
    // given
    seedQuest("protected-quest");

    // when/then — only GET is permitAll, so anonymous DELETE hits .authenticated() → 401
    mockMvc.perform(delete("/v1/quests/protected-quest"))
        .andExpect(status().isUnauthorized());
  }

  // --- Milestone 6: CRUD flow tests ---

  @Test
  @DisplayName("should return quest when created and fetched")
  @WithMockUser(roles = "ADMIN")
  void shouldReturnQuestWhenCreatedAndFetched() throws Exception {
    // given
    QuestCreateOrUpdateDto dto = buildCreateDto("crud-quest");

    // when — create
    mockMvc.perform(post("/v1/quests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated());

    // then — fetch and verify
    mockMvc.perform(get("/v1/quests/crud-quest"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.questId").value("crud-quest"))
        .andExpect(jsonPath("$.title").value("New Quest"))
        .andExpect(jsonPath("$.goal").value(200));
  }

  @Test
  @DisplayName("should return 409 when creating duplicate quest")
  @WithMockUser(roles = "ADMIN")
  void shouldReturn409WhenCreatingDuplicateQuest() throws Exception {
    // given
    seedQuest("duplicate-quest");
    QuestCreateOrUpdateDto dto = buildCreateDto("duplicate-quest");

    // when/then
    mockMvc.perform(post("/v1/quests")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isConflict());
  }

  @Test
  @DisplayName("should return 404 when quest not found")
  void shouldReturn404WhenQuestNotFound() throws Exception {
    // when/then
    mockMvc.perform(get("/v1/quests/nonexistent"))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("should return post in quest when post created")
  @WithMockUser(roles = "ADMIN")
  void shouldReturnPostInQuestWhenPostCreated() throws Exception {
    // given
    seedQuest("post-quest");
    QuestPostCreateOrUpdateDto postDto = buildPostDto("First Post");

    // when — create post
    mockMvc.perform(post("/v1/quests/post-quest/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value("First Post"));

    // then — fetch quest and verify post is included
    mockMvc.perform(get("/v1/quests/post-quest"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.posts.length()").value(1))
        .andExpect(jsonPath("$.posts[0].title").value("First Post"));
  }

  @Test
  @DisplayName("should update post content when put post")
  @WithMockUser(roles = "ADMIN")
  void shouldUpdatePostContentWhenPutPost() throws Exception {
    // given
    QuestEntity quest = seedQuest("update-quest");
    seedPost(quest, 0, "Original Title");

    QuestPostCreateOrUpdateDto updateDto = buildPostDto("Updated Title");

    // when
    mockMvc.perform(put("/v1/quests/update-quest/posts/0")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Title"));

    // then — verify via GET
    mockMvc.perform(get("/v1/quests/update-quest"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.posts[0].title").value("Updated Title"));
  }

  @Test
  @DisplayName("should remove post when delete post")
  @WithMockUser(roles = "ADMIN")
  void shouldRemovePostWhenDeletePost() throws Exception {
    // given
    QuestEntity quest = seedQuest("remove-quest");
    seedPost(quest, 0, "Post to keep");
    seedPost(quest, 1, "Post to delete");

    // when
    mockMvc.perform(delete("/v1/quests/remove-quest/posts/1"))
        .andExpect(status().isNoContent());

    // then
    mockMvc.perform(get("/v1/quests/remove-quest"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.posts.length()").value(1))
        .andExpect(jsonPath("$.posts[0].title").value("Post to keep"));
  }

  @Test
  @DisplayName("should change order when reorder posts")
  @WithMockUser(roles = "ADMIN")
  void shouldChangeOrderWhenReorderPosts() throws Exception {
    // given
    QuestEntity quest = seedQuest("reorder-quest");
    seedPost(quest, 0, "First");
    seedPost(quest, 1, "Second");
    seedPost(quest, 2, "Third");

    QuestPostReorderDto reorderDto = new QuestPostReorderDto(List.of(2, 0, 1));

    // when
    mockMvc.perform(put("/v1/quests/reorder-quest/posts/reorder")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reorderDto)))
        .andExpect(status().isOk());

    // then — new order: Third, First, Second
    mockMvc.perform(get("/v1/quests/reorder-quest"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.posts[0].title").value("Third"))
        .andExpect(jsonPath("$.posts[1].title").value("First"))
        .andExpect(jsonPath("$.posts[2].title").value("Second"));
  }

  @Test
  @DisplayName("should redact content when post is locked")
  void shouldRedactContentWhenPostIsLocked() throws Exception {
    // given — post requires 500 blocks, but totalBlocks is stubbed to 0
    QuestEntity quest = seedQuest("gated-quest");
    QuestPostEntity post = QuestPostEntity.builder()
        .questId(quest.getId())
        .postIndex(0)
        .date(LocalDate.now())
        .title("Locked Post")
        .description("Secret content")
        .images("[\"https://example.com/secret.png\"]")
        .requiredProgress(500)
        .build();
    questPostRepository.save(post);

    // when/then — anonymous user with 0 totalBlocks → locked
    mockMvc.perform(get("/v1/quests/gated-quest"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.posts[0].title").value("Locked Post"))
        .andExpect(jsonPath("$.posts[0].locked").value(true))
        .andExpect(jsonPath("$.posts[0].description").doesNotExist())
        .andExpect(jsonPath("$.posts[0].images").isEmpty())
        .andExpect(jsonPath("$.posts[0].requiredProgress").value(500));
  }

  // --- Validation & edge case integration tests ---

  @Test
  @DisplayName("should return 400 when list contributors with minBattles less than 1")
  void shouldReturn400WhenMinBattlesLessThanOne() throws Exception {
    // given
    seedQuest("validation-quest");

    // when/then
    mockMvc.perform(get("/v1/quests/validation-quest/contributors")
            .param("minBattles", "0"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should return 400 when list contributors with limit less than 1")
  void shouldReturn400WhenLimitLessThanOne() throws Exception {
    // given
    seedQuest("validation-quest");

    // when/then
    mockMvc.perform(get("/v1/quests/validation-quest/contributors")
            .param("limit", "0"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should return 400 when list contributors with limit exceeding 1000")
  void shouldReturn400WhenLimitExceeds1000() throws Exception {
    // given
    seedQuest("validation-quest");

    // when/then
    mockMvc.perform(get("/v1/quests/validation-quest/contributors")
            .param("limit", "1001"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should return 200 when updating quest as admin")
  @WithMockUser(roles = "ADMIN")
  void shouldReturn200WhenUpdatingQuestAsAdmin() throws Exception {
    // given
    seedQuest("updatable-quest");

    QuestCreateOrUpdateDto updateDto = QuestCreateOrUpdateDto.builder()
        .questId("updatable-quest")
        .title("Updated Title")
        .description("Updated description")
        .goal(500)
        .startDate(OffsetDateTime.now().minusDays(1))
        .endDate(OffsetDateTime.now().plusDays(60))
        .activePostIndex(0)
        .build();

    // when/then
    mockMvc.perform(put("/v1/quests/updatable-quest")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Title"))
        .andExpect(jsonPath("$.goal").value(500));
  }

  @Test
  @DisplayName("should return 401 when updating quest anonymously")
  void shouldReturn401WhenUpdatingQuestAnonymously() throws Exception {
    // given
    seedQuest("protected-update");

    QuestCreateOrUpdateDto updateDto = buildCreateDto("protected-update");

    // when/then
    mockMvc.perform(put("/v1/quests/protected-update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("should return 401 when creating post anonymously")
  void shouldReturn401WhenCreatingPostAnonymously() throws Exception {
    // given
    seedQuest("post-auth-quest");
    QuestPostCreateOrUpdateDto postDto = buildPostDto("Unauthorized Post");

    // when/then
    mockMvc.perform(post("/v1/quests/post-auth-quest/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postDto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("should return 404 when deleting post from nonexistent quest")
  @WithMockUser(roles = "ADMIN")
  void shouldReturn404WhenDeletingPostFromNonexistentQuest() throws Exception {
    // when/then
    mockMvc.perform(delete("/v1/quests/no-such-quest/posts/0"))
        .andExpect(status().isNotFound());
  }

  // --- Coverage: listQuestContributors happy path & extractUserId ---

  @Test
  @DisplayName("should return contributors when listing with valid params")
  void shouldReturnContributorsWhenListingWithValidParams() throws Exception {
    // given
    seedQuest("contrib-quest");

    ContributorProjection contributor = new ContributorProjection() {
      @Override public String getUsername() { return "test-user"; }
      @Override public Integer getBattleCount() { return 10; }
    };

    when(battleRepository.findContributorsByDateRange(any(), any(), eq(1), any()))
        .thenReturn(List.of(contributor));

    // when/then — exercises the full listQuestContributors path (lines 107-131)
    mockMvc.perform(get("/v1/quests/contrib-quest/contributors"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.questId").value("contrib-quest"))
        .andExpect(jsonPath("$.totalContributors").value(1))
        .andExpect(jsonPath("$.contributors[0].username").value("test-user"));
  }

  @Test
  @DisplayName("should return contributors when listing with explicit params")
  void shouldReturnContributorsWhenListingWithExplicitParams() throws Exception {
    // given
    seedQuest("param-quest");

    when(battleRepository.findContributorsByDateRange(any(), any(), eq(5), any()))
        .thenReturn(List.of());

    // when/then — exercises null-check branches for minBattles and limit
    mockMvc.perform(get("/v1/quests/param-quest/contributors")
            .param("minBattles", "5")
            .param("limit", "50"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalContributors").value(0));
  }

  @Test
  @DisplayName("should resolve user id when authenticated with jwt")
  void shouldResolveUserIdWhenAuthenticatedWithJwt() throws Exception {
    // given — seed a quest with a progress-gated post
    QuestEntity quest = seedQuest("jwt-quest");
    QuestPostEntity post = QuestPostEntity.builder()
        .questId(quest.getId())
        .postIndex(0)
        .date(LocalDate.now())
        .title("Public Post")
        .description("Visible")
        .images("[]")
        .build();
    questPostRepository.save(post);

    UUID userId = UUID.randomUUID();

    // Stub user battle count for tier resolution
    when(battleRepository.countCompletedByUserIdAndDateRange(eq(userId), any(), any()))
        .thenReturn(0L);

    // when/then — jwt with UUID sub exercises extractUserId line 140
    mockMvc.perform(get("/v1/quests/jwt-quest")
            .with(jwt().jwt(j -> j.subject(userId.toString())
                .claim("roles", List.of("ROLE_USER")))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.questId").value("jwt-quest"));
  }
}
