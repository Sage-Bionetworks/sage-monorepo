package org.sagebionetworks.agora.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugDto;
import org.sagebionetworks.agora.api.next.model.dto.PageMetadataDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of nominated drug objects.
 */

@Schema(name = "NominatedDrugsPage", description = "A page of nominated drug objects.")
@JsonTypeName("NominatedDrugsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class NominatedDrugsPageDto {

  @Valid
  private List<@Valid NominatedDrugDto> nominatedDrugs = new ArrayList<>();

  private PageMetadataDto page;

  public NominatedDrugsPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NominatedDrugsPageDto(List<@Valid NominatedDrugDto> nominatedDrugs, PageMetadataDto page) {
    this.nominatedDrugs = nominatedDrugs;
    this.page = page;
  }

  public NominatedDrugsPageDto nominatedDrugs(List<@Valid NominatedDrugDto> nominatedDrugs) {
    this.nominatedDrugs = nominatedDrugs;
    return this;
  }

  public NominatedDrugsPageDto addNominatedDrugsItem(NominatedDrugDto nominatedDrugsItem) {
    if (this.nominatedDrugs == null) {
      this.nominatedDrugs = new ArrayList<>();
    }
    this.nominatedDrugs.add(nominatedDrugsItem);
    return this;
  }

  /**
   * Get nominatedDrugs
   * @return nominatedDrugs
   */
  @NotNull @Valid 
  @Schema(name = "nominatedDrugs", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("nominatedDrugs")
  public List<@Valid NominatedDrugDto> getNominatedDrugs() {
    return nominatedDrugs;
  }

  public void setNominatedDrugs(List<@Valid NominatedDrugDto> nominatedDrugs) {
    this.nominatedDrugs = nominatedDrugs;
  }

  public NominatedDrugsPageDto page(PageMetadataDto page) {
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
    NominatedDrugsPageDto nominatedDrugsPage = (NominatedDrugsPageDto) o;
    return Objects.equals(this.nominatedDrugs, nominatedDrugsPage.nominatedDrugs) &&
        Objects.equals(this.page, nominatedDrugsPage.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nominatedDrugs, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NominatedDrugsPageDto {\n");
    sb.append("    nominatedDrugs: ").append(toIndentedString(nominatedDrugs)).append("\n");
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

    private NominatedDrugsPageDto instance;

    public Builder() {
      this(new NominatedDrugsPageDto());
    }

    protected Builder(NominatedDrugsPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(NominatedDrugsPageDto value) { 
      this.instance.setNominatedDrugs(value.nominatedDrugs);
      this.instance.setPage(value.page);
      return this;
    }

    public NominatedDrugsPageDto.Builder nominatedDrugs(List<NominatedDrugDto> nominatedDrugs) {
      this.instance.nominatedDrugs(nominatedDrugs);
      return this;
    }
    
    public NominatedDrugsPageDto.Builder page(PageMetadataDto page) {
      this.instance.page(page);
      return this;
    }
    
    /**
    * returns a built NominatedDrugsPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public NominatedDrugsPageDto build() {
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
  public static NominatedDrugsPageDto.Builder builder() {
    return new NominatedDrugsPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public NominatedDrugsPageDto.Builder toBuilder() {
    NominatedDrugsPageDto.Builder builder = new NominatedDrugsPageDto.Builder();
    return builder.copyOf(this);
  }

}

