package org.sagebionetworks.agora.api.next.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import org.sagebionetworks.agora.api.next.service.DrugService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class DrugApiDelegateImplTest {

  @Mock
  private DrugRepository repository;

  private DrugApiDelegateImpl delegate;

  @BeforeEach
  void setUp() {
    DrugService service = new DrugService(repository, new DrugMapper());
    delegate = new DrugApiDelegateImpl(service);
  }

  @Test
  @DisplayName("should return drug with no-cache headers when found")
  void shouldReturnDrugWithNoCacheHeadersWhenFound() {
    String chemblId = "CHEMBL2105758";
    DrugDocument document = buildDocument(chemblId);
    when(repository.findByChemblId(chemblId)).thenReturn(Optional.of(document));

    ResponseEntity<DrugDto> response = delegate.getDrug(chemblId);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getChemblId()).isEqualTo(chemblId);
    assertThat(response.getBody().getCommonName()).isEqualTo("Agomelatine");

    HttpHeaders headers = response.getHeaders();
    assertThat(headers.getCacheControl()).isEqualTo("no-cache, no-store, must-revalidate");
    assertThat(headers.getPragma()).contains("no-cache");
    assertThat(headers.getExpires()).isZero();
    assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
  }

  @Test
  @DisplayName("should throw not found exception when drug does not exist")
  void shouldThrowNotFoundExceptionWhenDrugDoesNotExist() {
    String chemblId = "CHEMBL_UNKNOWN";
    when(repository.findByChemblId(chemblId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> delegate.getDrug(chemblId))
      .isInstanceOf(ResponseStatusException.class)
      .hasMessageContaining("Drug not found: " + chemblId);
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
