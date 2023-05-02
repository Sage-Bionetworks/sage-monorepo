package org.sagebionetworks.openchallenges.image.service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.squareup.pollexor.Thumbor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.app.config.data.ImageServiceConfigData;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

  private static final Logger LOG = LoggerFactory.getLogger(ImageServiceTest.class);

  @Mock private ImageServiceConfigData imageServiceConfigData;

  private Thumbor thumbor = Mockito.mock(Thumbor.class, Mockito.RETURNS_DEEP_STUBS);

  @InjectMocks private ImageService imageService;

  // @BeforeEach
  // void setUp() {
  //   when(imageServiceConfigData.getThumborHost()).thenReturn("thumbor.example.com");
  //   when(imageServiceConfigData.getThumborSecurityKey()).thenReturn("secret");

  //   LOG.info("Thumbor host: {}", thumbor.getHost());

  //   imageService = new ImageService(imageServiceConfigData, thumbor);
  // }

  @Test
  void testGetImage() {
    String image = "image.png";
    String expectedUrl = "https://thumbor.example.com/image.png?resize=300x300";

    LOG.info("thumbor: {}", thumbor.getHost());

    // when(imageServiceConfigData.getThumborHost()).thenReturn("thumbor.example.com");
    // when(imageServiceConfigData.getThumborSecurityKey()).thenReturn("secret");
    // when(thumbor.buildImage(image)).thenReturn(thumbor.buildImage(image));
    when(thumbor.buildImage(image).resize(300, 300).toUrl()).thenReturn(expectedUrl);
    // when(thumbor.resize(300, 300)).thenReturn(thumbor);
    // when(thumbor.toUrl()).thenReturn(expectedUrl);

    ImageDto actual = imageService.getImage(image);

    assertEquals(expectedUrl, actual.getUrl());
  }

  // @Test
  // void givenImage_ImageDto_MatchURL() {

  //   // given an image
  //   String givenimg = "triforce.png";

  //   // return the epected URL using Mocked Thumbor instance
  //   String expectedImageUrl =
  //       "http://localhost:8082/img/ucBCtKeFXF3rPUsmUGmxcTRnoj0=/300x300/triforce.png";

  //   // assert that URL is as expected when ImageDto is run --PASS
  //   ImageDto result = imageService.getImage(givenimg);
  //   assertThat(result.getUrl()).isEqualTo(expectedImageUrl);
  // }
}
