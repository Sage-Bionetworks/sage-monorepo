package org.sagebionetworks.openchallenges.image.service.exception;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.image.service.exception.ImageHeightNotSpecifiedException;
import org.sagebionetworks.openchallenges.image.service.exception.SimpleChallengeGlobalException;
import org.sagebionetworks.openchallenges.image.service.exception.ErrorConstants;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;


public class ImageHeightNotSpecifiedExceptionTest {

  @Test
  public void ConstructorTypeShouldMatch() {
    // Define the exception detail
    String detail = "Image height is not specified";

    // Create an instance of ImageHeightNotSpecifiedException
    ImageHeightNotSpecifiedException exception = new ImageHeightNotSpecifiedException(detail);

    // Verify the exception details
    assertThat(ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED.getType()).isEqualTo(exception.getType());
  }
  @Test
  public void ConstructorTitle_Match() {
    // Define the exception detail
    String detail = "Image height is not specified";

    // Create an instance of ImageHeightNotSpecifiedException
    ImageHeightNotSpecifiedException exception = new ImageHeightNotSpecifiedException(detail);

    // Verify the exception details
    assertThat(ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED.getTitle()).isEqualTo(exception.getTitle());
  }
  @Test
  public void ConstructorStatusMatch() {
    // Define the exception detail
    String detail = "Image height is not specified";

    // Create an instance of ImageHeightNotSpecifiedException
    ImageHeightNotSpecifiedException exception = new ImageHeightNotSpecifiedException(detail);

    // Verify the exception details
    assertThat(ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED.getStatus()).isEqualTo(exception.getStatus());
  }
  @Test
  public void ConstructorDetailMatch() {
    // Define the exception detail
    String detail = "Image height is not specified";

    // Create an instance of ImageHeightNotSpecifiedException
    ImageHeightNotSpecifiedException exception = new ImageHeightNotSpecifiedException(detail);

    // Verify the exception details
    assertThat(exception.getDetail()).isEqualTo(detail);
  }
}
