package org.sagebionetworks.openchallenges.organization.service.model.rest.response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImageResponseTest {

  @Test
  public void ImageUrlGetterAndSetter_ShouldSetAndGetUrl_WhenCalledAfterImageUrlKeyPassed() {

    String imageUrl = "https://example.com/image.jpg";
    ImageResponse imageResponse = new ImageResponse();

    // Set the url
    imageResponse.setUrl(imageUrl);
    String actualUrl = imageResponse.getUrl();

    // Confirm that the same image url was actual that was set
    Assertions.assertEquals(imageUrl, actualUrl);
  }
}
