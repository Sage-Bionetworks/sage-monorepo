package org.sagebionetworks.openchallenges.image.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.app.config.data.ImageServiceConfigData;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.springframework.boot.test.context.SpringBootTest;

import com.squareup.pollexor.Thumbor;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {
  @Mock
  private ImageServiceConfigData imageServiceConfigData;

  @Mock
  private Thumbor thumbor;

  // @InjectMocks
  private ImageService imageService;

  @BeforeEach
  void setUp() {
    imageService = new ImageService(imageServiceConfigData);
  }

  @Test
  void testGetImage() {
    String image = "image.png";
    String expectedUrl = "https://thumbor.example.com/image.png?resize=300x300";

    when(imageServiceConfigData.getThumborHost()).thenReturn("thumbor.example.com");
    when(imageServiceConfigData.getThumborSecurityKey()).thenReturn("secret");
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
