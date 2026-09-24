package org.sagebionetworks.model.ad.api.next.model.dto;

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
 * A noun used to label a set of records in the comparison tool UI, in both its singular and plural forms. 
 */

@Schema(name = "ComparisonToolNoun", description = "A noun used to label a set of records in the comparison tool UI, in both its singular and plural forms. ")
@JsonTypeName("ComparisonToolNoun")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class ComparisonToolNounDto {

  private String singular;

  private String plural;

  public ComparisonToolNounDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public ComparisonToolNounDto(String singular, String plural) {
    this.singular = singular;
    this.plural = plural;
  }

  public ComparisonToolNounDto singular(String singular) {
    this.singular = singular;
    return this;
  }

  /**
   * The singular form of the noun.
   * @return singular
   */
  @NotNull 
  @Schema(name = "singular", example = "protein", description = "The singular form of the noun.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("singular")
  public String getSingular() {
    return singular;
  }

  public void setSingular(String singular) {
    this.singular = singular;
  }

  public ComparisonToolNounDto plural(String plural) {
    this.plural = plural;
    return this;
  }

  /**
   * The plural form of the noun.
   * @return plural
   */
  @NotNull 
  @Schema(name = "plural", example = "proteins", description = "The plural form of the noun.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("plural")
  public String getPlural() {
    return plural;
  }

  public void setPlural(String plural) {
    this.plural = plural;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ComparisonToolNounDto comparisonToolNoun = (ComparisonToolNounDto) o;
    return Objects.equals(this.singular, comparisonToolNoun.singular) &&
        Objects.equals(this.plural, comparisonToolNoun.plural);
  }

  @Override
  public int hashCode() {
    return Objects.hash(singular, plural);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ComparisonToolNounDto {\n");
    sb.append("    singular: ").append(toIndentedString(singular)).append("\n");
    sb.append("    plural: ").append(toIndentedString(plural)).append("\n");
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

    private ComparisonToolNounDto instance;

    public Builder() {
      this(new ComparisonToolNounDto());
    }

    protected Builder(ComparisonToolNounDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(ComparisonToolNounDto value) { 
      this.instance.setSingular(value.singular);
      this.instance.setPlural(value.plural);
      return this;
    }

    public ComparisonToolNounDto.Builder singular(String singular) {
      this.instance.singular(singular);
      return this;
    }
    
    public ComparisonToolNounDto.Builder plural(String plural) {
      this.instance.plural(plural);
      return this;
    }
    
    /**
    * returns a built ComparisonToolNounDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public ComparisonToolNounDto build() {
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
  public static ComparisonToolNounDto.Builder builder() {
    return new ComparisonToolNounDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public ComparisonToolNounDto.Builder toBuilder() {
    ComparisonToolNounDto.Builder builder = new ComparisonToolNounDto.Builder();
    return builder.copyOf(this);
  }

}

