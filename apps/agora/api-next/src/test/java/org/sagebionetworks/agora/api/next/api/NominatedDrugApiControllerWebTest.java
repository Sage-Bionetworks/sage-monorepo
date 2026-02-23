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
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugSearchQueryDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

class NominatedDrugApiControllerWebTest {

  private NominatedDrugApiDelegate delegate;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    delegate = mock(NominatedDrugApiDelegate.class);
    var controller = new NominatedDrugApiController(delegate);
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
      new Converter<String, NominatedDrugSearchQueryDto.SortOrdersEnum>() {
        @Override
        public NominatedDrugSearchQueryDto.SortOrdersEnum convert(String source) {
          return NominatedDrugSearchQueryDto.SortOrdersEnum.fromValue(Integer.parseInt(source));
        }
      }
    );
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setControllerAdvice(new GlobalExceptionHandler())
      .setConversionService(conversionService)
      .build();
  }

  @Test
  @DisplayName("should return not found problem when nominated drug not found")
  void shouldReturnNotFoundProblemWhenDelegateRaisesNominatedDrugNotFoundException()
    throws Exception {
    String commonName = "Agomelatine";
    when(delegate.getNominatedDrugs(any())).thenThrow(
      new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Nominated drug not found with common_name: " + commonName
      )
    );

    mockMvc
      .perform(
        get("/v1/comparison-tools/drugs")
          .param("items", commonName)
          .param("sortFields", "common_name")
          .param("sortOrders", "1")
      )
      .andExpect(status().isNotFound());
  }
}
