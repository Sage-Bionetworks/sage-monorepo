package org.sagebionetworks.model.ad.api.next.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sagebionetworks.model.ad.api.next.api.ModelApiController;
import org.sagebionetworks.model.ad.api.next.api.ModelApiDelegate;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelOrganismDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PreviousApiVersionAliasFilterWebTest {

  private static final String CURRENT_BASE_PATH = "/v2";
  private static final String MODEL_PATH = "/models/mouse/3xTg-AD";
  private static final String ENCODED_SLASH_MODEL_PATH = "/models/mouse/5xFAD%20(IU%2FJax%2FPitt)";

  private ModelApiDelegate delegate;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    delegate = mock(ModelApiDelegate.class);
    when(delegate.getModelByName(eq(ModelOrganismDto.MOUSE), any())).thenReturn(
      ResponseEntity.ok(new MouseModelDto().type(ModelOrganismDto.MOUSE.getValue()).name("3xTg-AD"))
    );
    var conversionService = new FormattingConversionService();
    conversionService.addConverter(
      new Converter<String, ModelOrganismDto>() {
        @Override
        public ModelOrganismDto convert(String source) {
          return ModelOrganismDto.fromValue(source);
        }
      }
    );
    mockMvc = MockMvcBuilders.standaloneSetup(new ModelApiController(delegate))
      .setConversionService(conversionService)
      .addFilters(new PreviousApiVersionAliasFilter(CURRENT_BASE_PATH))
      .build();
  }

  @Test
  @DisplayName("should serve a previous-version path from the current-version controller")
  void shouldServePreviousVersionPathFromCurrentVersionController() throws Exception {
    mockMvc
      .perform(get(PreviousApiVersionAliasFilter.PREVIOUS_BASE_PATH + MODEL_PATH))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.type").value(ModelOrganismDto.MOUSE.getValue()));
  }

  @Test
  @DisplayName("should serve a current-version path unchanged")
  void shouldServeCurrentVersionPathUnchanged() throws Exception {
    mockMvc
      .perform(get(CURRENT_BASE_PATH + MODEL_PATH))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.type").value(ModelOrganismDto.MOUSE.getValue()));
  }

  @Test
  @DisplayName("should not serve a path outside both api versions")
  void shouldNotServePathOutsideBothApiVersions() throws Exception {
    mockMvc.perform(get(MODEL_PATH)).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("should pass the same encoded model name as the current-version path")
  void shouldPassSameEncodedModelNameAsCurrentVersionPath() throws Exception {
    mockMvc
      .perform(get(URI.create(CURRENT_BASE_PATH + ENCODED_SLASH_MODEL_PATH)))
      .andExpect(status().isOk());
    String currentVersionName = captureRequestedModelName();
    clearInvocations(delegate);

    mockMvc
      .perform(
        get(URI.create(PreviousApiVersionAliasFilter.PREVIOUS_BASE_PATH + ENCODED_SLASH_MODEL_PATH))
      )
      .andExpect(status().isOk());

    assertThat(captureRequestedModelName()).isEqualTo(currentVersionName);
  }

  private String captureRequestedModelName() {
    ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class);
    verify(delegate).getModelByName(eq(ModelOrganismDto.MOUSE), name.capture());
    return name.getValue();
  }
}
