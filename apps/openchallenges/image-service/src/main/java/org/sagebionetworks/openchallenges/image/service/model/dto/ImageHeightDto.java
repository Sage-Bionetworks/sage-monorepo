package org.sagebionetworks.openchallenges.image.service.model.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The height of the image.
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ImageHeightDto {
  
  ORIGINAL("original"),
  
  _32PX("32px"),
  
  _100PX("100px"),
  
  _140PX("140px"),
  
  _250PX("250px"),
  
  _500PX("500px");

  private String value;

  ImageHeightDto(String value) {
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
  public static ImageHeightDto fromValue(String value) {
    for (ImageHeightDto b : ImageHeightDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

