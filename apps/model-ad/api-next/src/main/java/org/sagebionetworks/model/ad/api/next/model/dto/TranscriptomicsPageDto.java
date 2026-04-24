package org.sagebionetworks.model.ad.api.next.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.dto.PageMetadataDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsDto;
import org.springframework.lang.Nullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * A paginated response containing transcriptomics objects and pagination metadata.
 */

@Schema(name = "TranscriptomicsPage", description = "A paginated response containing transcriptomics objects and pagination metadata.")
@JsonTypeName("TranscriptomicsPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public class TranscriptomicsPageDto {

  @Valid
  private List<@Valid TranscriptomicsDto> transcriptomics = new ArrayList<>();

  private PageMetadataDto page;

  public TranscriptomicsPageDto() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public TranscriptomicsPageDto(List<@Valid TranscriptomicsDto> transcriptomics, PageMetadataDto page) {
    this.transcriptomics = transcriptomics;
    this.page = page;
  }

  public TranscriptomicsPageDto transcriptomics(List<@Valid TranscriptomicsDto> transcriptomics) {
    this.transcriptomics = transcriptomics;
    return this;
  }

  public TranscriptomicsPageDto addTranscriptomicsItem(TranscriptomicsDto transcriptomicsItem) {
    if (this.transcriptomics == null) {
      this.transcriptomics = new ArrayList<>();
    }
    this.transcriptomics.add(transcriptomicsItem);
    return this;
  }

  /**
   * The list of transcriptomics objects for the current page
   * @return transcriptomics
   */
  @NotNull @Valid 
  @Schema(name = "transcriptomics", description = "The list of transcriptomics objects for the current page", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("transcriptomics")
  public List<@Valid TranscriptomicsDto> getTranscriptomics() {
    return transcriptomics;
  }

  public void setTranscriptomics(List<@Valid TranscriptomicsDto> transcriptomics) {
    this.transcriptomics = transcriptomics;
  }

  public TranscriptomicsPageDto page(PageMetadataDto page) {
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
    TranscriptomicsPageDto transcriptomicsPage = (TranscriptomicsPageDto) o;
    return Objects.equals(this.transcriptomics, transcriptomicsPage.transcriptomics) &&
        Objects.equals(this.page, transcriptomicsPage.page);
  }

  @Override
  public int hashCode() {
    return Objects.hash(transcriptomics, page);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TranscriptomicsPageDto {\n");
    sb.append("    transcriptomics: ").append(toIndentedString(transcriptomics)).append("\n");
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

    private TranscriptomicsPageDto instance;

    public Builder() {
      this(new TranscriptomicsPageDto());
    }

    protected Builder(TranscriptomicsPageDto instance) {
      this.instance = instance;
    }

    protected Builder copyOf(TranscriptomicsPageDto value) { 
      this.instance.setTranscriptomics(value.transcriptomics);
      this.instance.setPage(value.page);
      return this;
    }

    public TranscriptomicsPageDto.Builder transcriptomics(List<TranscriptomicsDto> transcriptomics) {
      this.instance.transcriptomics(transcriptomics);
      return this;
    }
    
    public TranscriptomicsPageDto.Builder page(PageMetadataDto page) {
      this.instance.page(page);
      return this;
    }
    
    /**
    * returns a built TranscriptomicsPageDto instance.
    *
    * The builder is not reusable (NullPointerException)
    */
    public TranscriptomicsPageDto build() {
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
  public static TranscriptomicsPageDto.Builder builder() {
    return new TranscriptomicsPageDto.Builder();
  }

  /**
  * Create a builder with a shallow copy of this instance.
  */
  public TranscriptomicsPageDto.Builder toBuilder() {
    TranscriptomicsPageDto.Builder builder = new TranscriptomicsPageDto.Builder();
    return builder.copyOf(this);
  }

}

