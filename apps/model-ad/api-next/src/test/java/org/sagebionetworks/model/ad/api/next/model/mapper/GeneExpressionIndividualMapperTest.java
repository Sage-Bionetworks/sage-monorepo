package org.sagebionetworks.model.ad.api.next.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionIndividualDocument;
import org.sagebionetworks.model.ad.api.next.model.document.IndividualData;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionIndividualDto;

class GeneExpressionIndividualMapperTest {

  private GeneExpressionIndividualMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new GeneExpressionIndividualMapper(new IndividualDataMapper());
  }

  @Test
  @DisplayName("should map document to dto with all fields")
  void shouldMapDocumentToDtoWithAllFields() {
    // given
    GeneExpressionIndividualDocument document = new GeneExpressionIndividualDocument();
    document.setId(new ObjectId());
    document.setEnsemblGeneId("ENSG00000001");
    document.setGeneSymbol("GENE1");
    document.setTissue("brain");
    document.setName("5XFAD");
    document.setModelGroup("AD");
    document.setMatchedControl("Control1");
    document.setUnits("TPM");
    document.setAge("6mo");
    document.setAgeNumeric(6);
    document.setResultOrder(List.of("sample1", "sample2"));

    IndividualData data1 = createIndividualData("sample1", "WT", "Female", 10.5);
    IndividualData data2 = createIndividualData("sample2", "MUT", "Male", 15.3);
    document.setData(List.of(data1, data2));

    // when
    GeneExpressionIndividualDto dto = mapper.toDto(document);

    // then
    assertThat(dto).isNotNull();
    assertThat(dto.getEnsemblGeneId()).isEqualTo("ENSG00000001");
    assertThat(dto.getGeneSymbol()).isEqualTo("GENE1");
    assertThat(dto.getTissue()).isEqualTo("brain");
    assertThat(dto.getName()).isEqualTo("5XFAD");
    assertThat(dto.getModelGroup()).isEqualTo("AD");
    assertThat(dto.getMatchedControl()).isEqualTo("Control1");
    assertThat(dto.getUnits()).isEqualTo("TPM");
    assertThat(dto.getAge()).isEqualTo("6mo");
    assertThat(dto.getAgeNumeric()).isEqualTo(6);
    assertThat(dto.getResultOrder()).containsExactly("sample1", "sample2");
    assertThat(dto.getData()).hasSize(2);
    assertThat(dto.getData().get(0).getIndividualId()).isEqualTo("sample1");
    assertThat(dto.getData().get(1).getIndividualId()).isEqualTo("sample2");
  }

  @Test
  @DisplayName("should map document to dto without optional fields")
  void shouldMapDocumentToDtoWithoutOptionalFields() {
    // given
    GeneExpressionIndividualDocument document = new GeneExpressionIndividualDocument();
    document.setId(new ObjectId());
    document.setEnsemblGeneId("ENSG00000001");
    document.setGeneSymbol(null);
    document.setTissue("brain");
    document.setName("5XFAD");
    document.setModelGroup(null);
    document.setMatchedControl("Control1");
    document.setUnits("TPM");
    document.setAge("6mo");
    document.setAgeNumeric(6);
    document.setResultOrder(List.of("sample1"));
    document.setData(List.of(createIndividualData("sample1", "WT", "Female", 10.5)));

    // when
    GeneExpressionIndividualDto dto = mapper.toDto(document);

    // then
    assertThat(dto).isNotNull();
    assertThat(dto.getEnsemblGeneId()).isEqualTo("ENSG00000001");
    assertThat(dto.getGeneSymbol()).isNull();
    assertThat(dto.getModelGroup()).isNull();
    assertThat(dto.getName()).isEqualTo("5XFAD");
  }

  @Test
  @DisplayName("should handle null resultOrder gracefully")
  void shouldHandleNullResultOrderGracefully() {
    // given
    GeneExpressionIndividualDocument document = new GeneExpressionIndividualDocument();
    document.setId(new ObjectId());
    document.setEnsemblGeneId("ENSG00000001");
    document.setTissue("brain");
    document.setName("5XFAD");
    document.setMatchedControl("Control1");
    document.setUnits("TPM");
    document.setAge("6mo");
    document.setAgeNumeric(6);
    document.setResultOrder(null);
    document.setData(List.of());

    // when
    GeneExpressionIndividualDto dto = mapper.toDto(document);

    // then
    assertThat(dto).isNotNull();
    assertThat(dto.getResultOrder()).isEmpty();
  }

  @Test
  @DisplayName("should handle null data gracefully")
  void shouldHandleNullDataGracefully() {
    // given
    GeneExpressionIndividualDocument document = new GeneExpressionIndividualDocument();
    document.setId(new ObjectId());
    document.setEnsemblGeneId("ENSG00000001");
    document.setTissue("brain");
    document.setName("5XFAD");
    document.setMatchedControl("Control1");
    document.setUnits("TPM");
    document.setAge("6mo");
    document.setAgeNumeric(6);
    document.setResultOrder(List.of());
    document.setData(null);

    // when
    GeneExpressionIndividualDto dto = mapper.toDto(document);

    // then
    assertThat(dto).isNotNull();
    assertThat(dto.getData()).isEmpty();
  }

  @Test
  @DisplayName("should return null when document is null")
  void shouldReturnNullWhenDocumentIsNull() {
    // given / when
    GeneExpressionIndividualDto dto = mapper.toDto(null);

    // then
    assertThat(dto).isNull();
  }

  private IndividualData createIndividualData(
    String individualId,
    String genotype,
    String sex,
    Double value
  ) {
    return IndividualData.builder()
      .individualId(individualId)
      .genotype(genotype)
      .sex(sex)
      .value(BigDecimal.valueOf(value))
      .build();
  }
}
