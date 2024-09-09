package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** Links to navigate to different pages of results */
@Schema(
  name = "PageMetadata_paging",
  description = "Links to navigate to different pages of results"
)
@JsonTypeName("PageMetadata_paging")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class PageMetadataPagingDto {

  @JsonProperty("next")
  private String next;

  public PageMetadataPagingDto next(String next) {
    this.next = next;
    return this;
  }

  /**
   * Link to the next page of results
   *
   * @return next
   */
  @Schema(name = "next", description = "Link to the next page of results", required = false)
  public String getNext() {
    return next;
  }

  public void setNext(String next) {
    this.next = next;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PageMetadataPagingDto pageMetadataPaging = (PageMetadataPagingDto) o;
    return Objects.equals(this.next, pageMetadataPaging.next);
  }

  @Override
  public int hashCode() {
    return Objects.hash(next);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PageMetadataPagingDto {\n");
    sb.append("    next: ").append(toIndentedString(next)).append("\n");
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
