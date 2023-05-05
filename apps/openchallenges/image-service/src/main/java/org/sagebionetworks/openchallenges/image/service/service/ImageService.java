package org.sagebionetworks.openchallenges.image.service.service;

import com.squareup.pollexor.Thumbor;
import com.squareup.pollexor.ThumborUrlBuilder;
import org.sagebionetworks.openchallenges.app.config.data.ImageServiceConfigData;
import org.sagebionetworks.openchallenges.image.service.exception.ImageHeightNotSpecifiedException;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageAspectRatioDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageHeightDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageQueryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

  private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);

  private final ImageServiceConfigData imageServiceConfigData;

  private final Thumbor thumbor;

  public ImageService(ImageServiceConfigData imageServiceConfigData, Thumbor thumbor) {
    this.imageServiceConfigData = imageServiceConfigData;
    this.thumbor = thumbor;

    LOG.debug("Thumbor host: {}", this.imageServiceConfigData.getThumborHost());
  }

  public ImageDto getImage(ImageQueryDto query) {
    String imageUrl = generateImageUrl(query);
    return ImageDto.builder().url(imageUrl).build();
  }

  private String generateImageUrl(ImageQueryDto query) {
    if (query.getAspectRatio() != null && query.getHeight() == null) {
      throw new ImageHeightNotSpecifiedException(
          "Specifying the aspect ratio requires to specify the image height too.");
    }

    ThumborUrlBuilder builder = thumbor.buildImage(query.getObjectKey());
    Integer height = getImageHeightInPx(query.getHeight());

    if (height != null) {
      Integer width = getImageWidthInPixel(height, query.getAspectRatio());
      builder = builder.resize(width, height);
    }

    return builder.toUrl();
  }

  private Integer getImageHeightInPx(ImageHeightDto height) {
    switch (height) {
      case ORIGINAL:
        return null;
      case _100PX:
        return 100;
      case _250PX:
        return 250;
      case _500PX:
        return 500;
      default:
        return null;
    }
  }

  private Integer getImageWidthInPixel(Integer height, ImageAspectRatioDto aspectRatio) {
    switch (aspectRatio) {
      case ORIGINAL:
        return 0; // Thumbor will use the original width
      case _16_9:
        return Math.round(height * 16 / 9);
      case _1_1:
        return height;
      case _3_2:
        return Math.round(height * 3 / 2);
      case _2_3:
        return Math.round(height * 2 / 3);
      default:
        return 0; // Thumbor will use the original width
    }
  }
}
