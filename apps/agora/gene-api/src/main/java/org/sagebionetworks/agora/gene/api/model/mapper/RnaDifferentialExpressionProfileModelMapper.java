package org.sagebionetworks.agora.gene.api.model.mapper;

import java.util.Map;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileModelDto;

public class RnaDifferentialExpressionProfileModelMapper {

  private static final Map<RnaDifferentialExpressionProfileModelDto, String> modelToStoredValueMap =
    Map.of(
      RnaDifferentialExpressionProfileModelDto.AD_DIAGNOSIS_AOD_MALES_AND_FEMALES,
      "AD Diagnosis (males and females)",
      RnaDifferentialExpressionProfileModelDto.AD_DIAGNOSIS_MALES_AND_FEMALES,
      "AD Diagnosis x AOD (males and females)",
      RnaDifferentialExpressionProfileModelDto.AD_DIAGNOSIS_SEX_FEMALES_ONLY,
      "AD Diagnosis x Sex (males only)",
      RnaDifferentialExpressionProfileModelDto.AD_DIAGNOSIS_SEX_MALES_ONLY,
      "AD Diagnosis (males only)"
    );

  public static String mapToStoredValue(RnaDifferentialExpressionProfileModelDto modelDto) {
    if (modelDto == null) return null;
    return modelToStoredValueMap.getOrDefault(modelDto, modelDto.getValue());
  }
}
