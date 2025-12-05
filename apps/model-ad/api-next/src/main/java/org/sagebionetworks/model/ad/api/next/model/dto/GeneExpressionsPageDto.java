package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionDto;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A paginated response containing gene expression objects and pagination metadata.
 */

@Schema(name = "GeneExpressionsPage", description = "A paginated response containing gene expression objects and pagination metadata.")
@JsonTypeName("GeneExpressionsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GeneExpressionsPageDto {

  @Valid
  private List<@Valid GeneExpressionDto> geneExpressions = new ArrayList<>();

  private PageMetadataDto page;

  public GeneExpressionsPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GeneExpressionsPageDto(List<@Valid GeneExpressionDto> geneExpressions, PageMetadataDto page) {
    this.geneExpressions = geneExpressions;
    this.page = page;
  }

  public GeneExpressionsPageDto geneExpressions(List<@Valid GeneExpressionDto> geneExpressions) {
    this.geneExpressions = geneExpressions;
    return this;
  }

  public GeneExpressionsPageDto addGeneExpressionsItem(GeneExpressionDto geneExpressionsItem) {
    if (this.geneExpressions == null) {
      this.geneExpressions = new ArrayList<>();
    }
    this.geneExpressions.add(geneExpressionsItem);
    return this;
  }

  /**
   * The list of gene expression objects for the current page
   * @return geneExpressions
   */
  @NotNull @Valid 
  @Schema(name = "geneExpressions", description = "The list of gene expression objects for the current page", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("geneExpressions")
  public List<@Valid GeneExpressionDto> getGeneExpressions() {
    return geneExpressions;
  }

  public void setGeneExpressions(List<@Valid GeneExpressionDto> geneExpressions) {
    this.geneExpressions = geneExpressions;
  }

  public GeneExpressionsPageDto page(PageMetadataDto page) {
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
    GeneExpressionsPageDto geneExpressionsPage = (GeneExpressionsPageDto) o;
    return Objects.equals(this.geneExpressions, geneExpressionsPage.geneExpressions) &&
        Objects.equals(this.page, geneExpressionsPage.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(geneExpressions, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GeneExpressionsPageDto {\n");
    sb.append("    geneExpressions: ").append(toIndentedString(geneExpressions)).append("\n");
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

    private GeneExpressionsPageDto instance;

    public Builder() {
      this(new GeneExpressionsPageDto());
    }

    protected Builder(GeneExpressionsPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GeneExpressionsPageDto value) { 
      this.instance.setGeneExpressions(value.geneExpressions);
      this.instance.setPage(value.page);
      return this;
    }

    public GeneExpressionsPageDto.Builder geneExpressions(List<GeneExpressionDto> geneExpressions) {
      this.instance.geneExpressions(geneExpressions);
      return this;
    }
    
    public GeneExpressionsPageDto.Builder page(PageMetadataDto page) {
      this.instance.page(page);
      return this;
    }
    
    /**
    * returns a built GeneExpressionsPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GeneExpressionsPageDto build() {
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
  public static GeneExpressionsPageDto.Builder builder() {
    return new GeneExpressionsPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GeneExpressionsPageDto.Builder toBuilder() {
    GeneExpressionsPageDto.Builder builder = new GeneExpressionsPageDto.Builder();
    return builder.copyOf(this);
  }

}

