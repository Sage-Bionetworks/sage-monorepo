package org.sagebionetworks.model.ad.api.next.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.model.ad.api.next.configuration.CacheNames;
import org.sagebionetworks.model.ad.api.next.model.document.GeneExpressionIndividualDocument;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionIndividualDto;
import org.sagebionetworks.model.ad.api.next.model.dto.GeneExpressionIndividualFilterQueryDto;
import org.sagebionetworks.model.ad.api.next.model.dto.ModelIdentifierTypeDto;
import org.sagebionetworks.model.ad.api.next.model.mapper.GeneExpressionIndividualMapper;
import org.sagebionetworks.model.ad.api.next.model.repository.GeneExpressionIndividualRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
@CacheConfig(cacheNames = CacheNames.GENE_EXPRESSION_INDIVIDUAL)
public class GeneExpressionIndividualService {

  private final GeneExpressionIndividualRepository repository;
  private final GeneExpressionIndividualMapper geneExpressionIndividualMapper;

  @Cacheable(
    key = "'geneExpressionIndividual:' + #query.tissue + ':' + " +
    "#query.modelIdentifier + ':' + #query.modelIdentifierType + ':' + #query.ensemblGeneId"
  )
  public List<GeneExpressionIndividualDto> getGeneExpressionIndividual(
    GeneExpressionIndividualFilterQueryDto query
  ) {
    String modelIdentifier = query.getModelIdentifier();
    ModelIdentifierTypeDto modelIdentifierType = query.getModelIdentifierType();
    String tissue = query.getTissue();
    String ensemblGeneId = query.getEnsemblGeneId();

    List<GeneExpressionIndividualDocument> documents;
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
          GeneExpressionIndividualDocument::getAgeNumeric,
          Comparator.nullsLast(Comparator.naturalOrder())
        )
      )
      .map(geneExpressionIndividualMapper::toDto)
      .collect(Collectors.collectingAndThen(Collectors.toList(), List::copyOf));
  }
}
