package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.document.ComparisonToolConfigDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolConfigColumnDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolConfigFilterDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ComparisonToolPageDto;
import org.springframework.stereotype.Component;

@Component
public class ComparisonToolConfigMapper {

  public ComparisonToolConfigDto toDto(ComparisonToolConfigDocument document) {
    if (document == null) {
      return null;
    }

    ComparisonToolPageDto page = ComparisonToolPageDto.fromValue(document.getPage());

    List<ComparisonToolConfigColumnDto> columns = document.getColumns() == null
      ? List.of()
      : document.getColumns().stream().map(this::toComparisonToolConfigDto).toList();

    List<ComparisonToolConfigFilterDto> filters = document.getFilters() == null
      ? List.of()
      : document.getFilters().stream().map(this::toComparisonToolConfigFilterDto).toList();

    return new ComparisonToolConfigDto(
      page,
      document.getDropdowns(),
      document.getRowCount(),
      columns,
      filters
    );
  }

  private ComparisonToolConfigColumnDto toComparisonToolConfigDto(
    ComparisonToolConfigDocument.ComparisonToolConfigColumn column
  ) {
    if (column == null) {
      return null;
    }

    ComparisonToolConfigColumnDto dto = new ComparisonToolConfigColumnDto(
      ComparisonToolConfigColumnDto.TypeEnum.fromValue(column.getType()),
      column.getDataKey(),
      column.getIsExported(),
      column.getIsHidden()
    );

    dto.setName(column.getName());
    dto.setTooltip(column.getTooltip());
    dto.setSortTooltip(column.getSortTooltip());
    dto.setLinkText(column.getLinkText());
    dto.setLinkUrl(column.getLinkUrl());

    return dto;
  }

  private ComparisonToolConfigFilterDto toComparisonToolConfigFilterDto(
    ComparisonToolConfigDocument.ComparisonToolConfigFilter filter
  ) {
    if (filter == null) {
      return null;
    }

    ComparisonToolConfigFilterDto dto = new ComparisonToolConfigFilterDto(
      filter.getName(),
      filter.getDataKey(),
      filter.getQueryParamKey(),
      filter.getValues()
    );
    dto.setShortName(filter.getShortName());
    return dto;
  }
}
