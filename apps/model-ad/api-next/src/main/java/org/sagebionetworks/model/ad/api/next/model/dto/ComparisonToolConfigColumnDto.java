package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * ComparisonToolConfigColumnDto
 */

@JsonTypeName("ComparisonToolConfigColumn")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ComparisonToolConfigColumnDto {

  private @Nullable String name;

  /**
   * The data type of the column. Must be 'text', 'heat_map', 'link_internal', 'link_external', or 'primary', where 'primary' is the primary key column that uniquely identifies each record in the table.
   */
  public enum TypeEnum {
    TEXT("text"),
    
    HEAT_MAP("heat_map"),
    
    LINK_INTERNAL("link_internal"),
    
    LINK_EXTERNAL("link_external"),
    
    PRIMARY("primary");

    private final String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String value) {
      for (TypeEnum b : TypeEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private TypeEnum type;

  private String dataKey;

  private @Nullable String tooltip;

  private @Nullable String sortTooltip;

  private @Nullable String linkText;

  private @Nullable String linkUrl;

  private Boolean isExported;

  private Boolean isHidden;

  public ComparisonToolConfigColumnDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ComparisonToolConfigColumnDto(TypeEnum type, String dataKey, Boolean isExported, Boolean isHidden) {
    this.type = type;
    this.dataKey = dataKey;
    this.isExported = isExported;
    this.isHidden = isHidden;
  }

  public ComparisonToolConfigColumnDto name(@Nullable String name) {
    this.name = name;
    return this;
  }

  /**
   * The display name of the column.
   * @return name
   */
  
  @Schema(name = "name", description = "The display name of the column.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("name")
  public @Nullable String getName() {
    return name;
  }

  public void setName(@Nullable String name) {
    this.name = name;
  }

  public ComparisonToolConfigColumnDto type(TypeEnum type) {
    this.type = type;
    return this;
  }

  /**
   * The data type of the column. Must be 'text', 'heat_map', 'link_internal', 'link_external', or 'primary', where 'primary' is the primary key column that uniquely identifies each record in the table.
   * @return type
   */
  @NotNull 
  @Schema(name = "type", description = "The data type of the column. Must be 'text', 'heat_map', 'link_internal', 'link_external', or 'primary', where 'primary' is the primary key column that uniquely identifies each record in the table.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("type")
  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public ComparisonToolConfigColumnDto dataKey(String dataKey) {
    this.dataKey = dataKey;
    return this;
  }

  /**
   * The key used to retrieve data for this column from the data source.
   * @return dataKey
   */
  @NotNull 
  @Schema(name = "data_key", description = "The key used to retrieve data for this column from the data source.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("data_key")
  public String getDataKey() {
    return dataKey;
  }

  public void setDataKey(String dataKey) {
    this.dataKey = dataKey;
  }

  public ComparisonToolConfigColumnDto tooltip(@Nullable String tooltip) {
    this.tooltip = tooltip;
    return this;
  }

  /**
   * Tooltip text for the column.
   * @return tooltip
   */
  
  @Schema(name = "tooltip", description = "Tooltip text for the column.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("tooltip")
  public @Nullable String getTooltip() {
    return tooltip;
  }

  public void setTooltip(@Nullable String tooltip) {
    this.tooltip = tooltip;
  }

  public ComparisonToolConfigColumnDto sortTooltip(@Nullable String sortTooltip) {
    this.sortTooltip = sortTooltip;
    return this;
  }

  /**
   * Tooltip text for the column's sort functionality.
   * @return sortTooltip
   */
  
  @Schema(name = "sort_tooltip", description = "Tooltip text for the column's sort functionality.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sort_tooltip")
  public @Nullable String getSortTooltip() {
    return sortTooltip;
  }

  public void setSortTooltip(@Nullable String sortTooltip) {
    this.sortTooltip = sortTooltip;
  }

  public ComparisonToolConfigColumnDto linkText(@Nullable String linkText) {
    this.linkText = linkText;
    return this;
  }

  /**
   * The default text for a link in this column.
   * @return linkText
   */
  
  @Schema(name = "link_text", description = "The default text for a link in this column.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("link_text")
  public @Nullable String getLinkText() {
    return linkText;
  }

  public void setLinkText(@Nullable String linkText) {
    this.linkText = linkText;
  }

  public ComparisonToolConfigColumnDto linkUrl(@Nullable String linkUrl) {
    this.linkUrl = linkUrl;
    return this;
  }

  /**
   * The default URL for this column.
   * @return linkUrl
   */
  
  @Schema(name = "link_url", description = "The default URL for this column.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("link_url")
  public @Nullable String getLinkUrl() {
    return linkUrl;
  }

  public void setLinkUrl(@Nullable String linkUrl) {
    this.linkUrl = linkUrl;
  }

  public ComparisonToolConfigColumnDto isExported(Boolean isExported) {
    this.isExported = isExported;
    return this;
  }

  /**
   * Indicates whether the column is included in data exports.
   * @return isExported
   */
  @NotNull 
  @Schema(name = "is_exported", description = "Indicates whether the column is included in data exports.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("is_exported")
  public Boolean getIsExported() {
    return isExported;
  }

  public void setIsExported(Boolean isExported) {
    this.isExported = isExported;
  }

  public ComparisonToolConfigColumnDto isHidden(Boolean isHidden) {
    this.isHidden = isHidden;
    return this;
  }

  /**
   * Indicates whether the column is shown in the comparison table UI.
   * @return isHidden
   */
  @NotNull 
  @Schema(name = "is_hidden", description = "Indicates whether the column is shown in the comparison table UI.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("is_hidden")
  public Boolean getIsHidden() {
    return isHidden;
  }

  public void setIsHidden(Boolean isHidden) {
    this.isHidden = isHidden;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ComparisonToolConfigColumnDto comparisonToolConfigColumn = (ComparisonToolConfigColumnDto) o;
    return Objects.equals(this.name, comparisonToolConfigColumn.name) &&
        Objects.equals(this.type, comparisonToolConfigColumn.type) &&
        Objects.equals(this.dataKey, comparisonToolConfigColumn.dataKey) &&
        Objects.equals(this.tooltip, comparisonToolConfigColumn.tooltip) &&
        Objects.equals(this.sortTooltip, comparisonToolConfigColumn.sortTooltip) &&
        Objects.equals(this.linkText, comparisonToolConfigColumn.linkText) &&
        Objects.equals(this.linkUrl, comparisonToolConfigColumn.linkUrl) &&
        Objects.equals(this.isExported, comparisonToolConfigColumn.isExported) &&
        Objects.equals(this.isHidden, comparisonToolConfigColumn.isHidden);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, type, dataKey, tooltip, sortTooltip, linkText, linkUrl, isExported, isHidden);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ComparisonToolConfigColumnDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    dataKey: ").append(toIndentedString(dataKey)).append("\n");
    sb.append("    tooltip: ").append(toIndentedString(tooltip)).append("\n");
    sb.append("    sortTooltip: ").append(toIndentedString(sortTooltip)).append("\n");
    sb.append("    linkText: ").append(toIndentedString(linkText)).append("\n");
    sb.append("    linkUrl: ").append(toIndentedString(linkUrl)).append("\n");
    sb.append("    isExported: ").append(toIndentedString(isExported)).append("\n");
    sb.append("    isHidden: ").append(toIndentedString(isHidden)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
  
  public static class Builder {

    private ComparisonToolConfigColumnDto instance;

    public Builder() {
      this(new ComparisonToolConfigColumnDto());
    }

    protected Builder(ComparisonToolConfigColumnDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ComparisonToolConfigColumnDto value) { 
      this.instance.setName(value.name);
      this.instance.setType(value.type);
      this.instance.setDataKey(value.dataKey);
      this.instance.setTooltip(value.tooltip);
      this.instance.setSortTooltip(value.sortTooltip);
      this.instance.setLinkText(value.linkText);
      this.instance.setLinkUrl(value.linkUrl);
      this.instance.setIsExported(value.isExported);
      this.instance.setIsHidden(value.isHidden);
      return this;
    }

    public ComparisonToolConfigColumnDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ComparisonToolConfigColumnDto.Builder type(TypeEnum type) {
      this.instance.type(type);
      return this;
    }
    
    public ComparisonToolConfigColumnDto.Builder dataKey(String dataKey) {
      this.instance.dataKey(dataKey);
      return this;
    }
    
    public ComparisonToolConfigColumnDto.Builder tooltip(String tooltip) {
      this.instance.tooltip(tooltip);
      return this;
    }
    
    public ComparisonToolConfigColumnDto.Builder sortTooltip(String sortTooltip) {
      this.instance.sortTooltip(sortTooltip);
      return this;
    }
    
    public ComparisonToolConfigColumnDto.Builder linkText(String linkText) {
      this.instance.linkText(linkText);
      return this;
    }
    
    public ComparisonToolConfigColumnDto.Builder linkUrl(String linkUrl) {
      this.instance.linkUrl(linkUrl);
      return this;
    }
    
    public ComparisonToolConfigColumnDto.Builder isExported(Boolean isExported) {
      this.instance.isExported(isExported);
      return this;
    }
    
    public ComparisonToolConfigColumnDto.Builder isHidden(Boolean isHidden) {
      this.instance.isHidden(isHidden);
      return this;
    }
    
    /**
    * returns a built ComparisonToolConfigColumnDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ComparisonToolConfigColumnDto build() {
      try {
        return this.instance;
      } finally {
        // ensure that this.instance is not reused
        this.instance = null;
      }
    }

    @Override
    public String toString() {
      return getClass() + "=(" + instance + ")";
    }
  }

  /**
  * Create a builder with no initialized field (except for the default values).
  */
  public static ComparisonToolConfigColumnDto.Builder builder() {
    return new ComparisonToolConfigColumnDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ComparisonToolConfigColumnDto.Builder toBuilder() {
    ComparisonToolConfigColumnDto.Builder builder = new ComparisonToolConfigColumnDto.Builder();
    return builder.copyOf(this);
  }

}

