package org.sagebionetworks.openchallenges.image.service.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.*;
import javax.annotation.Generated;
import javax.validation.constraints.*;

/** The image size option. */
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public enum ImageSizeOptionDto {
  ORIGINAL("original"),

  _100PX("100px"),

  _250PX("250px"),

  _500PX("500px");

  private String value;

  ImageSizeOptionDto(String value) {
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
  public static ImageSizeOptionDto fromValue(String value) {
    for (ImageSizeOptionDto b : ImageSizeOptionDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    return null;
  }
}
