package org.sagebionetworks.openchallenges.image.service.api;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.sagebionetworks.openchallenges.image.service.service.ImageService;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageQueryDto;
import org.sagebionetworks.openchallenges.app.config.data.ImageServiceConfigData;
import org.sagebionetworks.openchallenges.app.config.data.ImageServiceConfigData;
import org.sagebionetworks.openchallenges.image.service.exception.ImageHeightNotSpecifiedException;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageAspectRatioDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageHeightDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageQueryDto;
import static org.mockito.Mockito.when;

public class ImageApiDelegateImplTest {

    @Mock
    private ImageService imageService;

    private ImageApiDelegateImpl imageApiDelegate;

    public ImageApiDelegateImplTest() {
        MockitoAnnotations.openMocks(this);
        imageApiDelegate = new ImageApiDelegateImpl(imageService);
    }

    @Test
    public void HttpStatusReturnsAsExpectedforGetImage() {
        // Create a sample ImageQueryDto
        ImageQueryDto queryDto = new ImageQueryDto();

        // Create a sample ImageDto response
        ImageDto imageDto = new ImageDto();

        // Mock the behavior of the ImageService
        when(imageService.getImage(queryDto)).thenReturn(imageDto);

        // Call the getImage method of the ImageApiDelegateImpl
        ResponseEntity<ImageDto> responseEntity = imageApiDelegate.getImage(queryDto);

        // Verify the response
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void BodyofGetImageReturnsCorrectValue() {
        // Create a sample ImageQueryDto
        ImageQueryDto queryDto = new ImageQueryDto();

        // Create a sample ImageDto response
        ImageDto imageDto = new ImageDto();

        // Mock the behavior of the ImageService
        when(imageService.getImage(queryDto)).thenReturn(imageDto);

        // Call the getImage method of the ImageApiDelegateImpl
        ResponseEntity<ImageDto> responseEntity = imageApiDelegate.getImage(queryDto);

        // Verify the response
        assertThat(responseEntity.getBody()).isEqualTo(imageDto);
    }
}