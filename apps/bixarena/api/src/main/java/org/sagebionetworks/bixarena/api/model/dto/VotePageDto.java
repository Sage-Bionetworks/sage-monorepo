package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.PageMetadataDto;
import org.sagebionetworks.bixarena.api.model.dto.VoteDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of votes
 */

@Schema(name = "VotePage", description = "A page of votes")
@JsonTypeName("VotePage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class VotePageDto {

  @Valid
  private List<@Valid VoteDto> data = new ArrayList<>();

  private PageMetadataDto page;

  public VotePageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public VotePageDto(List<@Valid VoteDto> data, PageMetadataDto page) {
    this.data = data;
    this.page = page;
  }

  public VotePageDto data(List<@Valid VoteDto> data) {
    this.data = data;
    return this;
  }

  public VotePageDto addDataItem(VoteDto dataItem) {
    if (this.data == null) {
      this.data = new ArrayList<>();
    }
    this.data.add(dataItem);
    return this;
  }

  /**
   * Get data
   * @return data
   */
  @NotNull @Valid 
  @Schema(name = "data", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("data")
  public List<@Valid VoteDto> getData() {
    return data;
  }

  public void setData(List<@Valid VoteDto> data) {
    this.data = data;
  }

  public VotePageDto page(PageMetadataDto page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * @return page
   */
  @NotNull @Valid 
  @Schema(name = "page", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("page")
  public PageMetadataDto getPage() {
    return page;
  }

  public void setPage(PageMetadataDto page) {
    this.page = page;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VotePageDto votePage = (VotePageDto) o;
    return Objects.equals(this.data, votePage.data) &&
        Objects.equals(this.page, votePage.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class VotePageDto {\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
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

    private VotePageDto instance;

    public Builder() {
      this(new VotePageDto());
    }

    protected Builder(VotePageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(VotePageDto value) { 
      this.instance.setData(value.data);
      this.instance.setPage(value.page);
      return this;
    }

    public VotePageDto.Builder data(List<VoteDto> data) {
      this.instance.data(data);
      return this;
    }
    
    public VotePageDto.Builder page(PageMetadataDto page) {
      this.instance.page(page);
      return this;
    }
    
    /**
    * returns a built VotePageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public VotePageDto build() {
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
  public static VotePageDto.Builder builder() {
    return new VotePageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public VotePageDto.Builder toBuilder() {
    VotePageDto.Builder builder = new VotePageDto.Builder();
    return builder.copyOf(this);
  }

}

