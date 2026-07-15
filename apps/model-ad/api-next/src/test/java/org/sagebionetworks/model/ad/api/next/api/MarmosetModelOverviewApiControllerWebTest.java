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
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelOverviewSearchQueryDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

class MarmosetModelOverviewApiControllerWebTest {

  private MarmosetModelOverviewApiDelegate delegate;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    delegate = mock(MarmosetModelOverviewApiDelegate.class);
    var controller = new MarmosetModelOverviewApiController(delegate);
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
      new Converter<String, MarmosetModelOverviewSearchQueryDto.SortOrdersEnum>() {
        @Override
        public MarmosetModelOverviewSearchQueryDto.SortOrdersEnum convert(String source) {
          return MarmosetModelOverviewSearchQueryDto.SortOrdersEnum.fromValue(
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
  @DisplayName("should return not found problem when marmoset model overview not found")
  void shouldReturnNotFoundProblemWhenDelegateRaisesMarmosetModelOverviewNotFoundException()
    throws Exception {
    String modelName = "3xTg-AD";
    when(delegate.getMarmosetModelOverviews(any())).thenThrow(
      new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Marmoset model overview not found with name: " + modelName
      )
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/marmoset-model-overview")
          .param("items", modelName)
          .param("sortFields", "name")
          .param("sortOrders", "1")
      )
      .andExpect(status().isNotFound());
  }
}
