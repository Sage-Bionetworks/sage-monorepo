import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ApiUtilTest {

  @Mock
  private NativeWebRequest nativeWebRequest;

  @Test
  void SetExampleResponse_ShouldReturnExpectedResponse() throws IOException {
    // Create a mock NativeWebRequest
    nativeWebRequest = new MockNativeWebRequest();

    // Set up mock behavior
    HttpServletResponse mockResponse = new MockHttpServletResponse();
    when(nativeWebRequest.getNativeResponse(HttpServletResponse.class)).thenReturn(mockResponse);

    // Define example content
    String contentType = "application/json";
    String example = "{\"key\": \"value\"}";

    // Call the setExampleResponse method
    ApiUtil.setExampleResponse(nativeWebRequest, contentType, example);

    // Verify the response
    assertEquals(contentType, mockResponse.getHeader("Content-Type"));
    assertEquals(example, mockResponse.getContentAsString());
  }

  private static class MockNativeWebRequest implements NativeWebRequest {
    @Override
    public <T> T getNativeRequest(Class<T> requiredType) {
      return null;
    }

    @Override
    public <T> T getNativeResponse(Class<T> requiredType) {
      return null;
    }

    @Override
    public String getHeader(String name) {
      return null;
    }

    @Override
    public String[] getHeaderValues(String name) {
      return new String[0];
    }

  }
}