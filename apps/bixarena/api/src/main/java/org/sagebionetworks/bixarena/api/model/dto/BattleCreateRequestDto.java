package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.UUID;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * The information used to create a new battle.
 */

@Schema(name = "BattleCreateRequest", description = "The information used to create a new battle.")
@JsonTypeName("BattleCreateRequest")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattleCreateRequestDto {

  private @Nullable String title;

  private UUID model1Id;

  private UUID model2Id;

  public BattleCreateRequestDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattleCreateRequestDto(UUID model1Id, UUID model2Id) {
    this.model1Id = model1Id;
    this.model2Id = model2Id;
  }

  public BattleCreateRequestDto title(@Nullable String title) {
    this.title = title;
    return this;
  }

  /**
   * Title of the battle.
   * @return title
   */
  
  @Schema(name = "title", example = "Gene Expression Analysis Comparison", description = "Title of the battle.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public @Nullable String getTitle() {
    return title;
  }

  public void setTitle(@Nullable String title) {
    this.title = title;
  }

  public BattleCreateRequestDto model1Id(UUID model1Id) {
    this.model1Id = model1Id;
    return this;
  }

  /**
   * UUID of an AI model.
   * @return model1Id
   */
  @NotNull @Valid 
  @Schema(name = "model1Id", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "UUID of an AI model.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model1Id")
  public UUID getModel1Id() {
    return model1Id;
  }

  public void setModel1Id(UUID model1Id) {
    this.model1Id = model1Id;
  }

  public BattleCreateRequestDto model2Id(UUID model2Id) {
    this.model2Id = model2Id;
    return this;
  }

  /**
   * UUID of an AI model.
   * @return model2Id
   */
  @NotNull @Valid 
  @Schema(name = "model2Id", example = "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d", description = "UUID of an AI model.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("model2Id")
  public UUID getModel2Id() {
    return model2Id;
  }

  public void setModel2Id(UUID model2Id) {
    this.model2Id = model2Id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattleCreateRequestDto battleCreateRequest = (BattleCreateRequestDto) o;
    return Objects.equals(this.title, battleCreateRequest.title) &&
        Objects.equals(this.model1Id, battleCreateRequest.model1Id) &&
        Objects.equals(this.model2Id, battleCreateRequest.model2Id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, model1Id, model2Id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattleCreateRequestDto {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    model1Id: ").append(toIndentedString(model1Id)).append("\n");
    sb.append("    model2Id: ").append(toIndentedString(model2Id)).append("\n");
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

    private BattleCreateRequestDto instance;

    public Builder() {
      this(new BattleCreateRequestDto());
    }

    protected Builder(BattleCreateRequestDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattleCreateRequestDto value) { 
      this.instance.setTitle(value.title);
      this.instance.setModel1Id(value.model1Id);
      this.instance.setModel2Id(value.model2Id);
      return this;
    }

    public BattleCreateRequestDto.Builder title(String title) {
      this.instance.title(title);
      return this;
    }
    
    public BattleCreateRequestDto.Builder model1Id(UUID model1Id) {
      this.instance.model1Id(model1Id);
      return this;
    }
    
    public BattleCreateRequestDto.Builder model2Id(UUID model2Id) {
      this.instance.model2Id(model2Id);
      return this;
    }
    
    /**
    * returns a built BattleCreateRequestDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattleCreateRequestDto build() {
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
  public static BattleCreateRequestDto.Builder builder() {
    return new BattleCreateRequestDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattleCreateRequestDto.Builder toBuilder() {
    BattleCreateRequestDto.Builder builder = new BattleCreateRequestDto.Builder();
    return builder.copyOf(this);
  }

}

