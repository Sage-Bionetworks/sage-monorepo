package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Search result
 */

@Schema(name = "SearchResult", description = "Search result")
@JsonTypeName("SearchResult")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class SearchResultDto {

  private String id;

  private String matchField;

  private String matchValue;

  public SearchResultDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SearchResultDto(String id, String matchField, String matchValue) {
    this.id = id;
    this.matchField = matchField;
    this.matchValue = matchValue;
  }

  public SearchResultDto id(String id) {
    this.id = id;
    return this;
  }

  /**
   * ID of result
   * @return id
   */
  @NotNull 
  @Schema(name = "id", description = "ID of result", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public SearchResultDto matchField(String matchField) {
    this.matchField = matchField;
    return this;
  }

  /**
   * Field that matched the query
   * @return matchField
   */
  @NotNull 
  @Schema(name = "match_field", description = "Field that matched the query", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("match_field")
  public String getMatchField() {
    return matchField;
  }

  public void setMatchField(String matchField) {
    this.matchField = matchField;
  }

  public SearchResultDto matchValue(String matchValue) {
    this.matchValue = matchValue;
    return this;
  }

  /**
   * Value that matched the query
   * @return matchValue
   */
  @NotNull 
  @Schema(name = "match_value", description = "Value that matched the query", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("match_value")
  public String getMatchValue() {
    return matchValue;
  }

  public void setMatchValue(String matchValue) {
    this.matchValue = matchValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SearchResultDto searchResult = (SearchResultDto) o;
    return Objects.equals(this.id, searchResult.id) &&
        Objects.equals(this.matchField, searchResult.matchField) &&
        Objects.equals(this.matchValue, searchResult.matchValue);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, matchField, matchValue);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SearchResultDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    matchField: ").append(toIndentedString(matchField)).append("\n");
    sb.append("    matchValue: ").append(toIndentedString(matchValue)).append("\n");
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

    private SearchResultDto instance;

    public Builder() {
      this(new SearchResultDto());
    }

    protected Builder(SearchResultDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(SearchResultDto value) { 
      this.instance.setId(value.id);
      this.instance.setMatchField(value.matchField);
      this.instance.setMatchValue(value.matchValue);
      return this;
    }

    public SearchResultDto.Builder id(String id) {
      this.instance.id(id);
      return this;
    }
    
    public SearchResultDto.Builder matchField(String matchField) {
      this.instance.matchField(matchField);
      return this;
    }
    
    public SearchResultDto.Builder matchValue(String matchValue) {
      this.instance.matchValue(matchValue);
      return this;
    }
    
    /**
    * returns a built SearchResultDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public SearchResultDto build() {
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
  public static SearchResultDto.Builder builder() {
    return new SearchResultDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public SearchResultDto.Builder toBuilder() {
    SearchResultDto.Builder builder = new SearchResultDto.Builder();
    return builder.copyOf(this);
  }

}

