package org.sagebionetworks.bixarena.api.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Terminal outcome of a categorization run. - `matched`: the classifier picked at least one category. - `abstained`: the classifier ran successfully but declared no category fits.   The row is persisted for audit; the effective categorization is not   auto-promoted.  - `failed`: the classifier could not run (AI service or LLM error). The row   is persisted so admins can identify and retry; the effective categorization   is not auto-promoted.
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public enum CategorizationStatusDto {
  
  MATCHED("matched"),
  
  ABSTAINED("abstained"),
  
  FAILED("failed");

  private final String value;

  CategorizationStatusDto(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static CategorizationStatusDto fromValue(String value) {
    for (CategorizationStatusDto b : CategorizationStatusDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

