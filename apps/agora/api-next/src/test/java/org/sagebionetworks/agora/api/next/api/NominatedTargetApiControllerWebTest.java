package org.sagebionetworks.agora.api.next.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.agora.api.next.exception.GlobalExceptionHandler;
import org.sagebionetworks.agora.api.next.model.dto.ItemFilterTypeQueryDto;
import org.sagebionetworks.agora.api.next.model.dto.NominatedTargetSearchQueryDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

class NominatedTargetApiControllerWebTest {

  private NominatedTargetApiDelegate delegate;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    delegate = mock(NominatedTargetApiDelegate.class);
    var controller = new NominatedTargetApiController(delegate);
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
      new Converter<String, NominatedTargetSearchQueryDto.SortOrdersEnum>() {
        @Override
        public NominatedTargetSearchQueryDto.SortOrdersEnum convert(String source) {
          return NominatedTargetSearchQueryDto.SortOrdersEnum.fromValue(Integer.parseInt(source));
        }
      }
    );
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setControllerAdvice(new GlobalExceptionHandler())
      .setConversionService(conversionService)
      .build();
  }

  @Test
  @DisplayName("should return not found problem when nominated target not found")
  void shouldReturnNotFoundProblemWhenDelegateRaisesNominatedTargetNotFoundException()
    throws Exception {
    String hgncSymbol = "TP53";
    when(delegate.getNominatedTargets(any())).thenThrow(
      new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Nominated target not found with hgnc_symbol: " + hgncSymbol
      )
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/nominated-target")
          .param("items", hgncSymbol)
          .param("sortFields", "hgnc_symbol")
          .param("sortOrders", "1")
      )
      .andExpect(status().isNotFound());
  }
}
