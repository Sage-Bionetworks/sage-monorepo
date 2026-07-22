package org.sagebionetworks.model.ad.api.next.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.exception.GlobalExceptionHandler;
import org.sagebionetworks.model.ad.api.next.exception.ModelNotFoundException;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.OrganismDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ModelApiControllerWebTest {

  private ModelApiDelegate delegate;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    delegate = mock(ModelApiDelegate.class);
    var controller = new ModelApiController(delegate);
    var conversionService = new FormattingConversionService();
    conversionService.addConverter(
      new Converter<String, OrganismDto>() {
        @Override
        public OrganismDto convert(String source) {
          return OrganismDto.fromValue(source);
        }
      }
    );
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setControllerAdvice(new GlobalExceptionHandler())
      .setConversionService(conversionService)
      .build();
  }

  @Test
  @DisplayName("should delegate and return ok when organism and name are valid")
  void shouldDelegateAndReturnOkWhenOrganismAndNameAreValid() throws Exception {
    MouseModelDto model = new MouseModelDto().type(OrganismDto.MOUSE.getValue()).name("3xTg-AD");
    when(delegate.getModelByName(eq(OrganismDto.MOUSE), any())).thenReturn(
      ResponseEntity.ok(model)
    );

    mockMvc
      .perform(get("/v1/models/mouse/3xTg-AD"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.type").value("mouse"));
  }

  @Test
  @DisplayName("should return bad request when organism is unknown")
  void shouldReturnBadRequestWhenOrganismIsUnknown() throws Exception {
    mockMvc.perform(get("/v1/models/rat/3xTg-AD")).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should return not found when model name does not exist")
  void shouldReturnNotFoundWhenModelNameDoesNotExist() throws Exception {
    when(delegate.getModelByName(eq(OrganismDto.MOUSE), any())).thenThrow(
      new ModelNotFoundException(OrganismDto.MOUSE, "missing")
    );

    mockMvc.perform(get("/v1/models/mouse/missing")).andExpect(status().isNotFound());
  }
}
