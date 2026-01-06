package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Comparison Tool Config Filter
 */

@Schema(name = "ComparisonToolConfigFilter", description = "Comparison Tool Config Filter")
@JsonTypeName("ComparisonToolConfigFilter")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ComparisonToolConfigFilterDto {

  private String name;

  private String dataKey;

  private @Nullable String shortName;

  private String queryParamKey;

  @Valid
  private List<String> values = new ArrayList<>();

  public ComparisonToolConfigFilterDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ComparisonToolConfigFilterDto(String name, String dataKey, String queryParamKey, List<String> values) {
    this.name = name;
    this.dataKey = dataKey;
    this.queryParamKey = queryParamKey;
    this.values = values;
  }

  public ComparisonToolConfigFilterDto name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Name of the filter group
   * @return name
   */
  @NotNull 
  @Schema(name = "name", example = "Biological Domain", description = "Name of the filter group", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ComparisonToolConfigFilterDto dataKey(String dataKey) {
    this.dataKey = dataKey;
    return this;
  }

  /**
   * Field to filter on
   * @return dataKey
   */
  @NotNull 
  @Schema(name = "data_key", example = "model_type", description = "Field to filter on", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("data_key")
  public String getDataKey() {
    return dataKey;
  }

  public void setDataKey(String dataKey) {
    this.dataKey = dataKey;
  }

  public ComparisonToolConfigFilterDto shortName(@Nullable String shortName) {
    this.shortName = shortName;
    return this;
  }

  /**
   * Short name of the filter group
   * @return shortName
   */
  
  @Schema(name = "short_name", example = "Biodomain", description = "Short name of the filter group", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("short_name")
  public @Nullable String getShortName() {
    return shortName;
  }

  public void setShortName(@Nullable String shortName) {
    this.shortName = shortName;
  }

  public ComparisonToolConfigFilterDto queryParamKey(String queryParamKey) {
    this.queryParamKey = queryParamKey;
    return this;
  }

  /**
   * Query parameter key for the filter
   * @return queryParamKey
   */
  @NotNull 
  @Schema(name = "query_param_key", example = "modelType", description = "Query parameter key for the filter", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("query_param_key")
  public String getQueryParamKey() {
    return queryParamKey;
  }

  public void setQueryParamKey(String queryParamKey) {
    this.queryParamKey = queryParamKey;
  }

  public ComparisonToolConfigFilterDto values(List<String> values) {
    this.values = values;
    return this;
  }

  public ComparisonToolConfigFilterDto addValuesItem(String valuesItem) {
    if (this.values == null) {
      this.values = new ArrayList<>();
    }
    this.values.add(valuesItem);
    return this;
  }

  /**
   * List of filter values
   * @return values
   */
  @NotNull 
  @Schema(name = "values", description = "List of filter values", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("values")
  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ComparisonToolConfigFilterDto comparisonToolConfigFilter = (ComparisonToolConfigFilterDto) o;
    return Objects.equals(this.name, comparisonToolConfigFilter.name) &&
        Objects.equals(this.dataKey, comparisonToolConfigFilter.dataKey) &&
        Objects.equals(this.shortName, comparisonToolConfigFilter.shortName) &&
        Objects.equals(this.queryParamKey, comparisonToolConfigFilter.queryParamKey) &&
        Objects.equals(this.values, comparisonToolConfigFilter.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, dataKey, shortName, queryParamKey, values);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ComparisonToolConfigFilterDto {\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    dataKey: ").append(toIndentedString(dataKey)).append("\n");
    sb.append("    shortName: ").append(toIndentedString(shortName)).append("\n");
    sb.append("    queryParamKey: ").append(toIndentedString(queryParamKey)).append("\n");
    sb.append("    values: ").append(toIndentedString(values)).append("\n");
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

    private ComparisonToolConfigFilterDto instance;

    public Builder() {
      this(new ComparisonToolConfigFilterDto());
    }

    protected Builder(ComparisonToolConfigFilterDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ComparisonToolConfigFilterDto value) { 
      this.instance.setName(value.name);
      this.instance.setDataKey(value.dataKey);
      this.instance.setShortName(value.shortName);
      this.instance.setQueryParamKey(value.queryParamKey);
      this.instance.setValues(value.values);
      return this;
    }

    public ComparisonToolConfigFilterDto.Builder name(String name) {
      this.instance.name(name);
      return this;
    }
    
    public ComparisonToolConfigFilterDto.Builder dataKey(String dataKey) {
      this.instance.dataKey(dataKey);
      return this;
    }
    
    public ComparisonToolConfigFilterDto.Builder shortName(String shortName) {
      this.instance.shortName(shortName);
      return this;
    }
    
    public ComparisonToolConfigFilterDto.Builder queryParamKey(String queryParamKey) {
      this.instance.queryParamKey(queryParamKey);
      return this;
    }
    
    public ComparisonToolConfigFilterDto.Builder values(List<String> values) {
      this.instance.values(values);
      return this;
    }
    
    /**
    * returns a built ComparisonToolConfigFilterDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ComparisonToolConfigFilterDto build() {
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
  public static ComparisonToolConfigFilterDto.Builder builder() {
    return new ComparisonToolConfigFilterDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ComparisonToolConfigFilterDto.Builder toBuilder() {
    ComparisonToolConfigFilterDto.Builder builder = new ComparisonToolConfigFilterDto.Builder();
    return builder.copyOf(this);
  }

}

