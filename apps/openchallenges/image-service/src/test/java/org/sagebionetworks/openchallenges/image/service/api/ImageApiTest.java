import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sagebionetworks.openchallenges.image.service.api.ImageApiDelegate;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageDto;
import org.sagebionetworks.openchallenges.image.service.model.dto.ImageQueryDto;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class ImageApiTest {

  @Mock private ImageApiDelegate delegate;

  @Test
  public void testGetImage() {
    // Create an instance of ImageApi
    ImageApi imageApi =
        new ImageApi() {
          @Override
          public ImageApiDelegate getDelegate() {
            return delegate;
          }
        };

    // Create mock response
    ImageDto mockImageDto = new ImageDto();
    ResponseEntity<ImageDto> mockResponse = ResponseEntity.ok(mockImageDto);

    // Create a mock ImageQueryDto
    ImageQueryDto mockImageQuery = new ImageQueryDto();

    // Set up mock behavior
    when(delegate.getImage(mockImageQuery)).thenReturn(mockResponse);

    // Call the getImage method
    ResponseEntity<ImageDto> response = imageApi.getImage(mockImageQuery);

    // Verify the response
    assertEquals(mockResponse, response);
  }
}
