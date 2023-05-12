package org.sagebionetworks.openchallenges.image.service.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The aspect ratio of the image (the height of the image must be specified). */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ImageAspectRatioDto {
  ORIGINAL("original"),

  _16_9("16_9"),

  _1_1("1_1"),

  _3_2("3_2"),

  _2_3("2_3");

  private String value;

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
