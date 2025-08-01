package org.sagebionetworks.openchallenges.image.service.model.dto;

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
 * The aspect ratio of the image (the height of the image must be specified).
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public enum ImageAspectRatioDto {
  
  ORIGINAL("original"),
  
  _16_9("16_9"),
  
  _1_1("1_1"),
  
  _3_2("3_2"),
  
  _2_3("2_3");

  private final String value;

  ImageAspectRatioDto(String value) {
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
  public static ImageAspectRatioDto fromValue(String value) {
    for (ImageAspectRatioDto b : ImageAspectRatioDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

