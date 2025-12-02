package org.sagebionetworks.model.ad.api.next.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.exception.GlobalExceptionHandler;
import org.sagebionetworks.model.ad.api.next.exception.InvalidObjectIdException;
import org.sagebionetworks.model.ad.api.next.exception.ModelOverviewNotFoundException;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ModelOverviewApiControllerWebTest {

  private ModelOverviewApiDelegate delegate;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    delegate = mock(ModelOverviewApiDelegate.class);
    var controller = new ModelOverviewApiController(delegate);
    var conversionService = new FormattingConversionService();
    conversionService.addConverter(
      new Converter<String, ItemFilterTypeQueryDto>() {
        @Override
        public ItemFilterTypeQueryDto convert(String source) {
          return ItemFilterTypeQueryDto.fromValue(source);
        }
      }
    );
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setControllerAdvice(new GlobalExceptionHandler())
      .setConversionService(conversionService)
      .build();
  }

  @Test
  @DisplayName("should return bad request problem when delegate raises InvalidObjectIdException")
  void shouldReturnBadRequestProblemWhenDelegateRaisesInvalidObjectIdException() throws Exception {
    when(delegate.getModelOverviews(any())).thenThrow(new InvalidObjectIdException("not-an-id"));

    mockMvc
      .perform(get("/v1/comparison-tools/model-overview").param("item", "not-an-id"))
      .andExpect(status().isBadRequest())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Invalid Request Parameter"))
      .andExpect(jsonPath("$.status").value(400))
      .andExpect(jsonPath("$.detail").value("Invalid ObjectId format: not-an-id"))
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/model-overview"));
  }

  @Test
  @DisplayName("should return not found problem when model overview not found")
  void shouldReturnNotFoundProblemWhenDelegateRaisesModelOverviewNotFoundException()
    throws Exception {
    String modelId = "673f5d8e8c1a2b3c4d5e6f7a";
    when(delegate.getModelOverviews(any())).thenThrow(new ModelOverviewNotFoundException(modelId));

    mockMvc
      .perform(get("/v1/comparison-tools/model-overview").param("item", modelId))
      .andExpect(status().isNotFound())
      .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
      .andExpect(jsonPath("$.title").value("Entity not found"))
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.detail").value("Model overview not found with id: " + modelId))
      .andExpect(jsonPath("$.instance").value("/v1/comparison-tools/model-overview"));
  }
}
