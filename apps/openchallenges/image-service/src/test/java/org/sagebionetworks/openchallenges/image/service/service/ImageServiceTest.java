package org.sagebionetworks.openchallenges.image.service.service;

import static org.assertj.core.api.Assertions.assertThat;
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

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

  @Mock private ImageServiceConfigData imageServiceConfigData;

  private Thumbor thumbor = Mockito.mock(Thumbor.class, Mockito.RETURNS_DEEP_STUBS);

  @InjectMocks private ImageService imageService;

  @Test
  void test() {
    String expectedUrl =
      "http://localhost:8082/img/ucBCtKeFXF3rPUsmUGmxcTRnoj0=/300x300/triforce.png";

    // given an image object key
    String image = "image.png";

    // when an image is requested from the image service
    when(thumbor.buildImage(image).resize(300, 300).toUrl()).thenReturn(expectedUrl);
    ImageDto actual = imageService.getImage(image);

    // then
    assertThat(actual.getUrl()).isEqualTo(expectedUrl);
  }
}
