import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sagebionetworks.openchallenges.image.service.ImageService;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ImageApiDelegateTest {

  @Mock
  private ImageService imageService;

  @Test
  void ImageApiDelegate_ShouldHaveInjectedImageServiceDependency() {
    // Create an instance of ImageApiDelegateImpl
    ImageApiDelegateImpl delegate = new ImageApiDelegateImpl(imageService);

    // Verify that the ImageService dependency is injected
    assertNotNull(delegate.getImageService());
  }
}