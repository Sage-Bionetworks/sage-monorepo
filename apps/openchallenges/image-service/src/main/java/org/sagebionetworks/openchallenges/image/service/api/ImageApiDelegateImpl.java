package org.sagebionetworks.openchallenges.image.service.api;

import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageQueryDto;
import org.sagebionetworks.openchallenges.image.service.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ImageApiDelegateImpl implements ImageApiDelegate {

  private final ImageService imageService;

  public ImageApiDelegateImpl(ImageService imageService) {
    this.imageService = imageService;
  }

  @Override
  public ResponseEntity<ImageDto> getImage(ImageQueryDto query) {
    return ResponseEntity.ok(imageService.getImage(query));
  }
}
