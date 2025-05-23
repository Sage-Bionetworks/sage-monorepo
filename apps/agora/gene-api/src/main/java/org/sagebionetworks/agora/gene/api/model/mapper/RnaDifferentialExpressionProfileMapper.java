package org.sagebionetworks.agora.gene.api.model.mapper;

import org.sagebionetworks.agora.gene.api.model.document.RnaDifferentialExpressionProfileDocument;
import org.sagebionetworks.agora.gene.api.model.dto.RnaDifferentialExpressionProfileDto;
import org.sagebionetworks.util.model.mapper.BaseMapper;
import org.springframework.beans.BeanUtils;

public class RnaDifferentialExpressionProfileMapper
  extends BaseMapper<
    RnaDifferentialExpressionProfileDocument,
    RnaDifferentialExpressionProfileDto
  > {

  private TissueMapper tissueMapper = new TissueMapper();
  private NominationsMapper nominationsMapper = new NominationsMapper();

  @Override
  public RnaDifferentialExpressionProfileDocument convertToEntity(
    RnaDifferentialExpressionProfileDto dto,
    Object... args
  ) {
    RnaDifferentialExpressionProfileDocument document =
      RnaDifferentialExpressionProfileDocument.builder().build();
    if (dto != null) {
      BeanUtils.copyProperties(dto, document);
    }
    return document;
  }

  @Override
  public RnaDifferentialExpressionProfileDto convertToDto(
    RnaDifferentialExpressionProfileDocument document,
    Object... args
  ) {
    RnaDifferentialExpressionProfileDto dto = new RnaDifferentialExpressionProfileDto();
    if (document != null) {
      BeanUtils.copyProperties(document, dto);
      dto.setTissues(tissueMapper.convertToDtoList(document.getTissues()));
      dto.setNominations(nominationsMapper.convertToDto(document.getNominations()));
    }
    return dto;
  }
}
