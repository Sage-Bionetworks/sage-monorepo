package org.sagebionetworks.agora.gene.api.model.dto;

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
 * The protein profiling method to apply for the differential expression profile.
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public enum ProteinDifferentialExpressionProfileProfilingMethodDto {
  
  LFQ("lfq"),
  
  SMR("smr"),
  
  TMT("tmt");

  private String value;

  ProteinDifferentialExpressionProfileProfilingMethodDto(String value) {
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
  public static ProteinDifferentialExpressionProfileProfilingMethodDto fromValue(String value) {
    for (ProteinDifferentialExpressionProfileProfilingMethodDto b : ProteinDifferentialExpressionProfileProfilingMethodDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

