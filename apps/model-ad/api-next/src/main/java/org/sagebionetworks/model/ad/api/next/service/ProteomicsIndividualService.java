package org.sagebionetworks.model.ad.api.next.service;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.document.ProteomicsIndividualDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelIdentifierTypeDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ProteomicsIndividualFilterQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.ProteomicsIndividualMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.ProteomicsIndividualRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.PROTEOMICS_INDIVIDUAL)
public class ProteomicsIndividualService {

  private final ProteomicsIndividualRepository repository;
  private final ProteomicsIndividualMapper proteomicsIndividualMapper;

  @Cacheable(
    key = "'proteomicsIndividual:' + #query.tissue + ':' + " +
    "#query.modelIdentifier + ':' + #query.modelIdentifierType + ':' + #query.uniqueId"
  )
  public List<ProteomicsIndividualDto> getProteomicsIndividual(
    ProteomicsIndividualFilterQueryDto query
  ) {
    String modelIdentifier = query.getModelIdentifier();
    ModelIdentifierTypeDto modelIdentifierType = query.getModelIdentifierType();
    String tissue = query.getTissue();
    String uniqueId = query.getUniqueId();

    List<ProteomicsIndividualDocument> documents;
    if (modelIdentifierType == ModelIdentifierTypeDto.NAME) {
      documents = repository.findByUniqueIdAndNameAndTissue(uniqueId, modelIdentifier, tissue);
    } else if (modelIdentifierType == ModelIdentifierTypeDto.MODEL_GROUP) {
      documents = repository.findByUniqueIdAndModelGroupAndTissue(
        uniqueId,
        modelIdentifier,
        tissue
      );
    } else {
      throw new IllegalArgumentException("Invalid modelIdentifierType: " + modelIdentifierType);
    }

    return documents
      .stream()
      .sorted(Comparator.comparing(ProteomicsIndividualDocument::getAgeNumeric))
      .map(proteomicsIndividualMapper::toDto)
      .toList();
  }
}
