package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of marmoset model overview objects.
 */

@Schema(name = "MarmosetModelOverviewsPage", description = "A page of marmoset model overview objects.")
@JsonTypeName("MarmosetModelOverviewsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class MarmosetModelOverviewsPageDto {

  @Valid
  private List<@Valid MarmosetModelOverviewDto> marmosetModelOverviews = new ArrayList<>();

  private PageMetadataDto page;

  public MarmosetModelOverviewsPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MarmosetModelOverviewsPageDto(List<@Valid MarmosetModelOverviewDto> marmosetModelOverviews, PageMetadataDto page) {
    this.marmosetModelOverviews = marmosetModelOverviews;
    this.page = page;
  }

  public MarmosetModelOverviewsPageDto marmosetModelOverviews(List<@Valid MarmosetModelOverviewDto> marmosetModelOverviews) {
    this.marmosetModelOverviews = marmosetModelOverviews;
    return this;
  }

  public MarmosetModelOverviewsPageDto addMarmosetModelOverviewsItem(MarmosetModelOverviewDto marmosetModelOverviewsItem) {
    if (this.marmosetModelOverviews == null) {
      this.marmosetModelOverviews = new ArrayList<>();
    }
    this.marmosetModelOverviews.add(marmosetModelOverviewsItem);
    return this;
  }

  /**
   * List of marmoset model overviews in this page.
   * @return marmosetModelOverviews
   */
  @NotNull @Valid 
  @Schema(name = "marmosetModelOverviews", description = "List of marmoset model overviews in this page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("marmosetModelOverviews")
  public List<@Valid MarmosetModelOverviewDto> getMarmosetModelOverviews() {
    return marmosetModelOverviews;
  }

  public void setMarmosetModelOverviews(List<@Valid MarmosetModelOverviewDto> marmosetModelOverviews) {
    this.marmosetModelOverviews = marmosetModelOverviews;
  }

  public MarmosetModelOverviewsPageDto page(PageMetadataDto page) {
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
    MarmosetModelOverviewsPageDto marmosetModelOverviewsPage = (MarmosetModelOverviewsPageDto) o;
    return Objects.equals(this.marmosetModelOverviews, marmosetModelOverviewsPage.marmosetModelOverviews) &&
        Objects.equals(this.page, marmosetModelOverviewsPage.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(marmosetModelOverviews, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MarmosetModelOverviewsPageDto {\n");
    sb.append("    marmosetModelOverviews: ").append(toIndentedString(marmosetModelOverviews)).append("\n");
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

    private MarmosetModelOverviewsPageDto instance;

    public Builder() {
      this(new MarmosetModelOverviewsPageDto());
    }

    protected Builder(MarmosetModelOverviewsPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(MarmosetModelOverviewsPageDto value) { 
      this.instance.setMarmosetModelOverviews(value.marmosetModelOverviews);
      this.instance.setPage(value.page);
      return this;
    }

    public MarmosetModelOverviewsPageDto.Builder marmosetModelOverviews(List<MarmosetModelOverviewDto> marmosetModelOverviews) {
      this.instance.marmosetModelOverviews(marmosetModelOverviews);
      return this;
    }
    
    public MarmosetModelOverviewsPageDto.Builder page(PageMetadataDto page) {
      this.instance.page(page);
      return this;
    }
    
    /**
    * returns a built MarmosetModelOverviewsPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public MarmosetModelOverviewsPageDto build() {
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
  public static MarmosetModelOverviewsPageDto.Builder builder() {
    return new MarmosetModelOverviewsPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public MarmosetModelOverviewsPageDto.Builder toBuilder() {
    MarmosetModelOverviewsPageDto.Builder builder = new MarmosetModelOverviewsPageDto.Builder();
    return builder.copyOf(this);
  }

}

