package org.sagebionetworks.openchallenges.challenge.service.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** A challenge */
@Schema(name = "ChallengeJsonLd", description = "A challenge")
@JsonTypeName("ChallengeJsonLd")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class ChallengeJsonLdDto {

  @JsonProperty("@context")
  private String atContext = "foo";

  @JsonProperty("@id")
  private String atId = "foo";

  @JsonProperty("@type")
  private String atType = "foo";

  public ChallengeJsonLdDto atContext(String atContext) {
    this.atContext = atContext;
    return this;
  }

  /**
   * Get atContext
   *
   * @return atContext
   */
  @Schema(name = "@context", required = false)
  public String getAtContext() {
    return atContext;
  }

  public void setAtContext(String atContext) {
    this.atContext = atContext;
  }

  public ChallengeJsonLdDto atId(String atId) {
    this.atId = atId;
    return this;
  }

  /**
   * Get atId
   *
   * @return atId
   */
  @Schema(name = "@id", required = false)
  public String getAtId() {
    return atId;
  }

  public void setAtId(String atId) {
    this.atId = atId;
  }

  public ChallengeJsonLdDto atType(String atType) {
    this.atType = atType;
    return this;
  }

  /**
   * Get atType
   *
   * @return atType
   */
  @Schema(name = "@type", required = false)
  public String getAtType() {
    return atType;
  }

  public void setAtType(String atType) {
    this.atType = atType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChallengeJsonLdDto challengeJsonLd = (ChallengeJsonLdDto) o;
    return Objects.equals(this.atContext, challengeJsonLd.atContext)
        && Objects.equals(this.atId, challengeJsonLd.atId)
        && Objects.equals(this.atType, challengeJsonLd.atType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(atContext, atId, atType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ChallengeJsonLdDto {\n");
    sb.append("    atContext: ").append(toIndentedString(atContext)).append("\n");
    sb.append("    atId: ").append(toIndentedString(atId)).append("\n");
    sb.append("    atType: ").append(toIndentedString(atType)).append("\n");
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
