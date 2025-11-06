package org.sagebionetworks.model.ad.api.next.service;

import java.util.List;
import org.sagebionetworks.model.ad.api.next.api.ModelOverviewApiDelegate;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.util.ApiHelper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class ModelOverviewApiDelegateImpl implements ModelOverviewApiDelegate {

  private final ModelOverviewQueryService modelOverviewQueryService;

  public ModelOverviewApiDelegateImpl(ModelOverviewQueryService modelOverviewQueryService) {
    this.modelOverviewQueryService = modelOverviewQueryService;
  }

  @Override
  public ResponseEntity<List<ModelOverviewDto>> getModelOverviews(
    @Nullable List<String> item,
    ItemFilterTypeQueryDto itemFilterType
  ) {
    ItemFilterTypeQueryDto effectiveFilter = itemFilterType != null
      ? itemFilterType
      : ItemFilterTypeQueryDto.INCLUDE;
    List<String> items = ApiHelper.sanitizeItems(item);

    List<ModelOverviewDto> dtos = modelOverviewQueryService.loadModelOverviews(
      items,
      effectiveFilter
    );
    
    return ResponseEntity.ok()
      .headers(ApiHelper.createNoCacheHeaders(MediaType.APPLICATION_JSON))
      .body(dtos);
  }
}
