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
 * A linked target
 */

@Schema(name = "LinkedTarget", description = "A linked target")
@JsonTypeName("LinkedTarget")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class LinkedTargetDto {

  private String ensemblGeneId;

  private String hgncSymbol;

  public LinkedTargetDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public LinkedTargetDto(String ensemblGeneId, String hgncSymbol) {
    this.ensemblGeneId = ensemblGeneId;
    this.hgncSymbol = hgncSymbol;
  }

  public LinkedTargetDto ensemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
    return this;
  }

  /**
   * The Ensembl gene ID of the linked target
   * @return ensemblGeneId
   */
  @NotNull 
  @Schema(name = "ensembl_gene_id", example = "ENSG00000139618", description = "The Ensembl gene ID of the linked target", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("ensembl_gene_id")
  public String getEnsemblGeneId() {
    return ensemblGeneId;
  }

  public void setEnsemblGeneId(String ensemblGeneId) {
    this.ensemblGeneId = ensemblGeneId;
  }

  public LinkedTargetDto hgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
    return this;
  }

  /**
   * The HGNC symbol of the linked target
   * @return hgncSymbol
   */
  @NotNull 
  @Schema(name = "hgnc_symbol", example = "BRCA1", description = "The HGNC symbol of the linked target", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("hgnc_symbol")
  public String getHgncSymbol() {
    return hgncSymbol;
  }

  public void setHgncSymbol(String hgncSymbol) {
    this.hgncSymbol = hgncSymbol;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LinkedTargetDto linkedTarget = (LinkedTargetDto) o;
    return Objects.equals(this.ensemblGeneId, linkedTarget.ensemblGeneId) &&
        Objects.equals(this.hgncSymbol, linkedTarget.hgncSymbol);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ensemblGeneId, hgncSymbol);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LinkedTargetDto {\n");
    sb.append("    ensemblGeneId: ").append(toIndentedString(ensemblGeneId)).append("\n");
    sb.append("    hgncSymbol: ").append(toIndentedString(hgncSymbol)).append("\n");
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

    private LinkedTargetDto instance;

    public Builder() {
      this(new LinkedTargetDto());
    }

    protected Builder(LinkedTargetDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(LinkedTargetDto value) { 
      this.instance.setEnsemblGeneId(value.ensemblGeneId);
      this.instance.setHgncSymbol(value.hgncSymbol);
      return this;
    }

    public LinkedTargetDto.Builder ensemblGeneId(String ensemblGeneId) {
      this.instance.ensemblGeneId(ensemblGeneId);
      return this;
    }
    
    public LinkedTargetDto.Builder hgncSymbol(String hgncSymbol) {
      this.instance.hgncSymbol(hgncSymbol);
      return this;
    }
    
    /**
    * returns a built LinkedTargetDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public LinkedTargetDto build() {
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
  public static LinkedTargetDto.Builder builder() {
    return new LinkedTargetDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public LinkedTargetDto.Builder toBuilder() {
    LinkedTargetDto.Builder builder = new LinkedTargetDto.Builder();
    return builder.copyOf(this);
  }

}

