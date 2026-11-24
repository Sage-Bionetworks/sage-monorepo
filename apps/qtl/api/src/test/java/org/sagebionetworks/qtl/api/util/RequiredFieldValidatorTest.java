package org.sagebionetworks.qtl.api.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.qtl.api.exception.DataIntegrityException;

class RequiredFieldValidatorTest {

  private static final String RECORD_TYPE = "eQTL record";
  private static final String FIELD_NAME = "variant";

  private final RequiredFieldValidator validator = new RequiredFieldValidator(RECORD_TYPE);

  @Test
  @DisplayName("should return the value when the field is present")
  void shouldReturnTheValueWhenTheFieldIsPresent() {
    String variant = "rs2120461";

    assertThat(validator.require(variant, FIELD_NAME)).isSameAs(variant);
  }

  @Test
  @DisplayName("should report the field and record type when the field is missing")
  void shouldReportTheFieldAndRecordTypeWhenTheFieldIsMissing() {
    assertThatThrownBy(() -> validator.require(null, FIELD_NAME))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessage("Missing variant in eQTL record");
  }
}
