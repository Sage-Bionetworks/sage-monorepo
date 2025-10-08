package org.sagebionetworks.bixarena.api.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptPageDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSortDto;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptEntity;
import org.sagebionetworks.bixarena.api.model.mapper.ExamplePromptMapper;
import org.sagebionetworks.bixarena.api.model.repository.ExamplePromptRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExamplePromptService {

  private final ExamplePromptRepository examplePromptRepository;
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
      var randomList = examplePromptRepository.findRandom(pageSize);
      page = new PageImpl<>(randomList, PageRequest.of(0, randomList.size()), randomList.size());
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
      .examplePrompts(examplePromptMapper.convertToDtoList(page.getContent()))
      .build();
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
      .and(sourceFilter(query));
  }

  private Specification<ExamplePromptEntity> activeFilter(ExamplePromptSearchQueryDto query) {
    if (query.getActive() == null) {
      return null; // no filtering
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
      return null; // no filtering
    }
    String sourceValue = query.getSource().getValue();
    return (root, cq, cb) -> cb.equal(root.get("source"), sourceValue);
  }
}
