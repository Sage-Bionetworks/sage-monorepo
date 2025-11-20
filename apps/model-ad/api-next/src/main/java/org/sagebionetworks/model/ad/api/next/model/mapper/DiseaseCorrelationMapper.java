package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.exception.DataIntegrityException;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument.CorrelationResult;
import org.sagebionetworks.model.ad.api.next.model.dto.CorrelationResultDto;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SexDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class DiseaseCorrelationMapper {

  public DiseaseCorrelationDto toDto(@Nullable DiseaseCorrelationDocument document) {
    if (document == null) {
      return null;
    }

    List<String> modifiedGenes = document.getModifiedGenes() == null
      ? List.of()
      : List.copyOf(document.getModifiedGenes());

    DiseaseCorrelationDto dto = new DiseaseCorrelationDto(
      document.getId() != null ? document.getId().toHexString() : null,
      document.getName(),
      document.getMatchedControl(),
      document.getModelType(),
      modifiedGenes,
      document.getCluster(),
      document.getAge(),
      toSexDto(document.getSex())
    );

    dto.setCBE(toCorrelationDto(document.getCbe()));
    dto.setDLPFC(toCorrelationDto(document.getDlpfc()));
    dto.setFP(toCorrelationDto(document.getFp()));
    dto.setIFG(toCorrelationDto(document.getIfg()));
    dto.setPHG(toCorrelationDto(document.getPhg()));
    dto.setSTG(toCorrelationDto(document.getStg()));
    dto.setTCX(toCorrelationDto(document.getTcx()));
    return dto;
  }

  private SexDto toSexDto(@Nullable String value) {
    if (value == null) {
      throw new DataIntegrityException("Missing sex value in disease correlation record");
    }
    try {
      return SexDto.fromValue(value);
    } catch (IllegalArgumentException ex) {
      throw new DataIntegrityException(
        "Unexpected sex value '" + value + "' in disease correlation record",
        ex
      );
    }
  }

  private @Nullable CorrelationResultDto toCorrelationDto(@Nullable CorrelationResult document) {
    if (document == null) {
      return null;
    }
    Double correlation = document.getCorrelation();
    Double adjustedPvalue = document.getAdjustedPvalue();
    if (correlation == null || adjustedPvalue == null) {
      return null;
    }
    return new CorrelationResultDto(
      BigDecimal.valueOf(correlation),
      BigDecimal.valueOf(adjustedPvalue)
    );
  }
}
