package org.sagebionetworks.bixarena.api.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptPageDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.entity.ExamplePromptEntity;
import org.sagebionetworks.bixarena.api.model.mapper.ExamplePromptMapper;
import org.sagebionetworks.bixarena.api.model.repository.ExamplePromptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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

  private static final Logger logger = LoggerFactory.getLogger(ExamplePromptService.class);

  private final ExamplePromptRepository examplePromptRepository;
  private final ExamplePromptMapper examplePromptMapper = new ExamplePromptMapper();

  @Transactional(readOnly = true)
  public ExamplePromptPageDto listExamplePrompts(ExamplePromptSearchQueryDto query) {
    logger.info("List example prompts with query {}", query);

    ExamplePromptSearchQueryDto effectiveQuery = query != null
      ? query
      : new ExamplePromptSearchQueryDto();

    Pageable pageable = createPageable(effectiveQuery);
    Specification<ExamplePromptEntity> spec = buildSpecification(effectiveQuery);

    Page<ExamplePromptEntity> page = examplePromptRepository.findAll(spec, pageable);

    return new ExamplePromptPageDto(
      page.getNumber(),
      page.getSize(),
      page.getTotalElements(),
      page.getTotalPages(),
      page.hasNext(),
      page.hasPrevious(),
      examplePromptMapper.convertToDtoList(page.getContent())
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
