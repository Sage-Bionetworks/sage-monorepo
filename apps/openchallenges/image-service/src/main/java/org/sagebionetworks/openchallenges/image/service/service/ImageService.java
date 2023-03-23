package org.sagebionetworks.openchallenges.image.service.service;

import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

  public ImageDto getImage(String image) {
    String imageUrl = String.format("http://localhost:8889/unsafe/%s", image);
    return ImageDto.builder().url(imageUrl).build();
  }
}
