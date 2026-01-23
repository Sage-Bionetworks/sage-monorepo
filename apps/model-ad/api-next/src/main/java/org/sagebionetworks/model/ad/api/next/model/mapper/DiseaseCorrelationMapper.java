package org.sagebionetworks.model.ad.api.next.model.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument;
import org.sagebionetworks.model.ad.api.next.model.document.DiseaseCorrelationDocument.CorrelationResult;
import org.sagebionetworks.model.ad.api.next.model.dto.CorrelationResultDto;
import org.sagebionetworks.model.ad.api.next.model.dto.DiseaseCorrelationDto;
import org.sagebionetworks.model.ad.api.next.model.dto.SexDto;
import org.sagebionetworks.model.ad.api.next.util.EnumConverter;
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
      getCompositeId(document),
      document.getName(),
      document.getMatchedControl(),
      document.getModelType(),
      modifiedGenes,
      document.getCluster(),
      document.getAge(),
      document.getAgeNumeric(),
      EnumConverter.toSexDto(document.getSex(), "disease correlation record")
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

  private String getCompositeId(DiseaseCorrelationDocument document) {
    String name = document.getName();
    String age = document.getAge();
    String sex = document.getSex();

    return String.format("%s~%s~%s", name, age, sex);
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
