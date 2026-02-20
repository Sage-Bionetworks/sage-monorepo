package org.sagebionetworks.agora.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetDto;
import org.sagebionetworks.agora.api.next.model.dto.PageMetadataDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of nominated target objects.
 */

@Schema(name = "NominatedTargetsPage", description = "A page of nominated target objects.")
@JsonTypeName("NominatedTargetsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class NominatedTargetsPageDto {

  @Valid
  private List<@Valid NominatedTargetDto> nominatedTargets = new ArrayList<>();

  private PageMetadataDto page;

  public NominatedTargetsPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public NominatedTargetsPageDto(List<@Valid NominatedTargetDto> nominatedTargets, PageMetadataDto page) {
    this.nominatedTargets = nominatedTargets;
    this.page = page;
  }

  public NominatedTargetsPageDto nominatedTargets(List<@Valid NominatedTargetDto> nominatedTargets) {
    this.nominatedTargets = nominatedTargets;
    return this;
  }

  public NominatedTargetsPageDto addNominatedTargetsItem(NominatedTargetDto nominatedTargetsItem) {
    if (this.nominatedTargets == null) {
      this.nominatedTargets = new ArrayList<>();
    }
    this.nominatedTargets.add(nominatedTargetsItem);
    return this;
  }

  /**
   * List of nominated targets in this page.
   * @return nominatedTargets
   */
  @NotNull @Valid 
  @Schema(name = "nominatedTargets", description = "List of nominated targets in this page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("nominatedTargets")
  public List<@Valid NominatedTargetDto> getNominatedTargets() {
    return nominatedTargets;
  }

  public void setNominatedTargets(List<@Valid NominatedTargetDto> nominatedTargets) {
    this.nominatedTargets = nominatedTargets;
  }

  public NominatedTargetsPageDto page(PageMetadataDto page) {
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
    NominatedTargetsPageDto nominatedTargetsPage = (NominatedTargetsPageDto) o;
    return Objects.equals(this.nominatedTargets, nominatedTargetsPage.nominatedTargets) &&
        Objects.equals(this.page, nominatedTargetsPage.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nominatedTargets, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NominatedTargetsPageDto {\n");
    sb.append("    nominatedTargets: ").append(toIndentedString(nominatedTargets)).append("\n");
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

    private NominatedTargetsPageDto instance;

    public Builder() {
      this(new NominatedTargetsPageDto());
    }

    protected Builder(NominatedTargetsPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(NominatedTargetsPageDto value) { 
      this.instance.setNominatedTargets(value.nominatedTargets);
      this.instance.setPage(value.page);
      return this;
    }

    public NominatedTargetsPageDto.Builder nominatedTargets(List<NominatedTargetDto> nominatedTargets) {
      this.instance.nominatedTargets(nominatedTargets);
      return this;
    }
    
    public NominatedTargetsPageDto.Builder page(PageMetadataDto page) {
      this.instance.page(page);
      return this;
    }
    
    /**
    * returns a built NominatedTargetsPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public NominatedTargetsPageDto build() {
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
  public static NominatedTargetsPageDto.Builder builder() {
    return new NominatedTargetsPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public NominatedTargetsPageDto.Builder toBuilder() {
    NominatedTargetsPageDto.Builder builder = new NominatedTargetsPageDto.Builder();
    return builder.copyOf(this);
  }

}

