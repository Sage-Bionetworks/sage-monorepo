package org.sagebionetworks.qtl.api.model.mapper;

import java.util.List;
import org.sagebionetworks.qtl.api.model.document.ComparisonToolConfigDocument;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolConfigColumnDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolConfigDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolConfigFilterDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolNounDto;
import org.sagebionetworks.qtl.api.model.dto.ComparisonToolPageDto;
import org.sagebionetworks.qtl.api.util.EnumConverter;
import org.sagebionetworks.qtl.api.util.RequiredFieldValidator;
import org.springframework.stereotype.Component;

@Component
public class ComparisonToolConfigMapper {

  private static final String RECORD_TYPE = "comparison tool config record";

  private static final RequiredFieldValidator REQUIRED_FIELDS = new RequiredFieldValidator(
    RECORD_TYPE
  );

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

    List<ComparisonToolConfigColumnDto> columns = REQUIRED_FIELDS.require(
      document.getColumns(),
      "columns"
    )
      .stream()
      .map(column -> toComparisonToolConfigDto(REQUIRED_FIELDS.require(column, "column")))
      .toList();

    List<ComparisonToolConfigFilterDto> filters = REQUIRED_FIELDS.require(
      document.getFilters(),
      "filters"
    )
      .stream()
      .map(filter -> toComparisonToolConfigFilterDto(REQUIRED_FIELDS.require(filter, "filter")))
      .toList();

    ComparisonToolConfigDto dto = new ComparisonToolConfigDto(
      page,
      REQUIRED_FIELDS.require(document.getDropdowns(), "dropdowns"),
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
    ComparisonToolConfigColumnDto dto = new ComparisonToolConfigColumnDto(
      EnumConverter.toEnum(
        column.getType(),
        ComparisonToolConfigColumnDto.TypeEnum::fromValue,
        "column type",
        RECORD_TYPE
      ),
      REQUIRED_FIELDS.require(column.getDataKey(), "column data_key"),
      REQUIRED_FIELDS.require(column.getIsExported(), "column is_exported"),
      REQUIRED_FIELDS.require(column.getIsHidden(), "column is_hidden")
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
    ComparisonToolConfigFilterDto dto = new ComparisonToolConfigFilterDto(
      REQUIRED_FIELDS.require(filter.getName(), "filter name"),
      REQUIRED_FIELDS.require(filter.getDataKey(), "filter data_key"),
      REQUIRED_FIELDS.require(filter.getQueryParamKey(), "filter query_param_key"),
      EnumConverter.toEnum(
        filter.getType(),
        ComparisonToolConfigFilterDto.TypeEnum::fromValue,
        "filter type",
        RECORD_TYPE
      ),
      REQUIRED_FIELDS.require(filter.getValues(), "filter values")
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

    return new ComparisonToolNounDto(
      REQUIRED_FIELDS.require(noun.getSingular(), "noun singular"),
      REQUIRED_FIELDS.require(noun.getPlural(), "noun plural")
    );
  }
}
