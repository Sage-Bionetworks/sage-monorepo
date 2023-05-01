package org.sagebionetworks.openchallenges.image.service.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ImageServiceTest {

  @Autowired private ImageService imageService;

  @Test
  void givenImage_ImageDto_MatchURL() {

    // given an image
    String givenimg = "triforce.png";

    // return the epected URL using Mocked Thumbor instance
    String expectedImageUrl =
        "http://localhost:8082/img/ucBCtKeFXF3rPUsmUGmxcTRnoj0=/300x300/triforce.png";

    // assert that URL is as expected when ImageDto is run --PASS
    ImageDto result = imageService.getImage(givenimg);
    assertThat(result.getUrl()).isEqualTo(expectedImageUrl);
  }
}
