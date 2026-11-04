package org.sagebionetworks.qtl.api.model.mapper;

import java.util.List;
import org.sagebionetworks.qtl.api.model.document.ComparisonToolConfigDocument;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolConfigColumnDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolConfigFilterDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolNounDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolPageDto;
import org.sagebionetworks.qtl.api.util.EnumConverter;
import org.springframework.stereotype.Component;

@Component
public class ComparisonToolConfigMapper {

  private static final String RECORD_TYPE = "comparison tool config record";

  public ComparisonToolConfigDto toDto(ComparisonToolConfigDocument document) {
    if (document == null) {
      return null;
    }

    ComparisonToolPageDto page = EnumConverter.toEnum(
      document.getPage(),
      ComparisonToolPageDto::fromValue,
      "page",
      RECORD_TYPE
    );

    List<ComparisonToolConfigColumnDto> columns = document.getColumns() == null
      ? List.of()
      : document.getColumns().stream().map(this::toComparisonToolConfigDto).toList();

    List<ComparisonToolConfigFilterDto> filters = document.getFilters() == null
      ? List.of()
      : document.getFilters().stream().map(this::toComparisonToolConfigFilterDto).toList();

    ComparisonToolConfigDto dto = new ComparisonToolConfigDto(
      page,
      document.getDropdowns(),
      document.getRowCount(),
      columns,
      filters
    );

    dto.setRowIdDataKey(document.getRowIdDataKey());
    dto.setParentIdDataKey(document.getParentIdDataKey());
    dto.setParentNoun(toComparisonToolNounDto(document.getParentNoun()));
    dto.setViewNoun(toComparisonToolNounDto(document.getViewNoun()));

    return dto;
  }

  private ComparisonToolConfigColumnDto toComparisonToolConfigDto(
    ComparisonToolConfigDocument.ComparisonToolConfigColumn column
  ) {
    if (column == null) {
      return null;
    }

    ComparisonToolConfigColumnDto dto = new ComparisonToolConfigColumnDto(
      EnumConverter.toEnum(
        column.getType(),
        ComparisonToolConfigColumnDto.TypeEnum::fromValue,
        "column type",
        RECORD_TYPE
      ),
      column.getDataKey(),
      column.getIsExported(),
      column.getIsHidden()
    );

    dto.setName(column.getName());
    dto.setTooltip(column.getTooltip());
    dto.setSortTooltip(column.getSortTooltip());
    dto.setLinkText(column.getLinkText());
    dto.setLinkUrl(column.getLinkUrl());
    dto.setColumnWidth(column.getColumnWidth());

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

  private ComparisonToolNounDto toComparisonToolNounDto(
    ComparisonToolConfigDocument.ComparisonToolNoun noun
  ) {
    if (noun == null) {
      return null;
    }

    return new ComparisonToolNounDto(noun.getSingular(), noun.getPlural());
  }
}
