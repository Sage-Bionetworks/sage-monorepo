package org.sagebionetworks.qtl.api.model.dto;

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
 * A location on a chromosome
 */

@Schema(name = "GenomicLocation", description = "A location on a chromosome")
@JsonTypeName("GenomicLocation")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GenomicLocationDto {

  private String chr;

  private Integer start;

  private Integer end;

  public GenomicLocationDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public GenomicLocationDto(String chr, Integer start, Integer end) {
    this.chr = chr;
    this.start = start;
    this.end = end;
  }

  public GenomicLocationDto chr(String chr) {
    this.chr = chr;
    return this;
  }

  /**
   * Chromosome name
   * @return chr
   */
  @NotNull 
  @Schema(name = "chr", example = "5", description = "Chromosome name", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("chr")
  public String getChr() {
    return chr;
  }

  public void setChr(String chr) {
    this.chr = chr;
  }

  public GenomicLocationDto start(Integer start) {
    this.start = start;
    return this;
  }

  /**
   * Start position
   * @return start
   */
  @NotNull 
  @Schema(name = "start", example = "118488207", description = "Start position", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("start")
  public Integer getStart() {
    return start;
  }

  public void setStart(Integer start) {
    this.start = start;
  }

  public GenomicLocationDto end(Integer end) {
    this.end = end;
    return this;
  }

  /**
   * End position
   * @return end
   */
  @NotNull 
  @Schema(name = "end", example = "118488208", description = "End position", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("end")
  public Integer getEnd() {
    return end;
  }

  public void setEnd(Integer end) {
    this.end = end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GenomicLocationDto genomicLocation = (GenomicLocationDto) o;
    return Objects.equals(this.chr, genomicLocation.chr) &&
        Objects.equals(this.start, genomicLocation.start) &&
        Objects.equals(this.end, genomicLocation.end);
  }

  @Override
  public int hashCode() {
    return Objects.hash(chr, start, end);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GenomicLocationDto {\n");
    sb.append("    chr: ").append(toIndentedString(chr)).append("\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
    sb.append("    end: ").append(toIndentedString(end)).append("\n");
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

    private GenomicLocationDto instance;

    public Builder() {
      this(new GenomicLocationDto());
    }

    protected Builder(GenomicLocationDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GenomicLocationDto value) { 
      this.instance.setChr(value.chr);
      this.instance.setStart(value.start);
      this.instance.setEnd(value.end);
      return this;
    }

    public GenomicLocationDto.Builder chr(String chr) {
      this.instance.chr(chr);
      return this;
    }
    
    public GenomicLocationDto.Builder start(Integer start) {
      this.instance.start(start);
      return this;
    }
    
    public GenomicLocationDto.Builder end(Integer end) {
      this.instance.end(end);
      return this;
    }
    
    /**
    * returns a built GenomicLocationDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GenomicLocationDto build() {
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
  public static GenomicLocationDto.Builder builder() {
    return new GenomicLocationDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GenomicLocationDto.Builder toBuilder() {
    GenomicLocationDto.Builder builder = new GenomicLocationDto.Builder();
    return builder.copyOf(this);
  }

}

