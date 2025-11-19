package org.sagebionetworks.model.ad.api.next.api;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.exception.ErrorConstants;
import org.sagebionetworks.model.ad.api.next.exception.InvalidCategoryException;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationsPageDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.service.DiseaseCorrelationService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class DiseaseCorrelationApiDelegateImpl implements DiseaseCorrelationApiDelegate {

  private final DiseaseCorrelationService diseaseCorrelationService;

  @Override
  public ResponseEntity<DiseaseCorrelationsPageDto> getDiseaseCorrelations(
    List<String> category,
    @Nullable List<String> item,
    ItemFilterTypeQueryDto itemFilterType,
    Integer pageNumber,
    Integer pageSize
  ) {
    String cluster = extractCluster(category);
    List<String> items = ApiHelper.sanitizeItems(item);
    ItemFilterTypeQueryDto effectiveFilter = Objects.requireNonNullElse(
      itemFilterType,
      ItemFilterTypeQueryDto.INCLUDE
    );

    DiseaseCorrelationsPageDto results = diseaseCorrelationService.loadDiseaseCorrelations(
      pageNumber,
      pageSize,
      cluster,
      items,
      effectiveFilter
    );

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(results);
  }

  private String extractCluster(List<String> category) {
    if (category == null || category.size() != 2) {
      throw new InvalidCategoryException(ErrorConstants.CATEGORY_REQUIREMENT_MESSAGE);
    }

    String topLevelCategory = category.get(0);
    String subCategory = category.get(1);

    if (!ErrorConstants.SUPPORTED_CATEGORY.equals(topLevelCategory)) {
      throw new InvalidCategoryException(topLevelCategory, ErrorConstants.SUPPORTED_CATEGORY);
    }

    if (!StringUtils.hasText(subCategory)) {
      throw new InvalidCategoryException(ErrorConstants.CATEGORY_REQUIREMENT_MESSAGE);
    }

    return subCategory;
  }
}
