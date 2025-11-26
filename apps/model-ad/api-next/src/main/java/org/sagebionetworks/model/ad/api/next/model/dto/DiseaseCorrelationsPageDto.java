package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationDto;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A paginated response containing disease correlation objects and pagination metadata.
 */

@Schema(name = "DiseaseCorrelationsPage", description = "A paginated response containing disease correlation objects and pagination metadata.")
@JsonTypeName("DiseaseCorrelationsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class DiseaseCorrelationsPageDto {

  @Valid
  private List<@Valid DiseaseCorrelationDto> diseaseCorrelations = new ArrayList<>();

  private PageMetadataDto page;

  public DiseaseCorrelationsPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DiseaseCorrelationsPageDto(List<@Valid DiseaseCorrelationDto> diseaseCorrelations, PageMetadataDto page) {
    this.diseaseCorrelations = diseaseCorrelations;
    this.page = page;
  }

  public DiseaseCorrelationsPageDto diseaseCorrelations(List<@Valid DiseaseCorrelationDto> diseaseCorrelations) {
    this.diseaseCorrelations = diseaseCorrelations;
    return this;
  }

  public DiseaseCorrelationsPageDto addDiseaseCorrelationsItem(DiseaseCorrelationDto diseaseCorrelationsItem) {
    if (this.diseaseCorrelations == null) {
      this.diseaseCorrelations = new ArrayList<>();
    }
    this.diseaseCorrelations.add(diseaseCorrelationsItem);
    return this;
  }

  /**
   * The list of disease correlation objects for the current page
   * @return diseaseCorrelations
   */
  @NotNull @Valid 
  @Schema(name = "diseaseCorrelations", description = "The list of disease correlation objects for the current page", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("diseaseCorrelations")
  public List<@Valid DiseaseCorrelationDto> getDiseaseCorrelations() {
    return diseaseCorrelations;
  }

  public void setDiseaseCorrelations(List<@Valid DiseaseCorrelationDto> diseaseCorrelations) {
    this.diseaseCorrelations = diseaseCorrelations;
  }

  public DiseaseCorrelationsPageDto page(PageMetadataDto page) {
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
    DiseaseCorrelationsPageDto diseaseCorrelationsPage = (DiseaseCorrelationsPageDto) o;
    return Objects.equals(this.diseaseCorrelations, diseaseCorrelationsPage.diseaseCorrelations) &&
        Objects.equals(this.page, diseaseCorrelationsPage.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(diseaseCorrelations, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DiseaseCorrelationsPageDto {\n");
    sb.append("    diseaseCorrelations: ").append(toIndentedString(diseaseCorrelations)).append("\n");
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

    private DiseaseCorrelationsPageDto instance;

    public Builder() {
      this(new DiseaseCorrelationsPageDto());
    }

    protected Builder(DiseaseCorrelationsPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(DiseaseCorrelationsPageDto value) { 
      this.instance.setDiseaseCorrelations(value.diseaseCorrelations);
      this.instance.setPage(value.page);
      return this;
    }

    public DiseaseCorrelationsPageDto.Builder diseaseCorrelations(List<DiseaseCorrelationDto> diseaseCorrelations) {
      this.instance.diseaseCorrelations(diseaseCorrelations);
      return this;
    }
    
    public DiseaseCorrelationsPageDto.Builder page(PageMetadataDto page) {
      this.instance.page(page);
      return this;
    }
    
    /**
    * returns a built DiseaseCorrelationsPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public DiseaseCorrelationsPageDto build() {
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
  public static DiseaseCorrelationsPageDto.Builder builder() {
    return new DiseaseCorrelationsPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public DiseaseCorrelationsPageDto.Builder toBuilder() {
    DiseaseCorrelationsPageDto.Builder builder = new DiseaseCorrelationsPageDto.Builder();
    return builder.copyOf(this);
  }

}

