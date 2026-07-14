package org.sagebionetworks.model.ad.api.next.model.mapper;

import org.sagebionetworks.model.ad.api.next.model.document.GeneticInfo;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneticInfoDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class GeneticInfoMapper {

  public GeneticInfoDto toDto(@Nullable GeneticInfo geneticInfo) {
    if (geneticInfo == null) {
      return null;
    }

    return new GeneticInfoDto(
      geneticInfo.getModifiedGene(),
      geneticInfo.getEnsemblGeneId(),
      geneticInfo.getAlleleType()
    )
      .allele(geneticInfo.getAllele())
      .mgiAlleleId(geneticInfo.getMgiAlleleId());
  }
}
