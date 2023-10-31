import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ImageAspectRatioDtoTest {

  @Test
  void GetValueShouldGetSpecifiedNumericCombination() {
    // Test the getValue() method for each enum value
    assertEquals("original", ImageAspectRatioDto.ORIGINAL.getValue());
    assertEquals("16_9", ImageAspectRatioDto._16_9.getValue());
    assertEquals("1_1", ImageAspectRatioDto._1_1.getValue());
    assertEquals("3_2", ImageAspectRatioDto._3_2.getValue());
    assertEquals("2_3", ImageAspectRatioDto._2_3.getValue());
  }

  @Test
  void FromValueShouldEqualExpectedValue() {
    // Test the fromValue() method for each enum value
    assertEquals(ImageAspectRatioDto.ORIGINAL, ImageAspectRatioDto.fromValue("original"));
    assertEquals(ImageAspectRatioDto._16_9, ImageAspectRatioDto.fromValue("16_9"));
    assertEquals(ImageAspectRatioDto._1_1, ImageAspectRatioDto.fromValue("1_1"));
    assertEquals(ImageAspectRatioDto._3_2, ImageAspectRatioDto.fromValue("3_2"));
    assertEquals(ImageAspectRatioDto._2_3, ImageAspectRatioDto.fromValue("2_3"));
  }

  @Test(expected = IllegalArgumentException.class)
  void testFromShouldReturnInvalidValue() {
    // Test the fromValue() method with an invalid value
    ImageAspectRatioDto.fromValue("invalid_value");
  }
}
