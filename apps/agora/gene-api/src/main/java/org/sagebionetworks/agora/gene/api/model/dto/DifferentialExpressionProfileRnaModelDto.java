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
 * The RNA model to apply for the differential expression profile.
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.12.0")
public enum DifferentialExpressionProfileRnaModelDto {
  
  AD_DIAGNOSIS_AOD_MALES_AND_FEMALES("ad_diagnosis_aod_males_and_females"),
  
  AD_DIAGNOSIS_MALES_AND_FEMALES("ad_diagnosis_males_and_females"),
  
  AD_DIAGNOSIS_SEX_FEMALES_ONLY("ad_diagnosis_sex_females_only"),
  
  AD_DIAGNOSIS_SEX_MALES_ONLY("ad_diagnosis_sex_males_only");

  private String value;

  DifferentialExpressionProfileRnaModelDto(String value) {
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
  public static DifferentialExpressionProfileRnaModelDto fromValue(String value) {
    for (DifferentialExpressionProfileRnaModelDto b : DifferentialExpressionProfileRnaModelDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

