package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The response returned after the creation of the user */
@Schema(
  name = "UserCreateResponse",
  description = "The response returned after the creation of the user"
)
@JsonTypeName("UserCreateResponse")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@lombok.Builder
public class UserCreateResponseDto {

  @JsonProperty("id")
  private Long id;

  public UserCreateResponseDto id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * The unique identifier of an account
   *
   * @return id
   */
  @NotNull
  @Schema(
    name = "id",
    example = "1",
    description = "The unique identifier of an account",
    required = true
  )
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserCreateResponseDto userCreateResponse = (UserCreateResponseDto) o;
    return Objects.equals(this.id, userCreateResponse.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserCreateResponseDto {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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
