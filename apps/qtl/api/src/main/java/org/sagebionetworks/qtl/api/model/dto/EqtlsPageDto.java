package org.sagebionetworks.qtl.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.qtl.api.model.dto.EqtlDto;
import org.sagebionetworks.qtl.api.model.dto.PageMetadataDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A paginated response containing eQTL objects and pagination metadata.
 */

@Schema(name = "EqtlsPage", description = "A paginated response containing eQTL objects and pagination metadata.")
@JsonTypeName("EqtlsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class EqtlsPageDto {

  @Valid
  private List<@Valid EqtlDto> eqtls = new ArrayList<>();

  private PageMetadataDto page;

  private @Nullable Boolean selectedRowInResults = null;

  public EqtlsPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public EqtlsPageDto(List<@Valid EqtlDto> eqtls, PageMetadataDto page) {
    this.eqtls = eqtls;
    this.page = page;
  }

  public EqtlsPageDto eqtls(List<@Valid EqtlDto> eqtls) {
    this.eqtls = eqtls;
    return this;
  }

  public EqtlsPageDto addEqtlsItem(EqtlDto eqtlsItem) {
    if (this.eqtls == null) {
      this.eqtls = new ArrayList<>();
    }
    this.eqtls.add(eqtlsItem);
    return this;
  }

  /**
   * The list of eQTL objects for the current page
   * @return eqtls
   */
  @NotNull @Valid 
  @Schema(name = "eqtls", description = "The list of eQTL objects for the current page", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("eqtls")
  public List<@Valid EqtlDto> getEqtls() {
    return eqtls;
  }

  public void setEqtls(List<@Valid EqtlDto> eqtls) {
    this.eqtls = eqtls;
  }

  public EqtlsPageDto page(PageMetadataDto page) {
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

  public EqtlsPageDto selectedRowInResults(@Nullable Boolean selectedRowInResults) {
    this.selectedRowInResults = selectedRowInResults;
    return this;
  }

  /**
   * Over the full filtered results, whether they contain the row named by the request's selectedRowId, regardless of the current page. A row listed in the request's items is not in them, so it answers false. Null when the request carries no selectedRowId or itemFilterType is 'include'. 
   * @return selectedRowInResults
   */
  
  @Schema(name = "selectedRowInResults", description = "Over the full filtered results, whether they contain the row named by the request's selectedRowId, regardless of the current page. A row listed in the request's items is not in them, so it answers false. Null when the request carries no selectedRowId or itemFilterType is 'include'. ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("selectedRowInResults")
  public @Nullable Boolean getSelectedRowInResults() {
    return selectedRowInResults;
  }

  public void setSelectedRowInResults(@Nullable Boolean selectedRowInResults) {
    this.selectedRowInResults = selectedRowInResults;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EqtlsPageDto eqtlsPage = (EqtlsPageDto) o;
    return Objects.equals(this.eqtls, eqtlsPage.eqtls) &&
        Objects.equals(this.page, eqtlsPage.page) &&
        Objects.equals(this.selectedRowInResults, eqtlsPage.selectedRowInResults);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eqtls, page, selectedRowInResults);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class EqtlsPageDto {\n");
    sb.append("    eqtls: ").append(toIndentedString(eqtls)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
    sb.append("    selectedRowInResults: ").append(toIndentedString(selectedRowInResults)).append("\n");
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

    private EqtlsPageDto instance;

    public Builder() {
      this(new EqtlsPageDto());
    }

    protected Builder(EqtlsPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(EqtlsPageDto value) { 
      this.instance.setEqtls(value.eqtls);
      this.instance.setPage(value.page);
      this.instance.setSelectedRowInResults(value.selectedRowInResults);
      return this;
    }

    public EqtlsPageDto.Builder eqtls(List<EqtlDto> eqtls) {
      this.instance.eqtls(eqtls);
      return this;
    }
    
    public EqtlsPageDto.Builder page(PageMetadataDto page) {
      this.instance.page(page);
      return this;
    }
    
    public EqtlsPageDto.Builder selectedRowInResults(Boolean selectedRowInResults) {
      this.instance.selectedRowInResults(selectedRowInResults);
      return this;
    }
    
    /**
    * returns a built EqtlsPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public EqtlsPageDto build() {
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
  public static EqtlsPageDto.Builder builder() {
    return new EqtlsPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public EqtlsPageDto.Builder toBuilder() {
    EqtlsPageDto.Builder builder = new EqtlsPageDto.Builder();
    return builder.copyOf(this);
  }

}

