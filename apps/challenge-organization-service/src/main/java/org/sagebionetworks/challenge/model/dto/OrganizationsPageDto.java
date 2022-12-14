package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/** A page of organizations */
@Schema(name = "OrganizationsPage", description = "A page of organizations")
@JsonTypeName("OrganizationsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@lombok.Builder
public class OrganizationsPageDto {

  @JsonProperty("paging")
  private PageMetadataPagingDto paging;

  @JsonProperty("totalResults")
  private Integer totalResults;

  @JsonProperty("organizations")
  @Valid
  private List<OrganizationDto> organizations = new ArrayList<>();

  public OrganizationsPageDto paging(PageMetadataPagingDto paging) {
    this.paging = paging;
    return this;
  }

  /**
   * Get paging
   *
   * @return paging
   */
  @NotNull
  @Valid
  @Schema(name = "paging", required = true)
  public PageMetadataPagingDto getPaging() {
    return paging;
  }

  public void setPaging(PageMetadataPagingDto paging) {
    this.paging = paging;
  }

  public OrganizationsPageDto totalResults(Integer totalResults) {
    this.totalResults = totalResults;
    return this;
  }

  /**
   * Total number of results in the result set
   *
   * @return totalResults
   */
  @NotNull
  @Schema(
      name = "totalResults",
      description = "Total number of results in the result set",
      required = true)
  public Integer getTotalResults() {
    return totalResults;
  }

  public void setTotalResults(Integer totalResults) {
    this.totalResults = totalResults;
  }

  public OrganizationsPageDto organizations(List<OrganizationDto> organizations) {
    this.organizations = organizations;
    return this;
  }

  public OrganizationsPageDto addOrganizationsItem(OrganizationDto organizationsItem) {
    this.organizations.add(organizationsItem);
    return this;
  }

  /**
   * A list of organizations
   *
   * @return organizations
   */
  @NotNull
  @Valid
  @Schema(name = "organizations", description = "A list of organizations", required = true)
  public List<OrganizationDto> getOrganizations() {
    return organizations;
  }

  public void setOrganizations(List<OrganizationDto> organizations) {
    this.organizations = organizations;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OrganizationsPageDto organizationsPage = (OrganizationsPageDto) o;
    return Objects.equals(this.paging, organizationsPage.paging)
        && Objects.equals(this.totalResults, organizationsPage.totalResults)
        && Objects.equals(this.organizations, organizationsPage.organizations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paging, totalResults, organizations);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OrganizationsPageDto {\n");
    sb.append("    paging: ").append(toIndentedString(paging)).append("\n");
    sb.append("    totalResults: ").append(toIndentedString(totalResults)).append("\n");
    sb.append("    organizations: ").append(toIndentedString(organizations)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
