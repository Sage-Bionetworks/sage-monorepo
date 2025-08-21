package org.sagebionetworks.agora.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.agora.api.model.dto.GCTGeneDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * List of GCTGene
 */

@Schema(name = "GCTGenesList", description = "List of GCTGene")
@JsonTypeName("GCTGenesList")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class GCTGenesListDto {

  @Valid
  private List<@Valid GCTGeneDto> items = new ArrayList<>();

  public GCTGenesListDto items(List<@Valid GCTGeneDto> items) {
    this.items = items;
    return this;
  }

  public GCTGenesListDto addItemsItem(GCTGeneDto itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * Get items
   * @return items
   */
  @Valid 
  @Schema(name = "items", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("items")
  public List<@Valid GCTGeneDto> getItems() {
    return items;
  }

  public void setItems(List<@Valid GCTGeneDto> items) {
    this.items = items;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GCTGenesListDto gcTGenesList = (GCTGenesListDto) o;
    return Objects.equals(this.items, gcTGenesList.items);
  }

  @Override
  public int hashCode() {
    return Objects.hash(items);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class GCTGenesListDto {\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
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

    private GCTGenesListDto instance;

    public Builder() {
      this(new GCTGenesListDto());
    }

    protected Builder(GCTGenesListDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(GCTGenesListDto value) { 
      this.instance.setItems(value.items);
      return this;
    }

    public GCTGenesListDto.Builder items(List<GCTGeneDto> items) {
      this.instance.items(items);
      return this;
    }
    
    /**
    * returns a built GCTGenesListDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public GCTGenesListDto build() {
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
  public static GCTGenesListDto.Builder builder() {
    return new GCTGenesListDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public GCTGenesListDto.Builder toBuilder() {
    GCTGenesListDto.Builder builder = new GCTGenesListDto.Builder();
    return builder.copyOf(this);
  }

}

