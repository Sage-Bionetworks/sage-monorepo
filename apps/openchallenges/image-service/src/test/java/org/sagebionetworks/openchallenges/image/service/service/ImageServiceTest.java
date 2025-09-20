package org.sagebionetworks.openchallenges.image.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.squareup.pollexor.Thumbor;
import com.squareup.pollexor.ThumborUrlBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.sagebionetworks.openchallenges.image.service.configuration.AppProperties;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageQueryDto;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

  @Mock
  private Thumbor thumbor;

  @Mock private AppProperties appProperties;

  @InjectMocks private ImageService imageService;

  @Test
  void GetImage_ShouldReturnImage_WhenObjectKeyIsPassed() {
    String expectedUrl = "http://localhost:8082/img/S2_Nh1GysneL6qVEGuBdz5NK-wQ=/image.png";

    // given
    ImageQueryDto query = new ImageQueryDto();
    query.setObjectKey("image.png");
    AppProperties.ThumborProperties thumborProps = new AppProperties.ThumborProperties(
      "http://localhost:8000/img/",
      "changeme"
    );
    when(appProperties.thumbor()).thenReturn(thumborProps);
    when(appProperties.placeholder()).thenReturn(new AppProperties.PlaceholderProperties(false, null));

    // when an image is requested from the image service
  ThumborUrlBuilder builder = mock(ThumborUrlBuilder.class);
  when(thumbor.buildImage(query.getObjectKey())).thenReturn(builder);
  // Only stub toUrl since resize may be skipped when height null
  when(builder.toUrl()).thenReturn(expectedUrl);
    ImageDto actual = imageService.getImage(query);

    // then
    assertThat(actual.getUrl()).isEqualTo(expectedUrl);
  }

  @Test
  void GetImage_ShouldReturnDirectPlaceholder_WhenPlaceholderEnabled() {
    // given
    ImageQueryDto query = new ImageQueryDto();
    query.setObjectKey("image.png");
    // request 100px height, square aspect ratio implicitly (null -> width fallback logic)
    query.setHeight(org.sagebionetworks.openchallenges.image.service.model.dto.ImageHeightDto._100PX);

    when(appProperties.thumbor()).thenReturn(new AppProperties.ThumborProperties(
      "http://localhost:8000/img/",
      "changeme"
    ));
    when(appProperties.placeholder()).thenReturn(new AppProperties.PlaceholderProperties(
      true,
      "https://images.placeholders.dev/{width}x{height}"
    ));

    // when
    ImageDto actual = imageService.getImage(query);

    // then
    assertThat(actual.getUrl()).isEqualTo("https://images.placeholders.dev/100x100");
  }
}
