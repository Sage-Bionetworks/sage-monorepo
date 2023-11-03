package org.sagebionetworks.openchallenges.image.service.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ImageHeightNotSpecifiedExceptionTest {

  @Test
  public void ImageHeightNotSpecifiedException_ConstructorTypeShouldMatch() {
    // Define the exception detail
    String detail = "Image height is not specified";

    // Create an instance of ImageHeightNotSpecifiedException
    ImageHeightNotSpecifiedException exception = new ImageHeightNotSpecifiedException(detail);

    // Verify the exception details
    assertThat(ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED.getType()).isEqualTo(exception.getType());
  }

  @Test
  public void ImageHeightNotSpecifiedException_ConstructorTitle_Match() {
    // Define the exception detail
    String detail = "Image height is not specified";

    // Create an instance of ImageHeightNotSpecifiedException
    ImageHeightNotSpecifiedException exception = new ImageHeightNotSpecifiedException(detail);

    // Verify the exception details
    assertThat(ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED.getTitle())
        .isEqualTo(exception.getTitle());
  }

  @Test
  public void ImageHeightNotSpecifiedException_ConstructorStatusMatch() {
    // Define the exception detail
    String detail = "Image height is not specified";

    // Create an instance of ImageHeightNotSpecifiedException
    ImageHeightNotSpecifiedException exception = new ImageHeightNotSpecifiedException(detail);

    // Verify the exception details
    assertThat(ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED.getStatus())
        .isEqualTo(exception.getStatus());
  }

  @Test
  public void ImageHeightNotSpecifiedException_ConstructorDetailMatch() {
    // Define the exception detail
    String detail = "Image height is not specified";

    // Create an instance of ImageHeightNotSpecifiedException
    ImageHeightNotSpecifiedException exception = new ImageHeightNotSpecifiedException(detail);

    // Verify the exception details
    assertThat(exception.getDetail()).isEqualTo(detail);
  }
}
