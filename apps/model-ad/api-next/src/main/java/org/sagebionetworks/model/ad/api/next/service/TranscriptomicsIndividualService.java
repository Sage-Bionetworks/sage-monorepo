package org.sagebionetworks.model.ad.api.next.service;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.document.TranscriptomicsIndividualDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelIdentifierTypeDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.TranscriptomicsIndividualFilterQueryDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.TranscriptomicsIndividualMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.TranscriptomicsIndividualRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.TRANSCRIPTOMICS_INDIVIDUAL)
public class TranscriptomicsIndividualService {

  private final TranscriptomicsIndividualRepository repository;
  private final TranscriptomicsIndividualMapper transcriptomicsIndividualMapper;

  @Cacheable(
    key = "'transcriptomicsIndividual:' + #query.tissue + ':' + " +
    "#query.modelIdentifier + ':' + #query.modelIdentifierType + ':' + #query.ensemblGeneId"
  )
  public List<TranscriptomicsIndividualDto> getTranscriptomicsIndividual(
    TranscriptomicsIndividualFilterQueryDto query
  ) {
    String modelIdentifier = query.getModelIdentifier();
    ModelIdentifierTypeDto modelIdentifierType = query.getModelIdentifierType();
    String tissue = query.getTissue();
    String ensemblGeneId = query.getEnsemblGeneId();

    List<TranscriptomicsIndividualDocument> documents;
    if (modelIdentifierType == ModelIdentifierTypeDto.NAME) {
      documents = repository.findByEnsemblGeneIdAndNameAndTissue(
        ensemblGeneId,
        modelIdentifier,
        tissue
      );
    } else if (modelIdentifierType == ModelIdentifierTypeDto.MODEL_GROUP) {
      documents = repository.findByEnsemblGeneIdAndModelGroupAndTissue(
        ensemblGeneId,
        modelIdentifier,
        tissue
      );
    } else {
      throw new IllegalArgumentException("Invalid modelIdentifierType: " + modelIdentifierType);
    }

    return documents
      .stream()
      .sorted(
        Comparator.comparing(
          TranscriptomicsIndividualDocument::getAgeNumeric,
          Comparator.nullsLast(Comparator.naturalOrder())
        )
      )
      .map(transcriptomicsIndividualMapper::toDto)
      .toList();
  }
}
