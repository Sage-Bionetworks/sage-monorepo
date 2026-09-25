package org.sagebionetworks.model.ad.api.next.model.dto;

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
 * Which field the `items` values are matched against. - row: Match items against this view's row UID field. - parent: Match items against this view's parent ID field.  Absent matches the view's default id field, which is the legacy behavior. Exactly one space is matched, never both. 
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", comments = "Generator version: 7.14.0")
public enum ItemIdSpaceQueryDto {
  
  ROW("row"),
  
  PARENT("parent");

  private final String value;

  ItemIdSpaceQueryDto(String value) {
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
  public static ItemIdSpaceQueryDto fromValue(String value) {
    for (ItemIdSpaceQueryDto b : ItemIdSpaceQueryDto.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

