package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.bixarena.api.model.dto.BattleDto;
import org.sagebionetworks.bixarena.api.model.dto.PageMetadataDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A page of battles.
 */

@Schema(name = "BattlePage", description = "A page of battles.")
@JsonTypeName("BattlePage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class BattlePageDto {

  @Valid
  private List<@Valid BattleDto> battles = new ArrayList<>();

  private PageMetadataDto page;

  public BattlePageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BattlePageDto(List<@Valid BattleDto> battles, PageMetadataDto page) {
    this.battles = battles;
    this.page = page;
  }

  public BattlePageDto battles(List<@Valid BattleDto> battles) {
    this.battles = battles;
    return this;
  }

  public BattlePageDto addBattlesItem(BattleDto battlesItem) {
    if (this.battles == null) {
      this.battles = new ArrayList<>();
    }
    this.battles.add(battlesItem);
    return this;
  }

  /**
   * List of battles in this page.
   * @return battles
   */
  @NotNull @Valid 
  @Schema(name = "battles", description = "List of battles in this page.", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("battles")
  public List<@Valid BattleDto> getBattles() {
    return battles;
  }

  public void setBattles(List<@Valid BattleDto> battles) {
    this.battles = battles;
  }

  public BattlePageDto page(PageMetadataDto page) {
    this.page = page;
    return this;
  }

  /**
   * Get page
   * @return page
   */
  @NotNull @Valid 
  @Schema(name = "page", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("page")
  public PageMetadataDto getPage() {
    return page;
  }

  public void setPage(PageMetadataDto page) {
    this.page = page;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BattlePageDto battlePage = (BattlePageDto) o;
    return Objects.equals(this.battles, battlePage.battles) &&
        Objects.equals(this.page, battlePage.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(battles, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BattlePageDto {\n");
    sb.append("    battles: ").append(toIndentedString(battles)).append("\n");
    sb.append("    page: ").append(toIndentedString(page)).append("\n");
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

    private BattlePageDto instance;

    public Builder() {
      this(new BattlePageDto());
    }

    protected Builder(BattlePageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(BattlePageDto value) { 
      this.instance.setBattles(value.battles);
      this.instance.setPage(value.page);
      return this;
    }

    public BattlePageDto.Builder battles(List<BattleDto> battles) {
      this.instance.battles(battles);
      return this;
    }
    
    public BattlePageDto.Builder page(PageMetadataDto page) {
      this.instance.page(page);
      return this;
    }
    
    /**
    * returns a built BattlePageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public BattlePageDto build() {
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
  public static BattlePageDto.Builder builder() {
    return new BattlePageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public BattlePageDto.Builder toBuilder() {
    BattlePageDto.Builder builder = new BattlePageDto.Builder();
    return builder.copyOf(this);
  }

}

