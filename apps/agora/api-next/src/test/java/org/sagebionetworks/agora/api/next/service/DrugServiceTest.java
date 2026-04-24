package org.sagebionetworks.agora.api.next.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.agora.api.next.model.document.DrugDocument;
import org.sagebionetworks.agora.api.next.model.dto.DrugDto;
import org.sagebionetworks.agora.api.next.model.mapper.DrugMapper;
import org.sagebionetworks.agora.api.next.model.repository.DrugRepository;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class DrugServiceTest {

  @Mock
  private DrugRepository repository;

  private DrugService service;

  @BeforeEach
  void setUp() {
    service = new DrugService(repository, new DrugMapper());
  }

  @Test
  @DisplayName("should return drug dto when drug is found")
  void shouldReturnDrugDtoWhenDrugIsFound() {
    String chemblId = "CHEMBL2105758";
    DrugDocument document = buildDocument(chemblId);
    when(repository.findByChemblId(chemblId)).thenReturn(Optional.of(document));

    DrugDto result = service.loadDrug(chemblId);

    assertThat(result).isNotNull();
    assertThat(result.getChemblId()).isEqualTo(chemblId);
    assertThat(result.getCommonName()).isEqualTo("Agomelatine");
    verify(repository).findByChemblId(chemblId);
  }

  @Test
  @DisplayName("should throw not found exception when drug does not exist")
  void shouldThrowNotFoundExceptionWhenDrugDoesNotExist() {
    String chemblId = "CHEMBL_UNKNOWN";
    when(repository.findByChemblId(chemblId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.loadDrug(chemblId))
      .isInstanceOf(ResponseStatusException.class)
      .hasMessageContaining("Drug not found: " + chemblId);

    verify(repository).findByChemblId(chemblId);
  }

  private DrugDocument buildDocument(String chemblId) {
    DrugDocument document = new DrugDocument();
    document.setId(new ObjectId());
    document.setCommonName("Agomelatine");
    document.setChemblId(chemblId);
    document.setDrugBankId("DB04819");
    document.setAliases(List.of("Agomelatina", "S-20098"));
    document.setModality("Small molecule");
    document.setDrugNominations(List.of());
    return document;
  }
}
