package org.sagebionetworks.openchallenges.image.service.service;

import com.squareup.pollexor.Thumbor;
import org.sagebionetworks.openchallenges.app.config.data.ImageServiceConfigData;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

  private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);

  private final ImageServiceConfigData imageServiceConfigData;

  private final Thumbor thumbor;

  public ImageService(ImageServiceConfigData imageServiceConfigData) {
    this.imageServiceConfigData = imageServiceConfigData;

    LOG.info("Thumbor host: {}", this.imageServiceConfigData.getThumborHost());
    this.thumbor =
        Thumbor.create(
            this.imageServiceConfigData.getThumborHost(),
            this.imageServiceConfigData.getThumborSecurityKey());
  }

  public ImageDto getImage(String image) {
    String imageUrl = thumbor.buildImage(image).resize(300, 300).toUrl();
    return ImageDto.builder().url(imageUrl).build();
  }
}
