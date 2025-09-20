package org.sagebionetworks.openchallenges.image.service.service;

import com.squareup.pollexor.Thumbor;
import com.squareup.pollexor.ThumborUrlBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.image.service.configuration.AppProperties;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageAspectRatioDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageHeightDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageQueryDto;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ImageService {

  private static final int DEFAULT_HEIGHT = 250;

  private final Thumbor thumbor;
  private final AppProperties appProperties;

  public ImageDto getImage(ImageQueryDto query) {
    String imageUrl = generateImageUrl(query);
    return ImageDto.builder().url(imageUrl).build();
  }

  private String generateImageUrl(ImageQueryDto query) {
    // Query has already been validated externally:
    // - height and aspectRatio are non-null with defaults of ORIGINAL

    final String source = (query.getObjectKey() != null)
      ? query.getObjectKey()
      : query.getImageUrl().toString();

    log.debug(
      "Requesting image for source='{}', height={}, ratio={}",
      source,
      query.getHeight(),
      query.getAspectRatio()
    );

    Dimensions dims = computeDimensions(query.getHeight(), query.getAspectRatio());

    // Placeholder short-circuit (independent of Thumbor)
    AppProperties.PlaceholderProperties ph = appProperties.placeholder();
    if (ph != null && ph.enabled()) {
      String url = buildPlaceholderUrl(ph, dims);
      log.debug("Returning placeholder image for source='{}': {}", source, url);
      return url;
    }

    // Normal Thumbor flow; pass objectKey or direct URL depending on query
    String url = buildThumborUrl(source, dims);
    log.debug("Returning thumbor image for source='{}': {}", source, url);
    return url;
  }

  /** Compute width/height from enums; width=0 indicates “preserve aspect by height” for Thumbor. */
  private Dimensions computeDimensions(ImageHeightDto heightDto, ImageAspectRatioDto ratioDto) {
    Integer heightPx = toHeightPx(heightDto); // null means ORIGINAL (no resize)
    if (heightPx == null) {
      // No resize: width=0, height=null → return original image from Thumbor
      return new Dimensions(0, null);
    }
    int widthPx = toWidthPx(heightPx, ratioDto);
    return new Dimensions(widthPx, heightPx);
  }

  private Integer toHeightPx(ImageHeightDto height) {
    return switch (height) {
      case ORIGINAL -> null;
      case _32PX -> 32;
      case _100PX -> 100;
      case _140PX -> 140;
      case _250PX -> 250;
      case _500PX -> 500;
    };
  }

  /**
   * If ratio is ORIGINAL, let Thumbor keep source aspect by passing width=0 with a fixed height.
   */
  private int toWidthPx(int height, ImageAspectRatioDto ratio) {
    return switch (ratio) {
      case ORIGINAL -> 0; // let Thumbor derive width from source aspect ratio
      case _1_1 -> height;
      case _16_9 -> Math.round((height * 16f) / 9f);
      case _3_2 -> Math.round((height * 3f) / 2f);
      case _2_3 -> Math.round((height * 2f) / 3f);
    };
  }

  private String buildPlaceholderUrl(AppProperties.PlaceholderProperties ph, Dimensions dims) {
    String template = (ph.urlTemplate() == null || ph.urlTemplate().isBlank())
      ? "https://images.placeholders.dev/{width}x{height}"
      : ph.urlTemplate();

    // Placeholders need concrete positive numbers
    int h = (dims.height() != null && dims.height() > 0) ? dims.height() : DEFAULT_HEIGHT;
    int w = (dims.width() > 0) ? dims.width() : h; // square fallback if width==0

    return template.replace("{width}", String.valueOf(w)).replace("{height}", String.valueOf(h));
  }

  private String buildThumborUrl(String source, Dimensions dims) {
    ThumborUrlBuilder builder = thumbor.buildImage(source);
    if (dims.height() != null) {
      // width may be 0 (preserve aspect); this is intentional for Thumbor
      builder = builder.resize(dims.width(), dims.height());
    }
    return builder.toUrl();
  }

  private record Dimensions(int width, Integer height) {}
}
