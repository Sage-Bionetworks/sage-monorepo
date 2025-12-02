package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOverviewDto;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of model overview objects.
 */

@Schema(name = "ModelOverviewsPage", description = "A page of model overview objects.")
@JsonTypeName("ModelOverviewsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ModelOverviewsPageDto {

  @Valid
  private List<@Valid ModelOverviewDto> modelOverviews = new ArrayList<>();

  private PageMetadataDto page;

  public ModelOverviewsPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ModelOverviewsPageDto(List<@Valid ModelOverviewDto> modelOverviews, PageMetadataDto page) {
    this.modelOverviews = modelOverviews;
    this.page = page;
  }

  public ModelOverviewsPageDto modelOverviews(List<@Valid ModelOverviewDto> modelOverviews) {
    this.modelOverviews = modelOverviews;
    return this;
  }

  public ModelOverviewsPageDto addModelOverviewsItem(ModelOverviewDto modelOverviewsItem) {
    if (this.modelOverviews == null) {
      this.modelOverviews = new ArrayList<>();
    }
    this.modelOverviews.add(modelOverviewsItem);
    return this;
  }

  /**
   * List of model overviews in this page.
   * @return modelOverviews
   */
  @NotNull @Valid 
  @Schema(name = "modelOverviews", description = "List of model overviews in this page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("modelOverviews")
  public List<@Valid ModelOverviewDto> getModelOverviews() {
    return modelOverviews;
  }

  public void setModelOverviews(List<@Valid ModelOverviewDto> modelOverviews) {
    this.modelOverviews = modelOverviews;
  }

  public ModelOverviewsPageDto page(PageMetadataDto page) {
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
    ModelOverviewsPageDto modelOverviewsPage = (ModelOverviewsPageDto) o;
    return Objects.equals(this.modelOverviews, modelOverviewsPage.modelOverviews) &&
        Objects.equals(this.page, modelOverviewsPage.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(modelOverviews, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelOverviewsPageDto {\n");
    sb.append("    modelOverviews: ").append(toIndentedString(modelOverviews)).append("\n");
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

    private ModelOverviewsPageDto instance;

    public Builder() {
      this(new ModelOverviewsPageDto());
    }

    protected Builder(ModelOverviewsPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ModelOverviewsPageDto value) { 
      this.instance.setModelOverviews(value.modelOverviews);
      this.instance.setPage(value.page);
      return this;
    }

    public ModelOverviewsPageDto.Builder modelOverviews(List<ModelOverviewDto> modelOverviews) {
      this.instance.modelOverviews(modelOverviews);
      return this;
    }
    
    public ModelOverviewsPageDto.Builder page(PageMetadataDto page) {
      this.instance.page(page);
      return this;
    }
    
    /**
    * returns a built ModelOverviewsPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ModelOverviewsPageDto build() {
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
  public static ModelOverviewsPageDto.Builder builder() {
    return new ModelOverviewsPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ModelOverviewsPageDto.Builder toBuilder() {
    ModelOverviewsPageDto.Builder builder = new ModelOverviewsPageDto.Builder();
    return builder.copyOf(this);
  }

}

