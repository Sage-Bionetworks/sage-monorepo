package org.sagebionetworks.model.ad.api.next.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.exception.GlobalExceptionHandler;
import org.sagebionetworks.model.ad.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelOverviewSearchQueryDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

class MouseModelOverviewApiControllerWebTest {

  private MouseModelOverviewApiDelegate delegate;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    delegate = mock(MouseModelOverviewApiDelegate.class);
    var controller = new MouseModelOverviewApiController(delegate);
    var conversionService = new FormattingConversionService();
    conversionService.addConverter(
      new Converter<String, ItemFilterTypeQueryDto>() {
        @Override
        public ItemFilterTypeQueryDto convert(String source) {
          return ItemFilterTypeQueryDto.fromValue(source);
        }
      }
    );
    conversionService.addConverter(
      new Converter<String, MouseModelOverviewSearchQueryDto.SortOrdersEnum>() {
        @Override
        public MouseModelOverviewSearchQueryDto.SortOrdersEnum convert(String source) {
          return MouseModelOverviewSearchQueryDto.SortOrdersEnum.fromValue(
            Integer.parseInt(source)
          );
        }
      }
    );
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setControllerAdvice(new GlobalExceptionHandler())
      .setConversionService(conversionService)
      .build();
  }

  @Test
  @DisplayName("should return not found problem when model overview not found")
  void shouldReturnNotFoundProblemWhenDelegateRaisesMouseModelOverviewNotFoundException()
    throws Exception {
    String modelName = "3xTg-AD";
    when(delegate.getMouseModelOverviews(any())).thenThrow(
      new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Model overview not found with name: " + modelName
      )
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/mouse-model-overview")
          .param("items", modelName)
          .param("sortFields", "name")
          .param("sortOrders", "1")
      )
      .andExpect(status().isNotFound());
  }
}
