import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertEquals;

public class ImageHeightNotSpecifiedExceptionTest {

  @Test
  public void testConstructor() {
    // Define the exception detail
    String detail = "Image height is not specified";

    // Create an instance of ImageHeightNotSpecifiedException
    ImageHeightNotSpecifiedException exception = new ImageHeightNotSpecifiedException(detail);

    // Verify the exception details
    assertEquals(ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED.getType(), exception.getType());
    assertEquals(ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED.getTitle(), exception.getTitle());
    assertEquals(ErrorConstants.IMAGE_HEIGHT_NOT_SPECIFIED.getStatus(), exception.getStatus());
    assertEquals(detail, exception.getDetail());
  }
}
