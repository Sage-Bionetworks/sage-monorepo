package org.sagebionetworks.bixarena.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.exception.ExamplePromptCategorizationNotFoundException;
import org.sagebionetworks.bixarena.api.exception.ExamplePromptNotFoundException;
import org.sagebionetworks.bixarena.api.model.dto.BiomedicalCategoryDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptCreateRequestDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptPageDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSortDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptUpdateRequestDto;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptCategorizationCategoryEntity;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptEntity;
import org.sagebionetworks.bixarena.api.model.mapper.ExamplePromptMapper;
import org.sagebionetworks.bixarena.api.model.repository.ExamplePromptCategorizationCategoryRepository;
import org.sagebionetworks.bixarena.api.model.repository.ExamplePromptCategorizationRepository;
import org.sagebionetworks.bixarena.api.model.repository.ExamplePromptRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExamplePromptService {

  private final ExamplePromptRepository examplePromptRepository;
  private final ExamplePromptCategorizationRepository categorizationRepository;
  private final ExamplePromptCategorizationCategoryRepository categoryRepository;
  private final ExamplePromptCategorizationService categorizationService;
  private final ExamplePromptMapper examplePromptMapper = new ExamplePromptMapper();

  @Transactional(readOnly = true)
  public ExamplePromptPageDto listExamplePrompts(ExamplePromptSearchQueryDto query) {
    log.info("List example prompts with query {}", query);

    ExamplePromptSearchQueryDto effectiveQuery = query != null
      ? query
      : new ExamplePromptSearchQueryDto();

    Page<ExamplePromptEntity> page;

    // Handle random sort specially since JPA Sort doesn't support SQL functions
    if (effectiveQuery.getSort() == ExamplePromptSortDto.RANDOM) {
      int pageSize = Optional.ofNullable(effectiveQuery.getPageSize()).orElse(25);
      List<BiomedicalCategoryDto> categories = effectiveQuery.getCategories();
      List<ExamplePromptEntity> randomList;
      if (categories == null || categories.isEmpty()) {
        randomList = examplePromptRepository.findRandom(pageSize);
      } else {
        List<String> slugs = categories.stream().map(BiomedicalCategoryDto::getValue).toList();
        randomList = examplePromptRepository.findRandomByCategory(slugs, pageSize);
      }
      int size = Math.max(randomList.size(), 1);
      page = new PageImpl<>(randomList, PageRequest.of(0, size), randomList.size());
    } else {
      Pageable pageable = createPageable(effectiveQuery);
      Specification<ExamplePromptEntity> spec = buildSpecification(effectiveQuery);
      page = examplePromptRepository.findAll(spec, pageable);
    }

    return ExamplePromptPageDto.builder()
      .number(page.getNumber())
      .size(page.getSize())
      .totalElements(page.getTotalElements())
      .totalPages(page.getTotalPages())
      .hasNext(page.hasNext())
      .hasPrevious(page.hasPrevious())
      .examplePrompts(toDtosWithCategories(page.getContent()))
      .build();
  }

  @Transactional
  public ExamplePromptDto createExamplePrompt(
    ExamplePromptCreateRequestDto request,
    UUID createdBy
  ) {
    log.info("Creating example prompt");

    // Source defaults to bixarena when omitted.
    String source = request.getSource() != null
      ? request.getSource().getValue()
      : "bixarena";

    ExamplePromptEntity entity = ExamplePromptEntity.builder()
      .question(request.getQuestion())
      .source(source)
      .createdBy(createdBy)
      .build();

    ExamplePromptEntity saved = examplePromptRepository.save(entity);
    examplePromptRepository.flush();

    UUID promptId = saved.getId();
    // Deferred so the async thread sees the committed prompt row.
    afterCommit(() -> categorizationService.categorizePromptAsync(promptId));

    log.info("Created example prompt with ID: {}", promptId);
    return toDtoWithCategories(saved);
  }

  @Transactional(readOnly = true)
  public ExamplePromptDto getExamplePrompt(UUID promptId) {
    log.info("Get example prompt with ID: {}", promptId);
    return toDtoWithCategories(getPromptOrThrow(promptId));
  }

  @Transactional
  public ExamplePromptDto updateExamplePrompt(
    UUID promptId,
    ExamplePromptUpdateRequestDto request
  ) {
    log.info("Updating example prompt with ID: {}", promptId);

    ExamplePromptEntity entity = getPromptOrThrow(promptId);

    boolean questionChanged = false;
    if (request.getQuestion() != null && !request.getQuestion().equals(entity.getQuestion())) {
      entity.setQuestion(request.getQuestion());
      questionChanged = true;
    }
    if (request.getSource() != null) {
      entity.setSource(request.getSource().getValue());
    }
    if (request.getActive() != null) {
      entity.setActive(request.getActive());
    }

    ExamplePromptEntity saved = examplePromptRepository.save(entity);
    examplePromptRepository.flush();

    if (questionChanged) {
      // Deferred so the async thread sees the committed question update.
      afterCommit(() -> categorizationService.categorizePromptAsync(promptId));
    }

    log.info("Updated example prompt with ID: {}", promptId);
    return toDtoWithCategories(saved);
  }

  /**
   * Sets or clears the effective categorization for a prompt by pointing at a row from history.
   * Pass {@code null} to clear.
   *
   * @throws ExamplePromptNotFoundException if the prompt is not found
   * @throws ExamplePromptCategorizationNotFoundException if categorizationId is non-null and
   *     does not belong to this prompt
   */
  @Transactional
  public ExamplePromptDto setEffectiveCategorization(UUID promptId, UUID categorizationId) {
    log.info("Setting effective categorization for prompt {}: {}", promptId, categorizationId);

    ExamplePromptEntity prompt = getPromptOrThrow(promptId);

    if (categorizationId != null) {
      categorizationRepository
        .findById(categorizationId)
        .filter(c -> c.getPromptId().equals(promptId))
        .orElseThrow(() ->
          new ExamplePromptCategorizationNotFoundException(
            String.format(
              "Categorization %s not found for prompt %s",
              categorizationId,
              promptId
            )
          )
        );
    }

    prompt.setEffectiveCategorizationId(categorizationId);
    ExamplePromptEntity saved = examplePromptRepository.save(prompt);
    return toDtoWithCategories(saved);
  }

  @Transactional
  public void deleteExamplePrompt(UUID promptId) {
    log.info("Deleting example prompt with ID: {}", promptId);
    getPromptOrThrow(promptId);
    examplePromptRepository.deleteById(promptId);
    log.info("Deleted example prompt with ID: {}", promptId);
  }

  /**
   * Batch-loads categories for all effective categorizations in the list to avoid N+1.
   * Total queries: 1 (list) + 1 (categories IN), regardless of page size.
   */
  private List<ExamplePromptDto> toDtosWithCategories(List<ExamplePromptEntity> entities) {
    if (entities.isEmpty()) {
      return List.of();
    }

    List<UUID> categorizationIds = entities
      .stream()
      .map(ExamplePromptEntity::getEffectiveCategorizationId)
      .filter(Objects::nonNull)
      .toList();

    Map<UUID, List<BiomedicalCategoryDto>> categoriesByCatId = categorizationIds.isEmpty()
      ? Map.of()
      : categoryRepository
          .findByCategorizationIdIn(categorizationIds)
          .stream()
          .collect(
            Collectors.groupingBy(
              ExamplePromptCategorizationCategoryEntity::getCategorizationId,
              Collectors.mapping(
                c -> BiomedicalCategoryDto.fromValue(c.getCategory()),
                Collectors.toList()
              )
            )
          );

    return entities
      .stream()
      .map(e -> {
        ExamplePromptDto dto = examplePromptMapper.convertToDto(e);
        UUID effId = e.getEffectiveCategorizationId();
        dto.setCategories(
          effId == null
            ? Collections.emptyList()
            : new ArrayList<>(categoriesByCatId.getOrDefault(effId, List.of()))
        );
        return dto;
      })
      .toList();
  }

  private ExamplePromptDto toDtoWithCategories(ExamplePromptEntity entity) {
    return toDtosWithCategories(List.of(entity)).get(0);
  }

  private static void afterCommit(Runnable action) {
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        action.run();
      }
    });
  }

  private ExamplePromptEntity getPromptOrThrow(UUID promptId) {
    return examplePromptRepository
      .findById(promptId)
      .orElseThrow(() ->
        new ExamplePromptNotFoundException("Example prompt not found: " + promptId)
      );
  }

  private Pageable createPageable(ExamplePromptSearchQueryDto query) {
    Sort sort = createSort(query);
    return PageRequest.of(
      Optional.ofNullable(query.getPageNumber()).orElse(0),
      Optional.ofNullable(query.getPageSize()).orElse(25),
      sort
    );
  }

  private Sort createSort(ExamplePromptSearchQueryDto query) {
    String sortField = Optional.ofNullable(query.getSort()).map(Enum::name).orElse("CREATED_AT");
    String directionStr = Optional.ofNullable(query.getDirection()).map(Enum::name).orElse("ASC");

    Sort.Direction direction = "DESC".equalsIgnoreCase(directionStr)
      ? Sort.Direction.DESC
      : Sort.Direction.ASC;

    String entityField =
      switch (sortField.toLowerCase()) {
        case "question" -> "question";
        case "created_at" -> "createdAt";
        case "source" -> "source";
        case "active" -> "active";
        default -> "createdAt"; // CREATED_AT
      };

    return Sort.by(direction, entityField);
  }

  private Specification<ExamplePromptEntity> buildSpecification(ExamplePromptSearchQueryDto query) {
    return Specification.where(activeFilter(query))
      .and(searchFilter(query))
      .and(sourceFilter(query))
      .and(categoriesFilter(query));
  }

  private Specification<ExamplePromptEntity> activeFilter(ExamplePromptSearchQueryDto query) {
    if (query.getActive() == null) {
      return null;
    }
    boolean active = query.getActive();
    return (root, cq, cb) -> cb.equal(root.get("active"), active);
  }

  private Specification<ExamplePromptEntity> searchFilter(ExamplePromptSearchQueryDto query) {
    if (query.getSearch() == null || query.getSearch().trim().isEmpty()) {
      return null;
    }
    String pattern = "%" + query.getSearch().trim().toLowerCase() + "%";
    return (root, cq, cb) -> cb.like(cb.lower(root.get("question")), pattern);
  }

  private Specification<ExamplePromptEntity> sourceFilter(ExamplePromptSearchQueryDto query) {
    if (query.getSource() == null) {
      return null;
    }
    String sourceValue = query.getSource().getValue();
    return (root, cq, cb) -> cb.equal(root.get("source"), sourceValue);
  }

  private Specification<ExamplePromptEntity> categoriesFilter(ExamplePromptSearchQueryDto query) {
    List<BiomedicalCategoryDto> categories = query.getCategories();
    if (categories == null || categories.isEmpty()) {
      return null;
    }
    List<String> slugs = categories.stream().map(BiomedicalCategoryDto::getValue).toList();
    return (root, cq, cb) -> {
      var subquery = cq.subquery(UUID.class);
      var epcc = subquery.from(ExamplePromptCategorizationCategoryEntity.class);
      subquery.select(epcc.get("categorizationId")).where(epcc.get("category").in(slugs));
      return root.get("effectiveCategorizationId").in(subquery);
    };
  }
}
