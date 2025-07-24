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

  private static final Logger logger = LoggerFactory.getLogger(ImageService.class);

  private final ImageServiceConfigData imageServiceConfigData;

  private final Thumbor thumbor;

  public ImageService(ImageServiceConfigData imageServiceConfigData, Thumbor thumbor) {
    this.imageServiceConfigData = imageServiceConfigData;
    this.thumbor = thumbor;

    logger.debug("Thumbor host: {}", this.imageServiceConfigData.getThumborHost());
  }

  public ImageDto getImage(ImageQueryDto query) {
    String imageUrl = generateImageUrl(query);
    return ImageDto.builder().url(imageUrl).build();
  }

  private String generateImageUrl(ImageQueryDto query) {
    if (query.getAspectRatio() != null && query.getHeight() == null) {
      throw new ImageHeightNotSpecifiedException(
        "Image height must also be specified when specifying the aspect ratio."
      );
    }

    logger.info("Requesting an image url for the objectId: {}", query.getObjectKey());

    ThumborUrlBuilder builder = thumbor.buildImage(query.getObjectKey());
    Integer height = getImageHeightInPx(query.getHeight());

    if (height != null) {
      Integer width = getImageWidthInPixel(height, query.getAspectRatio());
      builder = builder.resize(width, height);
    }

    return builder.toUrl();
  }

  private Integer getImageHeightInPx(ImageHeightDto height) {
    // we can't use switch here because height may be null
    if (height == ImageHeightDto.ORIGINAL) {
      return null;
    } else if (height == ImageHeightDto._32PX) {
      return 32;
    } else if (height == ImageHeightDto._100PX) {
      return 100;
    } else if (height == ImageHeightDto._140PX) {
      return 140;
    } else if (height == ImageHeightDto._250PX) {
      return 250;
    } else if (height == ImageHeightDto._500PX) {
      return 500;
    }
    return null;
  }

  private Integer getImageWidthInPixel(Integer height, ImageAspectRatioDto aspectRatio) {
    // we can't use switch here because height may be null
    if (aspectRatio == ImageAspectRatioDto.ORIGINAL) {
      return 0; // Thumbor will use the original width
    } else if (aspectRatio == ImageAspectRatioDto._16_9) {
      return Math.round((height * 16f) / 9);
    } else if (aspectRatio == ImageAspectRatioDto._1_1) {
      return height;
    } else if (aspectRatio == ImageAspectRatioDto._3_2) {
      return Math.round((height * 3f) / 2);
    } else if (aspectRatio == ImageAspectRatioDto._2_3) {
      return Math.round((height * 2f) / 3);
    }
    return 0; // Thumbor will use the original width
  }
}
