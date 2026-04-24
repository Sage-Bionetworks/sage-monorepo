package org.sagebionetworks.agora.api.next.service;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.agora.api.next.configuration.CacheNames;
import org.sagebionetworks.agora.api.next.model.document.DrugDocument;
import org.sagebionetworks.agora.api.next.model.dto.DrugDto;
import org.sagebionetworks.agora.api.next.model.mapper.DrugMapper;
import org.sagebionetworks.agora.api.next.model.repository.DrugRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
@CacheConfig(cacheNames = CacheNames.DRUG)
public class DrugService {

  private final DrugRepository repository;
  private final DrugMapper drugMapper;

  @Cacheable(key = "#chemblId")
  public DrugDto loadDrug(String chemblId) {
    DrugDocument document = repository
      .findByChemblId(chemblId)
      .orElseThrow(() ->
        new ResponseStatusException(HttpStatus.NOT_FOUND, "Drug not found: " + chemblId)
      );

    return drugMapper.toDto(document);
  }
}
