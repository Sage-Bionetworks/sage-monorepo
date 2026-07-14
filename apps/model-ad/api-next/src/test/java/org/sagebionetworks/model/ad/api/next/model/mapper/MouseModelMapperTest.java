package org.sagebionetworks.model.ad.api.next.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.model.document.GeneticInfo;
import org.sagebionetworks.model.ad.api.next.model.document.IndividualData;
import org.sagebionetworks.model.ad.api.next.model.document.ModelData;
import org.sagebionetworks.model.ad.api.next.model.document.MouseModelDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.MouseModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.OrganismDto;

class MouseModelMapperTest {

  private MouseModelMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new MouseModelMapper(
      new GeneticInfoMapper(),
      new ModelDataMapper(new IndividualDataMapper())
    );
  }

  @Test
  @DisplayName("should map document to MouseModelDto with type stamped as mouse")
  void shouldMapDocumentToMouseModelDtoWithTypeStampedAsMouse() {
    // given
    MouseModelDocument document = buildDocument();

    // when
    ModelDto dto = mapper.toDto(document);

    // then
    assertThat(dto).isInstanceOf(MouseModelDto.class);
    MouseModelDto mouseModel = (MouseModelDto) dto;
    assertThat(mouseModel.getType()).isEqualTo(OrganismDto.MOUSE.getValue());
    assertThat(mouseModel.getName()).isEqualTo("3xTg-AD");
    assertThat(mouseModel.getModelType()).isEqualTo("Early Onset AD");
    assertThat(mouseModel.getMatchedControls()).containsExactly("Control1");
    assertThat(mouseModel.getStudySynid()).isEqualTo("syn123");
    assertThat(mouseModel.getRrid()).isEqualTo("RRID:1");
    assertThat(mouseModel.getJaxId()).isEqualTo("JAX:1");
    assertThat(mouseModel.getAlzforumId()).isEqualTo("ALZ:1");
    assertThat(mouseModel.getGenotype()).isEqualTo("homozygous");
    assertThat(mouseModel.getAliases()).containsExactly("alias1");
    assertThat(mouseModel.getGeneticInfo()).hasSize(1);
    assertThat(mouseModel.getGeneticInfo().get(0).getAllele()).isEqualTo("allele1");
    assertThat(mouseModel.getGeneticInfo().get(0).getMgiAlleleId()).isEqualByComparingTo("1.0");
    assertThat(mouseModel.getBiomarkers()).hasSize(1);
    assertThat(mouseModel.getBiomarkers().get(0).getTissue()).isEqualTo("brain");
    assertThat(mouseModel.getBiomarkers().get(0).getData()).hasSize(1);
    assertThat(mouseModel.getPathology()).hasSize(1);
  }

  @Test
  @DisplayName("should map null list fields to empty lists")
  void shouldMapNullListFieldsToEmptyLists() {
    // given
    MouseModelDocument document = new MouseModelDocument();
    document.setId(new ObjectId());
    document.setName("3xTg-AD");
    document.setMatchedControls(null);
    document.setAliases(null);
    document.setGeneticInfo(null);
    document.setBiomarkers(null);
    document.setPathology(null);

    // when
    MouseModelDto mouseModel = (MouseModelDto) mapper.toDto(document);

    // then
    assertThat(mouseModel.getMatchedControls()).isEmpty();
    assertThat(mouseModel.getAliases()).isEmpty();
    assertThat(mouseModel.getGeneticInfo()).isEmpty();
    assertThat(mouseModel.getBiomarkers()).isEmpty();
    assertThat(mouseModel.getPathology()).isEmpty();
  }

  @Test
  @DisplayName("should return null when document is null")
  void shouldReturnNullWhenDocumentIsNull() {
    assertThat(mapper.toDto(null)).isNull();
  }

  private MouseModelDocument buildDocument() {
    GeneticInfo geneticInfo = GeneticInfo.builder()
      .modifiedGene("APP")
      .ensemblGeneId("ENSG1")
      .allele("allele1")
      .alleleType("knock-in")
      .mgiAlleleId(BigDecimal.valueOf(1.0))
      .build();

    IndividualData individual = IndividualData.builder()
      .individualId("ind1")
      .genotype("MUT")
      .sex("Female")
      .value(BigDecimal.valueOf(2.5))
      .build();

    ModelData biomarker = ModelData.builder()
      .name("biomarker1")
      .evidenceType("evidence")
      .tissue("brain")
      .age("6mo")
      .units("pg/mL")
      .yAxisMax(BigDecimal.valueOf(10))
      .data(List.of(individual))
      .build();

    ModelData pathology = ModelData.builder()
      .name("pathology1")
      .evidenceType("evidence")
      .tissue("hippocampus")
      .age("6mo")
      .units("count")
      .yAxisMax(BigDecimal.valueOf(5))
      .data(List.of(individual))
      .build();

    MouseModelDocument document = new MouseModelDocument();
    document.setId(new ObjectId());
    document.setName("3xTg-AD");
    document.setMatchedControls(List.of("Control1"));
    document.setModelType("Early Onset AD");
    document.setContributingGroup("group");
    document.setStudySynid("syn123");
    document.setRrid("RRID:1");
    document.setJaxId("JAX:1");
    document.setAlzforumId("ALZ:1");
    document.setGenotype("homozygous");
    document.setAliases(List.of("alias1"));
    document.setTranscriptomics("transcriptomics-link");
    document.setDiseaseCorrelation("disease-correlation-link");
    document.setSpatialTranscriptomics("spatial-link");
    document.setGeneticInfo(List.of(geneticInfo));
    document.setBiomarkers(List.of(biomarker));
    document.setPathology(List.of(pathology));
    return document;
  }
}
