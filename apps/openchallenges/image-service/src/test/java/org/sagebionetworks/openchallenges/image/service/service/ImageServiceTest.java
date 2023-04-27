package org.sagebionetworks.openchallenges.image.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import com.squareup.pollexor.Thumbor;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.app.config.data.ImageServiceConfigData;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ImageServiceTest {

  @Autowired private ImageService imageService;

  @MockBean private Logger LOG;

  @MockBean private Thumbor thumbor;

  @MockBean private ImageServiceConfigData imageServiceConfigData;

  @Value("${example.expectedURL}")
  private String expectedURL =
      "http://localhost:8889/ucBCtKeFXF3rPUsmUGmxcTRnoj0=/300x300/triforce.png";

  @Test
  void givenImage_ImageDto_MatchURL() {

    // given an image
    String givenimg = "triforce.png";

    // return the epected URL using Mocked Thumbor instance
    String expectedImageUrl =
        "http://localhost:8889/ucBCtKeFXF3rPUsmUGmxcTRnoj0=/300x300/triforce.png";
    when(thumbor.buildImage(anyString())).thenReturn(thumbor);
    when(thumbor.resize(isA(int.class), isA(int.class))).thenReturn(thumbor);
    when(thumbor.toUrl()).thenReturn(expectedImageUrl);

    // assert that URL is as expected when ImageDto is run --PASS
    ImageDto result = imageService.getImage(givenimg);
    assertThat(result.getUrl()).isEqualTo(expectedURL);
  }
}
