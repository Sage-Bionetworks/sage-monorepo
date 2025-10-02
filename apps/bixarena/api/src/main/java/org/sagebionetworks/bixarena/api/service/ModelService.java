package org.sagebionetworks.bixarena.api.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.ModelPageDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.entity.ModelEntity;
import org.sagebionetworks.bixarena.api.model.mapper.ModelMapper;
import org.sagebionetworks.bixarena.api.model.repository.ModelRepository;
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
public class ModelService {

  private final ModelRepository modelRepository;
  private final ModelMapper modelMapper = new ModelMapper();

  @Transactional(readOnly = true)
  public ModelPageDto listModels(ModelSearchQueryDto query) {
    ModelSearchQueryDto effectiveQuery = query != null ? query : new ModelSearchQueryDto();

    Pageable pageable = createPageable(effectiveQuery);
    Specification<ModelEntity> spec = buildSpecification(effectiveQuery);

    Page<ModelEntity> page = modelRepository.findAll(spec, pageable);

    return new ModelPageDto(
      page.getNumber(),
      page.getSize(),
      page.getTotalElements(),
      page.getTotalPages(),
      page.hasNext(),
      page.hasPrevious(),
      modelMapper.convertToDtoList(page.getContent())
    );
  }

  private Pageable createPageable(ModelSearchQueryDto query) {
    Sort sort = createSort(query);
    return PageRequest.of(
      Optional.ofNullable(query.getPageNumber()).orElse(0),
      Optional.ofNullable(query.getPageSize()).orElse(25),
      sort
    );
  }

  private Sort createSort(ModelSearchQueryDto query) {
    String sortField = Optional.ofNullable(query.getSort()).map(Enum::name).orElse("NAME");
    String directionStr = Optional.ofNullable(query.getDirection()).map(Enum::name).orElse("ASC");

    Sort.Direction direction = "DESC".equalsIgnoreCase(directionStr)
      ? Sort.Direction.DESC
      : Sort.Direction.ASC;

    String entityField =
      switch (sortField.toLowerCase()) {
        case "created_at" -> "createdAt";
        case "updated_at" -> "updatedAt";
        case "slug" -> "slug";
        case "license" -> "license";
        case "active" -> "active";
        default -> "name"; // NAME
      };

    return Sort.by(direction, entityField);
  }

  private Specification<ModelEntity> buildSpecification(ModelSearchQueryDto query) {
    return Specification.where(activeFilter(query))
        .and(searchFilter(query))
        .and(licenseFilter(query))
        .and(organizationFilter(query));
  }

  private Specification<ModelEntity> activeFilter(ModelSearchQueryDto query) {
    if (query.getActive() == null) {
      return null; // no filtering
    }
    boolean active = query.getActive();
    return (root, cq, cb) -> cb.equal(root.get("active"), active);
  }

  private Specification<ModelEntity> searchFilter(ModelSearchQueryDto query) {
    if (query.getSearch() == null || query.getSearch().trim().isEmpty()) {
      return null;
    }
    String pattern = "%" + query.getSearch().trim().toLowerCase() + "%";
    return (root, cq, cb) ->
      cb.or(
        cb.like(cb.lower(root.get("name")), pattern),
        cb.like(cb.lower(root.get("slug")), pattern)
      );
  }

  private Specification<ModelEntity> licenseFilter(ModelSearchQueryDto query) {
    if (query.getLicense() == null) {
      return null; // no filtering
    }
    String licenseValue = query.getLicense().getValue();
    return (root, cq, cb) -> cb.equal(root.get("license"), licenseValue);
  }

  private Specification<ModelEntity> organizationFilter(ModelSearchQueryDto query) {
    if (query.getOrganization() == null || query.getOrganization().trim().isEmpty()) {
      return null; // no filtering
    }
    String pattern = "%" + query.getOrganization().trim().toLowerCase() + "%";
    return (root, cq, cb) -> cb.like(cb.lower(root.get("organization")), pattern);
  }
}
