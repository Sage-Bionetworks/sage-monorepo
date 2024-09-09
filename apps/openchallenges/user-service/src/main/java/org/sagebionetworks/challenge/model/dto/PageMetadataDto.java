package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/** The metadata of a page */
@Schema(name = "PageMetadata", description = "The metadata of a page")
@JsonTypeName("PageMetadata")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class PageMetadataDto {

  @JsonProperty("paging")
  private PageMetadataPagingDto paging;

  @JsonProperty("totalResults")
  private Integer totalResults;

  public PageMetadataDto paging(PageMetadataPagingDto paging) {
    this.paging = paging;
    return this;
  }

  /**
   * Get paging
   *
   * @return paging
   */
  @NotNull
  @Valid
  @Schema(name = "paging", required = true)
  public PageMetadataPagingDto getPaging() {
    return paging;
  }

  public void setPaging(PageMetadataPagingDto paging) {
    this.paging = paging;
  }

  public PageMetadataDto totalResults(Integer totalResults) {
    this.totalResults = totalResults;
    return this;
  }

  /**
   * Total number of results in the result set
   *
   * @return totalResults
   */
  @NotNull
  @Schema(
    name = "totalResults",
    description = "Total number of results in the result set",
    required = true
  )
  public Integer getTotalResults() {
    return totalResults;
  }

  public void setTotalResults(Integer totalResults) {
    this.totalResults = totalResults;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PageMetadataDto pageMetadata = (PageMetadataDto) o;
    return (
      Objects.equals(this.paging, pageMetadata.paging) &&
      Objects.equals(this.totalResults, pageMetadata.totalResults)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(paging, totalResults);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PageMetadataDto {\n");
    sb.append("    paging: ").append(toIndentedString(paging)).append("\n");
    sb.append("    totalResults: ").append(toIndentedString(totalResults)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
