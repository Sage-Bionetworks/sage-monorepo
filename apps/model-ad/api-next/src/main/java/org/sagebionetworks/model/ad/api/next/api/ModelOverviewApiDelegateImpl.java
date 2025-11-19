package org.sagebionetworks.model.ad.api.next.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewsPageDto;
import org.sagebionetworks.model.ad.api.next.service.ModelOverviewService;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModelOverviewApiDelegateImpl implements ModelOverviewApiDelegate {

  private final ModelOverviewService modelOverviewService;

  @Override
  public ResponseEntity<ModelOverviewsPageDto> getModelOverviews(
    @Nullable List<String> item,
    ItemFilterTypeQueryDto itemFilterType,
    @Nullable Integer pageNumber,
    @Nullable Integer pageSize
  ) {
    ItemFilterTypeQueryDto effectiveFilter = itemFilterType != null
      ? itemFilterType
      : ItemFilterTypeQueryDto.INCLUDE;
    List<String> items = ApiHelper.sanitizeItems(item);

    ModelOverviewsPageDto page = modelOverviewService.loadModelOverviews(
      pageNumber,
      pageSize,
      items,
      effectiveFilter
    );

    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(page);
  }
}
