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

/** A page of users */
@Schema(name = "UsersPage", description = "A page of users")
@JsonTypeName("UsersPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@lombok.Builder
public class UsersPageDto {

  @JsonProperty("paging")
  private PageMetadataPagingDto paging;

  @JsonProperty("totalResults")
  private Integer totalResults;

  @JsonProperty("users")
  @Valid
  private List<UserDto> users = new ArrayList<>();

  public UsersPageDto paging(PageMetadataPagingDto paging) {
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

  public UsersPageDto totalResults(Integer totalResults) {
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
    required = true
  )
  public Integer getTotalResults() {
    return totalResults;
  }

  public void setTotalResults(Integer totalResults) {
    this.totalResults = totalResults;
  }

  public UsersPageDto users(List<UserDto> users) {
    this.users = users;
    return this;
  }

  public UsersPageDto addUsersItem(UserDto usersItem) {
    this.users.add(usersItem);
    return this;
  }

  /**
   * A list of users
   *
   * @return users
   */
  @NotNull
  @Valid
  @Schema(name = "users", description = "A list of users", required = true)
  public List<UserDto> getUsers() {
    return users;
  }

  public void setUsers(List<UserDto> users) {
    this.users = users;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UsersPageDto usersPage = (UsersPageDto) o;
    return (
      Objects.equals(this.paging, usersPage.paging) &&
      Objects.equals(this.totalResults, usersPage.totalResults) &&
      Objects.equals(this.users, usersPage.users)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(paging, totalResults, users);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UsersPageDto {\n");
    sb.append("    paging: ").append(toIndentedString(paging)).append("\n");
    sb.append("    totalResults: ").append(toIndentedString(totalResults)).append("\n");
    sb.append("    users: ").append(toIndentedString(users)).append("\n");
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
