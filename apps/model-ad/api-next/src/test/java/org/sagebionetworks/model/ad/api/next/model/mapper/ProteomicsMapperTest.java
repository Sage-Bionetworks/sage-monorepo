package org.sagebionetworks.model.ad.api.next.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.model.ad.api.next.exception.DataIntegrityException;
import org.sagebionetworks.model.ad.api.next.model.document.FoldChangeResult;
import org.sagebionetworks.model.ad.api.next.model.document.Link;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsDto;

class ProteomicsMapperTest {

  private static final String UNIQUE_ID = "ENSMUSG00000000001P27144";
  private static final String MODEL_NAME = "LOAD2";
  private static final String SEX = "Female";

  private ProteomicsMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ProteomicsMapper(new LinkMapper(), new FoldChangeMapper());
  }

  @Test
  @DisplayName("should return null when document is null")
  void shouldReturnNullWhenDocumentIsNull() {
    assertThat(mapper.toDto(null)).isNull();
  }

  @Test
  @DisplayName("should build composite id from unique id, name, and sex")
  void shouldBuildCompositeIdFromUniqueIdNameAndSex() {
    ProteomicsDto dto = mapper.toDto(buildDocument());

    assertThat(dto.getCompositeId()).isEqualTo(UNIQUE_ID + "~" + MODEL_NAME + "~" + SEX);
  }

  @Test
  @DisplayName("should map identifying and model fields")
  void shouldMapIdentifyingAndModelFields() {
    ProteomicsDto dto = mapper.toDto(buildDocument());

    assertThat(dto.getEnsemblGeneId()).isEqualTo("ENSMUSG00000000001");
    assertThat(dto.getGeneSymbol()).isEqualTo("Gnai3");
    assertThat(dto.getUniprotid()).isEqualTo("P27144");
    assertThat(dto.getUniqueId()).isEqualTo(UNIQUE_ID);
    assertThat(dto.getDisplaySymbol()).isEqualTo("Gnai3 (P27144)");
    assertThat(dto.getBiodomains()).containsExactly("Apoptosis", "Synapse");
    assertThat(dto.getName().getLinkText()).isEqualTo(MODEL_NAME);
    assertThat(dto.getName().getLinkUrl()).isEqualTo("models/LOAD2");
    assertThat(dto.getMatchedControl()).isEqualTo("C57BL/6J");
    assertThat(dto.getModelGroup()).isNull();
    assertThat(dto.getModelType()).isEqualTo("Late Onset AD");
    assertThat(dto.getTissue()).isEqualTo("Hemibrain");
    assertThat(dto.getSex().getValue()).isEqualTo(SEX);
  }

  @Test
  @DisplayName("should pass display symbol through without falling back to ensembl gene id")
  void shouldPassDisplaySymbolThroughWithoutFallback() {
    ProteomicsDocument document = buildDocument();
    document.setGeneSymbol("");
    document.setDisplaySymbol("ENSMUSG00000000001 (P27144)");

    ProteomicsDto dto = mapper.toDto(document);

    assertThat(dto.getGeneSymbol()).isEmpty();
    assertThat(dto.getDisplaySymbol()).isEqualTo("ENSMUSG00000000001 (P27144)");
  }

  @Test
  @DisplayName("should default biodomains to empty list when absent")
  void shouldDefaultBiodomainsToEmptyListWhenAbsent() {
    ProteomicsDocument document = buildDocument();
    document.setBiodomains(null);

    assertThat(mapper.toDto(document).getBiodomains()).isEmpty();
  }

  @Test
  @DisplayName("should convert fold change values to big decimal for all four age buckets")
  void shouldConvertFoldChangeValuesForAllFourAgeBuckets() {
    ProteomicsDocument document = buildDocument();
    document.setFourMonths(foldChange(0.01167d, 0.7812d));
    document.setTwelveMonths(foldChange(-0.5d, 0.04d));
    document.setEighteenMonths(foldChange(1.25d, 0.002d));
    document.setTwentyFourMonths(foldChange(2.5d, 0.0001d));

    ProteomicsDto dto = mapper.toDto(document);

    assertThat(dto.get4months().getLog2Fc()).isEqualTo(BigDecimal.valueOf(0.01167d));
    assertThat(dto.get4months().getAdjPVal()).isEqualTo(BigDecimal.valueOf(0.7812d));
    assertThat(dto.get12months().getLog2Fc()).isEqualTo(BigDecimal.valueOf(-0.5d));
    assertThat(dto.get18months().getLog2Fc()).isEqualTo(BigDecimal.valueOf(1.25d));
    assertThat(dto.get24months().getLog2Fc()).isEqualTo(BigDecimal.valueOf(2.5d));
    assertThat(dto.get24months().getAdjPVal()).isEqualTo(BigDecimal.valueOf(0.0001d));
  }

  @Test
  @DisplayName("should omit age bucket when document has no fold change result")
  void shouldOmitAgeBucketWhenDocumentHasNoFoldChangeResult() {
    ProteomicsDto dto = mapper.toDto(buildDocument());

    assertThat(dto.get4months()).isNull();
    assertThat(dto.get12months()).isNull();
    assertThat(dto.get18months()).isNull();
    assertThat(dto.get24months()).isNull();
  }

  @Test
  @DisplayName("should omit age bucket when fold change values are incomplete")
  void shouldOmitAgeBucketWhenFoldChangeValuesAreIncomplete() {
    ProteomicsDocument document = buildDocument();
    document.setFourMonths(FoldChangeResult.builder().log2Fc(0.5d).build());
    document.setTwelveMonths(FoldChangeResult.builder().adjPVal(0.5d).build());

    ProteomicsDto dto = mapper.toDto(document);

    assertThat(dto.get4months()).isNull();
    assertThat(dto.get12months()).isNull();
  }

  @Test
  @DisplayName("should throw exception when sex is not a recognized value")
  void shouldThrowExceptionWhenSexIsNotRecognized() {
    ProteomicsDocument document = buildDocument();
    document.setSex("Unknown");

    assertThatThrownBy(() -> mapper.toDto(document))
      .isInstanceOf(DataIntegrityException.class)
      .hasMessageContaining("proteomics record");
  }

  private FoldChangeResult foldChange(Double log2Fc, Double adjustedPvalue) {
    return FoldChangeResult.builder().log2Fc(log2Fc).adjPVal(adjustedPvalue).build();
  }

  private ProteomicsDocument buildDocument() {
    ProteomicsDocument document = new ProteomicsDocument();
    document.setEnsemblGeneId("ENSMUSG00000000001");
    document.setGeneSymbol("Gnai3");
    document.setUniprotid("P27144");
    document.setUniqueId(UNIQUE_ID);
    document.setDisplaySymbol("Gnai3 (P27144)");
    document.setBiodomains(List.of("Apoptosis", "Synapse"));
    document.setName(Link.builder().linkText(MODEL_NAME).linkUrl("models/LOAD2").build());
    document.setMatchedControl("C57BL/6J");
    document.setModelGroup(null);
    document.setModelType("Late Onset AD");
    document.setTissue("Hemibrain");
    document.setSex(SEX);
    return document;
  }
}
