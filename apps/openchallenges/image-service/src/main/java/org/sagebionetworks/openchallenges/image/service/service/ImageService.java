package org.sagebionetworks.openchallenges.image.service.service;

import com.squareup.pollexor.Thumbor;
import com.squareup.pollexor.ThumborUrlBuilder;
import org.sagebionetworks.openchallenges.app.config.data.ImageServiceConfigData;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageQueryDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageSizeOptionDto;
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
    ThumborUrlBuilder builder = thumbor.buildImage(query.getObjectKey());
    LOG.info("size: {}", query.getSize());
    if (query.getSize() != null) {
      // May receive width OR height
      ImageSizeOptionDto width = query.getSize().getWidth();
      ImageSizeOptionDto height = query.getSize().getHeight();
      if (width != null && width != ImageSizeOptionDto.ORIGINAL) {
        builder = builder.resize(getImageSizeOptionInPx(width), 0);
      } else if (height != null && height != ImageSizeOptionDto.ORIGINAL) {
        builder = builder.resize(0, getImageSizeOptionInPx(height));
      }
    }
    return builder.toUrl();
  }

  private int getImageSizeOptionInPx(ImageSizeOptionDto option) {
    switch (option) {
      case ORIGINAL:
        return 0;
      case _100PX:
        return 100;
      case _250PX:
        return 250;
      case _500PX:
        return 500;
      default:
        return 0;
    }
  }
}
