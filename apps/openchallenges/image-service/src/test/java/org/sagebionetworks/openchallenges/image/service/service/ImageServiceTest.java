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
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageQueryDto;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

  @Mock private ImageServiceConfigData imageServiceConfigData;

  private Thumbor thumbor = Mockito.mock(Thumbor.class, Mockito.RETURNS_DEEP_STUBS);

  @InjectMocks private ImageService imageService;

  @Test
  void getImage_ShouldReturnImage_WhenObjectKeyIsPassed() {
    String expectedUrl =
        "http://localhost:8082/img/ucBCtKeFXF3rPUsmUGmxcTRnoj0=/300x300/triforce.png";

    // given
    ImageQueryDto query = new ImageQueryDto();
    query.setObjectKey("image.png");

    // when an image is requested from the image service
    when(thumbor.buildImage(query.getObjectKey()).resize(300, 300).toUrl()).thenReturn(expectedUrl);
    ImageDto actual = imageService.getImage(query);

    // then
    assertThat(actual.getUrl()).isEqualTo(expectedUrl);
  }
}
