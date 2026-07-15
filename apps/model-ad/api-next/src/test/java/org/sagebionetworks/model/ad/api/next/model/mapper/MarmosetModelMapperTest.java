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
import org.sagebionetworks.model.ad.api.next.model.document.MarmosetModelDocument;
import org.sagebionetworks.model.ad.api.next.model.document.ModelData;
import org.sagebionetworks.model.ad.api.next.model.dto.MarmosetModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelDto;
import org.sagebionetworks.model.ad.api.next.model.dto.OrganismDto;

class MarmosetModelMapperTest {

  private MarmosetModelMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new MarmosetModelMapper(
      new GeneticInfoMapper(),
      new ModelDataMapper(new IndividualDataMapper())
    );
  }

  @Test
  @DisplayName("should map document to MarmosetModelDto with type stamped as marmoset")
  void shouldMapDocumentToMarmosetModelDtoWithTypeStampedAsMarmoset() {
    // given
    MarmosetModelDocument document = buildDocument();

    // when
    ModelDto dto = mapper.toDto(document);

    // then
    assertThat(dto).isInstanceOf(MarmosetModelDto.class);
    MarmosetModelDto marmosetModel = (MarmosetModelDto) dto;
    assertThat(marmosetModel.getType()).isEqualTo(OrganismDto.MARMOSET.getValue());
    assertThat(marmosetModel.getName()).isEqualTo("Marmoset1");
    assertThat(marmosetModel.getModelType()).isEqualTo("Marmoset AD");
    assertThat(marmosetModel.getStudySynid()).isEqualTo("syn999");
    assertThat(marmosetModel.getGeneticInfo()).hasSize(1);
    assertThat(marmosetModel.getGeneticInfo().get(0).getModifiedGene()).isEqualTo("PSEN1");
    assertThat(marmosetModel.getGeneticInfo().get(0).getAllele()).isNull();
    assertThat(marmosetModel.getGeneticInfo().get(0).getMgiAlleleId()).isNull();
    assertThat(marmosetModel.getBiomarkers()).hasSize(1);
    assertThat(marmosetModel.getBiomarkers().get(0).getTissue()).isNull();
    assertThat(marmosetModel.getBiomarkers().get(0).getData()).hasSize(1);
  }

  @Test
  @DisplayName("should map null list fields to empty lists")
  void shouldMapNullListFieldsToEmptyLists() {
    // given
    MarmosetModelDocument document = new MarmosetModelDocument();
    document.setId(new ObjectId());
    document.setName("Marmoset1");
    document.setModelType("Marmoset AD");
    document.setStudySynid("syn999");
    document.setGeneticInfo(null);
    document.setBiomarkers(null);

    // when
    MarmosetModelDto marmosetModel = (MarmosetModelDto) mapper.toDto(document);

    // then
    assertThat(marmosetModel.getGeneticInfo()).isEmpty();
    assertThat(marmosetModel.getBiomarkers()).isEmpty();
  }

  @Test
  @DisplayName("should return null when document is null")
  void shouldReturnNullWhenDocumentIsNull() {
    assertThat(mapper.toDto(null)).isNull();
  }

  private MarmosetModelDocument buildDocument() {
    GeneticInfo geneticInfo = GeneticInfo.builder()
      .modifiedGene("PSEN1")
      .ensemblGeneId("ENSG2")
      .alleleType("knock-in")
      .build();

    IndividualData individual = IndividualData.builder()
      .individualId("ind1")
      .genotype("MUT")
      .sex("Male")
      .value(BigDecimal.valueOf(3.1))
      .build();

    ModelData biomarker = ModelData.builder()
      .name("biomarker1")
      .evidenceType("evidence")
      .age("12mo")
      .units("pg/mL")
      .yAxisMax(BigDecimal.valueOf(20))
      .data(List.of(individual))
      .build();

    MarmosetModelDocument document = new MarmosetModelDocument();
    document.setId(new ObjectId());
    document.setName("Marmoset1");
    document.setModelType("Marmoset AD");
    document.setStudySynid("syn999");
    document.setGeneticInfo(List.of(geneticInfo));
    document.setBiomarkers(List.of(biomarker));
    return document;
  }
}
