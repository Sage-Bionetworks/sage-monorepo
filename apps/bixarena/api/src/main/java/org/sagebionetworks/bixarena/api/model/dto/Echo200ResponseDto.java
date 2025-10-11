package org.sagebionetworks.bixarena.api.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.lang.Nullable;

/**
 * Echo200ResponseDto
 */

@JsonTypeName("echo_200_response")
@Generated(
  value = "org.openapitools.codegen.languages.SpringCodegen",
  comments = "Generator version: 7.14.0"
)
public class Echo200ResponseDto {

  private @Nullable String sub;

  @Valid
  private List<String> roles = new ArrayList<>();

  public Echo200ResponseDto sub(@Nullable String sub) {
    this.sub = sub;
    return this;
  }

  /**
   * Get sub
   * @return sub
   */

  @Schema(name = "sub", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("sub")
  public @Nullable String getSub() {
    return sub;
  }

  public void setSub(@Nullable String sub) {
    this.sub = sub;
  }

  public Echo200ResponseDto roles(List<String> roles) {
    this.roles = roles;
    return this;
  }

  public Echo200ResponseDto addRolesItem(String rolesItem) {
    if (this.roles == null) {
      this.roles = new ArrayList<>();
    }
    this.roles.add(rolesItem);
    return this;
  }

  /**
   * Get roles
   * @return roles
   */

  @Schema(name = "roles", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("roles")
  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Echo200ResponseDto echo200Response = (Echo200ResponseDto) o;
    return (
      Objects.equals(this.sub, echo200Response.sub) &&
      Objects.equals(this.roles, echo200Response.roles)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(sub, roles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Echo200ResponseDto {\n");
    sb.append("    sub: ").append(toIndentedString(sub)).append("\n");
    sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
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

    private Echo200ResponseDto instance;

    public Builder() {
      this(new Echo200ResponseDto());
    }

    protected Builder(Echo200ResponseDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(Echo200ResponseDto value) {
      this.instance.setSub(value.sub);
      this.instance.setRoles(value.roles);
      return this;
    }

    public Echo200ResponseDto.Builder sub(String sub) {
      this.instance.sub(sub);
      return this;
    }

    public Echo200ResponseDto.Builder roles(List<String> roles) {
      this.instance.roles(roles);
      return this;
    }

    /**
     * returns a built Echo200ResponseDto instance.
     *
     * The builder is not reusable (NullPointerException)
     */
    public Echo200ResponseDto build() {
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
  public static Echo200ResponseDto.Builder builder() {
    return new Echo200ResponseDto.Builder();
  }

  /**
   * Create a builder with a shallow copy of this instance.
   */
  public Echo200ResponseDto.Builder toBuilder() {
    Echo200ResponseDto.Builder builder = new Echo200ResponseDto.Builder();
    return builder.copyOf(this);
  }
}
