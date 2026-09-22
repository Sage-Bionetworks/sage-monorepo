package org.sagebionetworks.model.ad.api.next.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.model.document.IndividualData;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsIndividualDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualDto;

class ProteomicsIndividualMapperTest {

  private ProteomicsIndividualMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ProteomicsIndividualMapper(new IndividualDataMapper());
  }

  @Test
  @DisplayName("should map document to dto with all fields")
  void shouldMapDocumentToDtoWithAllFields() {
    // given
    ProteomicsIndividualDocument document = new ProteomicsIndividualDocument();
    document.setId(new ObjectId());
    document.setEnsemblGeneId("ENSMUSG00000000001");
    document.setGeneSymbol("Gnai3");
    document.setUniprotid("P27144");
    document.setUniqueId("ENSMUSG00000000001P27144");
    document.setDisplaySymbol("Gnai3 (P27144)");
    document.setTissue("Hemibrain");
    document.setName("LOAD2");
    document.setModelGroup("LOAD");
    document.setMatchedControl("LOAD1");
    document.setUnits("Log2 Counts per Million");
    document.setAge("4 months");
    document.setAgeNumeric(4);
    document.setResultOrder(List.of("LOAD1", "LOAD2"));

    IndividualData data1 = createIndividualData("sample1", "LOAD1", "Female", 10.5);
    IndividualData data2 = createIndividualData("sample2", "LOAD2", "Male", 15.3);
    document.setData(List.of(data1, data2));

    // when
    ProteomicsIndividualDto dto = mapper.toDto(document);

    // then
    assertThat(dto).isNotNull();
    assertThat(dto.getEnsemblGeneId()).isEqualTo("ENSMUSG00000000001");
    assertThat(dto.getGeneSymbol()).isEqualTo("Gnai3");
    assertThat(dto.getUniprotid()).isEqualTo("P27144");
    assertThat(dto.getUniqueId()).isEqualTo("ENSMUSG00000000001P27144");
    assertThat(dto.getDisplaySymbol()).isEqualTo("Gnai3 (P27144)");
    assertThat(dto.getTissue()).isEqualTo("Hemibrain");
    assertThat(dto.getName()).isEqualTo("LOAD2");
    assertThat(dto.getModelGroup()).isEqualTo("LOAD");
    assertThat(dto.getMatchedControl()).isEqualTo("LOAD1");
    assertThat(dto.getUnits()).isEqualTo("Log2 Counts per Million");
    assertThat(dto.getAge()).isEqualTo("4 months");
    assertThat(dto.getAgeNumeric()).isEqualTo(4);
    assertThat(dto.getResultOrder()).containsExactly("LOAD1", "LOAD2");
    assertThat(dto.getData()).hasSize(2);
    assertThat(dto.getData().get(0).getIndividualId()).isEqualTo("sample1");
    assertThat(dto.getData().get(1).getIndividualId()).isEqualTo("sample2");
  }

  @Test
  @DisplayName("should map document to dto without optional fields")
  void shouldMapDocumentToDtoWithoutOptionalFields() {
    // given
    ProteomicsIndividualDocument document = new ProteomicsIndividualDocument();
    document.setId(new ObjectId());
    document.setEnsemblGeneId("ENSMUSG00000000001");
    document.setGeneSymbol("Gnai3");
    document.setUniprotid("P27144");
    document.setUniqueId("ENSMUSG00000000001P27144");
    document.setDisplaySymbol("Gnai3 (P27144)");
    document.setTissue("Hemibrain");
    document.setName("LOAD2");
    document.setModelGroup(null);
    document.setMatchedControl("LOAD1");
    document.setUnits("Log2 Counts per Million");
    document.setAge("4 months");
    document.setAgeNumeric(4);
    document.setResultOrder(List.of("LOAD1"));
    document.setData(List.of(createIndividualData("sample1", "LOAD1", "Female", 10.5)));

    // when
    ProteomicsIndividualDto dto = mapper.toDto(document);

    // then
    assertThat(dto).isNotNull();
    assertThat(dto.getUniqueId()).isEqualTo("ENSMUSG00000000001P27144");
    assertThat(dto.getGeneSymbol()).isEqualTo("Gnai3");
    assertThat(dto.getModelGroup()).isNull();
    assertThat(dto.getName()).isEqualTo("LOAD2");
  }

  @Test
  @DisplayName("should handle null resultOrder gracefully")
  void shouldHandleNullResultOrderGracefully() {
    // given
    ProteomicsIndividualDocument document = new ProteomicsIndividualDocument();
    document.setId(new ObjectId());
    document.setEnsemblGeneId("ENSMUSG00000000001");
    document.setUniprotid("P27144");
    document.setUniqueId("ENSMUSG00000000001P27144");
    document.setDisplaySymbol("Gnai3 (P27144)");
    document.setTissue("Hemibrain");
    document.setName("LOAD2");
    document.setMatchedControl("LOAD1");
    document.setUnits("Log2 Counts per Million");
    document.setAge("4 months");
    document.setAgeNumeric(4);
    document.setResultOrder(null);
    document.setData(List.of());

    // when
    ProteomicsIndividualDto dto = mapper.toDto(document);

    // then
    assertThat(dto).isNotNull();
    assertThat(dto.getResultOrder()).isEmpty();
  }

  @Test
  @DisplayName("should handle null data gracefully")
  void shouldHandleNullDataGracefully() {
    // given
    ProteomicsIndividualDocument document = new ProteomicsIndividualDocument();
    document.setId(new ObjectId());
    document.setEnsemblGeneId("ENSMUSG00000000001");
    document.setUniprotid("P27144");
    document.setUniqueId("ENSMUSG00000000001P27144");
    document.setDisplaySymbol("Gnai3 (P27144)");
    document.setTissue("Hemibrain");
    document.setName("LOAD2");
    document.setMatchedControl("LOAD1");
    document.setUnits("Log2 Counts per Million");
    document.setAge("4 months");
    document.setAgeNumeric(4);
    document.setResultOrder(List.of());
    document.setData(null);

    // when
    ProteomicsIndividualDto dto = mapper.toDto(document);

    // then
    assertThat(dto).isNotNull();
    assertThat(dto.getData()).isEmpty();
  }

  @Test
  @DisplayName("should return null when document is null")
  void shouldReturnNullWhenDocumentIsNull() {
    // given / when
    ProteomicsIndividualDto dto = mapper.toDto(null);

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
