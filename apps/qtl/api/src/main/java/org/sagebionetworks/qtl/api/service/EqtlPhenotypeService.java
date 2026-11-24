package org.sagebionetworks.qtl.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.qtl.api.configuration.CacheNames;
import org.sagebionetworks.qtl.api.exception.EqtlPhenotypeNotFoundException;
import org.sagebionetworks.qtl.api.model.dto.EqtlPhenotypeDto;
import org.sagebionetworks.qtl.api.model.dto.EqtlPhenotypeQueryDto;
import org.sagebionetworks.qtl.api.model.mapper.EqtlPhenotypeMapper;
import org.sagebionetworks.qtl.api.model.repository.EqtlPhenotypeRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.EQTL_PHENOTYPE)
public class EqtlPhenotypeService {

  private final EqtlPhenotypeRepository repository;
  private final EqtlPhenotypeMapper eqtlPhenotypeMapper;

  @Cacheable(key = "#query.variant + '~' + #query.ensemblGeneId + '~' + #query.cellTypeId")
  public EqtlPhenotypeDto getEqtlPhenotype(EqtlPhenotypeQueryDto query) {
    String variant = query.getVariant();
    String ensemblGeneId = query.getEnsemblGeneId();
    String cellTypeId = query.getCellTypeId();

    return repository
      .findByVariantAndEnsemblGeneIdAndCellTypeId(variant, ensemblGeneId, cellTypeId)
      .map(eqtlPhenotypeMapper::toDto)
      .orElseThrow(() -> new EqtlPhenotypeNotFoundException(variant, ensemblGeneId, cellTypeId));
  }
}
