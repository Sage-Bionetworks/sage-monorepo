package org.sagebionetworks.openchallenges.image.service.service;

import com.squareup.pollexor.Thumbor;
import com.squareup.pollexor.ThumborUrlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.image.service.configuration.AppProperties;
import org.sagebionetworks.openchallenges.image.service.exception.ImageHeightNotSpecifiedException;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageAspectRatioDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageHeightDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageQueryDto;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ImageService {

  private final Thumbor thumbor;
  private final AppProperties appProperties;

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

    log.info("Requesting an image url for the objectId: {}", query.getObjectKey());

    Integer height = getImageHeightInPx(query.getHeight());
    Integer width = (height != null) ? getImageWidthInPixel(height, query.getAspectRatio()) : null; // width only matters if we have a height

    // Placeholder logic (dev convenience when using Thumbor's HTTP loader)
    AppProperties.ThumborProperties thumborProps = appProperties.thumbor();
    if (thumborProps.usePlaceholderImages()) {
      String template = thumborProps.placeholderUrlTemplate();
      if (template == null || template.isBlank()) {
        template = "https://images.placeholders.dev/{width}x{height}";
      }
      // If no height was requested, use a default (e.g. 250) so we can still return something
      int effectiveHeight = (height != null) ? height : 250;
      int effectiveWidth = (width != null && width > 0) ? width : effectiveHeight; // square fallback
      String placeholderUrl = template
        .replace("{width}", String.valueOf(effectiveWidth))
        .replace("{height}", String.valueOf(effectiveHeight));
      log.debug("Returning placeholder image URL: {}", placeholderUrl);
      return placeholderUrl;
    }

    // Normal Thumbor flow
    ThumborUrlBuilder builder = thumbor.buildImage(query.getObjectKey());
    if (height != null) {
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
