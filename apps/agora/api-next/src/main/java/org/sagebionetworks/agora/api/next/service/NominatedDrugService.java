package org.sagebionetworks.agora.api.next.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.agora.api.next.configuration.CacheNames;
import org.sagebionetworks.agora.api.next.exception.NominatedDrugNotFoundException;
import org.sagebionetworks.agora.api.next.model.document.NominatedDrugDocument;
import org.sagebionetworks.agora.api.next.model.dto.NominatedDrugDto;
import org.sagebionetworks.agora.api.next.model.mapper.NominatedDrugMapper;
import org.sagebionetworks.agora.api.next.model.repository.NominatedDrugRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.NOMINATED_DRUG)
public class NominatedDrugService {

  private final NominatedDrugRepository repository;
  private final NominatedDrugMapper nominatedDrugMapper;

  @Cacheable
  public List<NominatedDrugDto> listNominatedDrugs() {
    List<NominatedDrugDocument> documents = repository.findAll();

    if (documents == null || documents.isEmpty()) {
      throw new NominatedDrugNotFoundException();
    }

    return documents.stream().map(nominatedDrugMapper::toDto).toList();
  }
}
