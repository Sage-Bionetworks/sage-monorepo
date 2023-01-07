package org.sagebionetworks.challenge.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.*;
import java.util.Objects;
import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

/** A date range. */
@Schema(name = "DateRange", description = "A date range.")
@JsonTypeName("DateRange")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
// TODO Add x-java-class-annotations
public class DateRangeDto {

  @JsonProperty("start")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate start;

  @JsonProperty("end")
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate end;

  public DateRangeDto start(LocalDate start) {
    this.start = start;
    return this;
  }

  /**
   * The start date of the date range.
   *
   * @return start
   */
  @Valid
  @Schema(
      name = "start",
      example = "Sat Jul 17 00:00:00 UTC 2021",
      description = "The start date of the date range.",
      required = false)
  public LocalDate getStart() {
    return start;
  }

  public void setStart(LocalDate start) {
    this.start = start;
  }

  public DateRangeDto end(LocalDate end) {
    this.end = end;
    return this;
  }

  /**
   * The end date of the date range.
   *
   * @return end
   */
  @Valid
  @Schema(
      name = "end",
      example = "Sat Jul 17 00:00:00 UTC 2021",
      description = "The end date of the date range.",
      required = false)
  public LocalDate getEnd() {
    return end;
  }

  public void setEnd(LocalDate end) {
    this.end = end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DateRangeDto dateRange = (DateRangeDto) o;
    return Objects.equals(this.start, dateRange.start) && Objects.equals(this.end, dateRange.end);
  }

  @Override
  public int hashCode() {
    return Objects.hash(start, end);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DateRangeDto {\n");
    sb.append("    start: ").append(toIndentedString(start)).append("\n");
    sb.append("    end: ").append(toIndentedString(end)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
