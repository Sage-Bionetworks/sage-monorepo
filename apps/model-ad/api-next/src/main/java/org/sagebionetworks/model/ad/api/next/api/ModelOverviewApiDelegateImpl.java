package org.sagebionetworks.model.ad.api.next.api;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewSearchQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.service.ModelOverviewService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class ModelOverviewApiDelegateImpl implements ModelOverviewApiDelegate {

  private static final Set<String> VALID_QUERY_PARAMS = Set.of(
    "pageNumber",
    "pageSize",
    "items",
    "itemFilterType",
    "search",
    "available_data",
    "availableData",
    "center",
    "model_type",
    "modelType",
    "modified_genes",
    "modifiedGenes",
    "sortFields",
    "sortOrders"
  );

  private final ModelOverviewService modelOverviewService;

  @Override
  public ResponseEntity<ModelOverviewsPageDto> getModelOverviews(
    ModelOverviewSearchQueryDto query
  ) {
    // Validate query parameters
    validateQueryParameters();

    ModelOverviewsPageDto page = modelOverviewService.loadModelOverviews(query);

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(page);
  }

  private void validateQueryParameters() {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
      .getRequestAttributes())
      .getRequest();
    Map<String, String[]> parameterMap = request.getParameterMap();

    for (String paramName : parameterMap.keySet()) {
      if (!VALID_QUERY_PARAMS.contains(paramName)) {
        throw new IllegalArgumentException("Unknown query parameter: " + paramName);
      }
    }
  }
}
