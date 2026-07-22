package org.sagebionetworks.model.ad.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.model.ad.api.next.exception.ModelNotFoundException;
import org.sagebionetworks.model.ad.api.next.model.document.MarmosetModelDocument;
import org.sagebionetworks.model.ad.api.next.model.document.MouseModelDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.OrganismDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.GeneticInfoMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.IndividualDataMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.MarmosetModelMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.ModelDataMapper;
import org.sagebionetworks.model.ad.api.next.model.mapper.MouseModelMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.MarmosetModelRepository;
import org.sagebionetworks.model.ad.api.next.model.repository.MouseModelRepository;

@ExtendWith(MockitoExtension.class)
class ModelServiceTest {

  @Mock
  private MouseModelRepository mouseModelRepository;

  @Mock
  private MarmosetModelRepository marmosetModelRepository;

  private ModelService service;

  @BeforeEach
  void setUp() {
    GeneticInfoMapper geneticInfoMapper = new GeneticInfoMapper();
    ModelDataMapper modelDataMapper = new ModelDataMapper(new IndividualDataMapper());
    service = new ModelService(
      mouseModelRepository,
      new MouseModelMapper(geneticInfoMapper, modelDataMapper),
      marmosetModelRepository,
      new MarmosetModelMapper(geneticInfoMapper, modelDataMapper)
    );
  }

  @Test
  @DisplayName("should route to mouse repository and mapper when organism is mouse")
  void shouldRouteToMouseRepositoryAndMapperWhenOrganismIsMouse() {
    MouseModelDocument document = new MouseModelDocument();
    document.setName("3xTg-AD");
    when(mouseModelRepository.findByName("3xTg-AD")).thenReturn(Optional.of(document));

    ModelDto result = service.getModelByName(OrganismDto.MOUSE, "3xTg-AD");

    assertThat(result).isInstanceOf(MouseModelDto.class);
    assertThat(((MouseModelDto) result).getType()).isEqualTo(OrganismDto.MOUSE.getValue());
    verifyNoInteractions(marmosetModelRepository);
  }

  @Test
  @DisplayName("should route to marmoset repository and mapper when organism is marmoset")
  void shouldRouteToMarmosetRepositoryAndMapperWhenOrganismIsMarmoset() {
    MarmosetModelDocument document = new MarmosetModelDocument();
    document.setName("Marmoset1");
    when(marmosetModelRepository.findByName("Marmoset1")).thenReturn(Optional.of(document));

    ModelDto result = service.getModelByName(OrganismDto.MARMOSET, "Marmoset1");

    assertThat(result).isInstanceOf(MarmosetModelDto.class);
    assertThat(((MarmosetModelDto) result).getType()).isEqualTo(OrganismDto.MARMOSET.getValue());
    verifyNoInteractions(mouseModelRepository);
  }

  @Test
  @DisplayName("should throw ModelNotFoundException when mouse model is absent")
  void shouldThrowModelNotFoundExceptionWhenMouseModelIsAbsent() {
    when(mouseModelRepository.findByName("missing")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getModelByName(OrganismDto.MOUSE, "missing"))
      .isInstanceOf(ModelNotFoundException.class)
      .hasMessageContaining("mouse")
      .hasMessageContaining("missing");
  }

  @Test
  @DisplayName("should throw ModelNotFoundException when marmoset model is absent")
  void shouldThrowModelNotFoundExceptionWhenMarmosetModelIsAbsent() {
    when(marmosetModelRepository.findByName("missing")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getModelByName(OrganismDto.MARMOSET, "missing"))
      .isInstanceOf(ModelNotFoundException.class)
      .hasMessageContaining("marmoset")
      .hasMessageContaining("missing");
  }
}
