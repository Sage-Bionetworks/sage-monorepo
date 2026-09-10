package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A paginated response containing proteomics objects and pagination metadata.
 */

@Schema(name = "ProteomicsPage", description = "A paginated response containing proteomics objects and pagination metadata.")
@JsonTypeName("ProteomicsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ProteomicsPageDto {

  @Valid
  private List<@Valid ProteomicsDto> proteomics = new ArrayList<>();

  private PageMetadataDto page;

  public ProteomicsPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ProteomicsPageDto(List<@Valid ProteomicsDto> proteomics, PageMetadataDto page) {
    this.proteomics = proteomics;
    this.page = page;
  }

  public ProteomicsPageDto proteomics(List<@Valid ProteomicsDto> proteomics) {
    this.proteomics = proteomics;
    return this;
  }

  public ProteomicsPageDto addProteomicsItem(ProteomicsDto proteomicsItem) {
    if (this.proteomics == null) {
      this.proteomics = new ArrayList<>();
    }
    this.proteomics.add(proteomicsItem);
    return this;
  }

  /**
   * The list of proteomics objects for the current page
   * @return proteomics
   */
  @NotNull @Valid 
  @Schema(name = "proteomics", description = "The list of proteomics objects for the current page", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("proteomics")
  public List<@Valid ProteomicsDto> getProteomics() {
    return proteomics;
  }

  public void setProteomics(List<@Valid ProteomicsDto> proteomics) {
    this.proteomics = proteomics;
  }

  public ProteomicsPageDto page(PageMetadataDto page) {
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
    ProteomicsPageDto proteomicsPage = (ProteomicsPageDto) o;
    return Objects.equals(this.proteomics, proteomicsPage.proteomics) &&
        Objects.equals(this.page, proteomicsPage.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(proteomics, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ProteomicsPageDto {\n");
    sb.append("    proteomics: ").append(toIndentedString(proteomics)).append("\n");
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

    private ProteomicsPageDto instance;

    public Builder() {
      this(new ProteomicsPageDto());
    }

    protected Builder(ProteomicsPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ProteomicsPageDto value) { 
      this.instance.setProteomics(value.proteomics);
      this.instance.setPage(value.page);
      return this;
    }

    public ProteomicsPageDto.Builder proteomics(List<ProteomicsDto> proteomics) {
      this.instance.proteomics(proteomics);
      return this;
    }
    
    public ProteomicsPageDto.Builder page(PageMetadataDto page) {
      this.instance.page(page);
      return this;
    }
    
    /**
    * returns a built ProteomicsPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ProteomicsPageDto build() {
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
  public static ProteomicsPageDto.Builder builder() {
    return new ProteomicsPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ProteomicsPageDto.Builder toBuilder() {
    ProteomicsPageDto.Builder builder = new ProteomicsPageDto.Builder();
    return builder.copyOf(this);
  }

}

