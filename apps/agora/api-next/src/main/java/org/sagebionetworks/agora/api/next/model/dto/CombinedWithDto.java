package org.sagebionetworks.agora.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A drug that a nomination is combined with as part of a combination therapy
 */

@Schema(name = "CombinedWith", description = "A drug that a nomination is combined with as part of a combination therapy")
@JsonTypeName("CombinedWith")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class CombinedWithDto {

  private String commonName = null;

  private String chemblId;

  public CombinedWithDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CombinedWithDto(String commonName, String chemblId) {
    this.commonName = commonName;
    this.chemblId = chemblId;
  }

  public CombinedWithDto commonName(String commonName) {
    this.commonName = commonName;
    return this;
  }

  /**
   * The common name of the drug this is combined with
   * @return commonName
   */
  @NotNull 
  @Schema(name = "common_name", example = "Irinotecan", description = "The common name of the drug this is combined with", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("common_name")
  public String getCommonName() {
    return commonName;
  }

  public void setCommonName(String commonName) {
    this.commonName = commonName;
  }

  public CombinedWithDto chemblId(String chemblId) {
    this.chemblId = chemblId;
    return this;
  }

  /**
   * The ChEMBL ID of the drug this is combined with
   * @return chemblId
   */
  @NotNull 
  @Schema(name = "chembl_id", example = "CHEMBL481", description = "The ChEMBL ID of the drug this is combined with", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("chembl_id")
  public String getChemblId() {
    return chemblId;
  }

  public void setChemblId(String chemblId) {
    this.chemblId = chemblId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CombinedWithDto combinedWith = (CombinedWithDto) o;
    return Objects.equals(this.commonName, combinedWith.commonName) &&
        Objects.equals(this.chemblId, combinedWith.chemblId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(commonName, chemblId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CombinedWithDto {\n");
    sb.append("    commonName: ").append(toIndentedString(commonName)).append("\n");
    sb.append("    chemblId: ").append(toIndentedString(chemblId)).append("\n");
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

    private CombinedWithDto instance;

    public Builder() {
      this(new CombinedWithDto());
    }

    protected Builder(CombinedWithDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(CombinedWithDto value) { 
      this.instance.setCommonName(value.commonName);
      this.instance.setChemblId(value.chemblId);
      return this;
    }

    public CombinedWithDto.Builder commonName(String commonName) {
      this.instance.commonName(commonName);
      return this;
    }
    
    public CombinedWithDto.Builder chemblId(String chemblId) {
      this.instance.chemblId(chemblId);
      return this;
    }
    
    /**
    * returns a built CombinedWithDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public CombinedWithDto build() {
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
  public static CombinedWithDto.Builder builder() {
    return new CombinedWithDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public CombinedWithDto.Builder toBuilder() {
    CombinedWithDto.Builder builder = new CombinedWithDto.Builder();
    return builder.copyOf(this);
  }

}

