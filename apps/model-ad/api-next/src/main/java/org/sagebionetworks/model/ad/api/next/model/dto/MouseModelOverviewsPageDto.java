package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of mouse model overview objects.
 */

@Schema(name = "MouseModelOverviewsPage", description = "A page of mouse model overview objects.")
@JsonTypeName("MouseModelOverviewsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class MouseModelOverviewsPageDto {

  @Valid
  private List<@Valid MouseModelOverviewDto> mouseModelOverviews = new ArrayList<>();

  private PageMetadataDto page;

  public MouseModelOverviewsPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public MouseModelOverviewsPageDto(List<@Valid MouseModelOverviewDto> mouseModelOverviews, PageMetadataDto page) {
    this.mouseModelOverviews = mouseModelOverviews;
    this.page = page;
  }

  public MouseModelOverviewsPageDto mouseModelOverviews(List<@Valid MouseModelOverviewDto> mouseModelOverviews) {
    this.mouseModelOverviews = mouseModelOverviews;
    return this;
  }

  public MouseModelOverviewsPageDto addMouseModelOverviewsItem(MouseModelOverviewDto mouseModelOverviewsItem) {
    if (this.mouseModelOverviews == null) {
      this.mouseModelOverviews = new ArrayList<>();
    }
    this.mouseModelOverviews.add(mouseModelOverviewsItem);
    return this;
  }

  /**
   * List of mouse model overviews in this page.
   * @return mouseModelOverviews
   */
  @NotNull @Valid 
  @Schema(name = "mouseModelOverviews", description = "List of mouse model overviews in this page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("mouseModelOverviews")
  public List<@Valid MouseModelOverviewDto> getMouseModelOverviews() {
    return mouseModelOverviews;
  }

  public void setMouseModelOverviews(List<@Valid MouseModelOverviewDto> mouseModelOverviews) {
    this.mouseModelOverviews = mouseModelOverviews;
  }

  public MouseModelOverviewsPageDto page(PageMetadataDto page) {
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
    MouseModelOverviewsPageDto mouseModelOverviewsPage = (MouseModelOverviewsPageDto) o;
    return Objects.equals(this.mouseModelOverviews, mouseModelOverviewsPage.mouseModelOverviews) &&
        Objects.equals(this.page, mouseModelOverviewsPage.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mouseModelOverviews, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class MouseModelOverviewsPageDto {\n");
    sb.append("    mouseModelOverviews: ").append(toIndentedString(mouseModelOverviews)).append("\n");
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

    private MouseModelOverviewsPageDto instance;

    public Builder() {
      this(new MouseModelOverviewsPageDto());
    }

    protected Builder(MouseModelOverviewsPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(MouseModelOverviewsPageDto value) { 
      this.instance.setMouseModelOverviews(value.mouseModelOverviews);
      this.instance.setPage(value.page);
      return this;
    }

    public MouseModelOverviewsPageDto.Builder mouseModelOverviews(List<MouseModelOverviewDto> mouseModelOverviews) {
      this.instance.mouseModelOverviews(mouseModelOverviews);
      return this;
    }
    
    public MouseModelOverviewsPageDto.Builder page(PageMetadataDto page) {
      this.instance.page(page);
      return this;
    }
    
    /**
    * returns a built MouseModelOverviewsPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public MouseModelOverviewsPageDto build() {
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
  public static MouseModelOverviewsPageDto.Builder builder() {
    return new MouseModelOverviewsPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public MouseModelOverviewsPageDto.Builder toBuilder() {
    MouseModelOverviewsPageDto.Builder builder = new MouseModelOverviewsPageDto.Builder();
    return builder.copyOf(this);
  }

}

