package org.sagebionetworks.agora.api.next.model.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.agora.api.next.model.document.DrugDocument;
import org.sagebionetworks.agora.api.next.model.document.DrugNominationEvidenceDocument;
import org.sagebionetworks.agora.api.next.model.document.LinkedTargetDocument;
import org.sagebionetworks.agora.api.next.model.dto.DrugDto;
import org.sagebionetworks.agora.api.next.model.dto.ModalityDto;

class DrugMapperTest {

  private final DrugMapper mapper = new DrugMapper();

  @Test
  @DisplayName("should return null when document is null")
  void shouldReturnNullWhenDocumentIsNull() {
    assertThat(mapper.toDto(null)).isNull();
  }

  @Test
  @DisplayName("should map all fields from document to dto")
  void shouldMapAllFieldsFromDocumentToDto() {
    DrugDocument document = buildFullDocument();

    DrugDto dto = mapper.toDto(document);

    assertThat(dto.getCommonName()).isEqualTo("Agomelatine");
    assertThat(dto.getDescription()).isEqualTo("A melatonin receptor agonist");
    assertThat(dto.getIupacId()).isEqualTo("N-[2-(7-methoxynaphthalen-1-yl)ethyl]acetamide");
    assertThat(dto.getChemblId()).isEqualTo("CHEMBL2105758");
    assertThat(dto.getDrugBankId()).isEqualTo("DB04819");
    assertThat(dto.getAliases()).containsExactly("Agomelatina", "S-20098", "Valdoxan");
    assertThat(dto.getModality()).isEqualTo(ModalityDto.SMALL_MOLECULE);
    assertThat(dto.getYearOfFirstApproval()).isEqualTo(2010);
    assertThat(dto.getMaximumClinicalTrialPhase()).isEqualTo("Phase IV");
    assertThat(dto.getMechanismsOfAction()).containsExactly(
      "Melatonin receptor agonist",
      "Serotonin 2c (5-HT2c) receptor antagonist"
    );
  }

  @Test
  @DisplayName("should map linked targets from document to dto")
  void shouldMapLinkedTargetsFromDocumentToDto() {
    DrugDocument document = buildFullDocument();

    DrugDto dto = mapper.toDto(document);

    assertThat(dto.getLinkedTargets()).hasSize(1);
    assertThat(dto.getLinkedTargets().get(0).getEnsemblGeneId()).isEqualTo("ENSG00000139618");
    assertThat(dto.getLinkedTargets().get(0).getHgncSymbol()).isEqualTo("BRCA1");
  }

  @Test
  @DisplayName("should map drug nominations from document to dto")
  void shouldMapDrugNominationsFromDocumentToDto() {
    DrugDocument document = buildFullDocument();

    DrugDto dto = mapper.toDto(document);

    assertThat(dto.getDrugNominations()).hasSize(1);
    assertThat(dto.getDrugNominations().get(0).getGrantNumber()).isEqualTo("R01AG123456");
    assertThat(dto.getDrugNominations().get(0).getContactPi()).isEqualTo("Dr. Jane Doe");
    assertThat(dto.getDrugNominations().get(0).getEvidence()).isEqualTo("Preclinical studies");
    assertThat(dto.getDrugNominations().get(0).getInitialNomination()).isEqualTo(2025);
    assertThat(dto.getDrugNominations().get(0).getProgram()).isEqualTo("ACTDRx AD");
  }

  @Test
  @DisplayName("should handle null nullable fields gracefully")
  void shouldHandleNullNullableFieldsGracefully() {
    DrugDocument document = new DrugDocument();
    document.setId(new ObjectId());
    document.setCommonName("TestDrug");
    document.setChemblId("CHEMBL123");
    document.setDrugBankId("DB123");

    DrugDto dto = mapper.toDto(document);

    assertThat(dto.getCommonName()).isEqualTo("TestDrug");
    assertThat(dto.getAliases()).isEmpty();
    assertThat(dto.getModality()).isNull();
    assertThat(dto.getLinkedTargets()).isEmpty();
    assertThat(dto.getMechanismsOfAction()).isEmpty();
    assertThat(dto.getDrugNominations()).isEmpty();
  }

  private DrugDocument buildFullDocument() {
    LinkedTargetDocument linkedTarget = new LinkedTargetDocument();
    linkedTarget.setEnsemblGeneId("ENSG00000139618");
    linkedTarget.setHgncSymbol("BRCA1");

    DrugNominationEvidenceDocument nomination = new DrugNominationEvidenceDocument();
    nomination.setGrantNumber("R01AG123456");
    nomination.setContactPi("Dr. Jane Doe");
    nomination.setEvidence("Preclinical studies");
    nomination.setDataUsed("transcriptomics data");
    nomination.setReference("https://doi.org/10.1101/2023.12.26.573348");
    nomination.setComputationalValidationStatus("completed");
    nomination.setExperimentalValidationStatus("completed");
    nomination.setInitialNomination(2025);
    nomination.setProgram("ACTDRx AD");

    DrugDocument document = new DrugDocument();
    document.setId(new ObjectId());
    document.setCommonName("Agomelatine");
    document.setDescription("A melatonin receptor agonist");
    document.setIupacId("N-[2-(7-methoxynaphthalen-1-yl)ethyl]acetamide");
    document.setChemblId("CHEMBL2105758");
    document.setDrugBankId("DB04819");
    document.setAliases(List.of("Agomelatina", "S-20098", "Valdoxan"));
    document.setModality("Small molecule");
    document.setYearOfFirstApproval(2010);
    document.setMaximumClinicalTrialPhase("Phase IV");
    document.setLinkedTargets(List.of(linkedTarget));
    document.setMechanismsOfAction(List.of(
      "Melatonin receptor agonist",
      "Serotonin 2c (5-HT2c) receptor antagonist"
    ));
    document.setDrugNominations(List.of(nomination));
    return document;
  }
}
