package org.sagebionetworks.agora.gene.api.service;

import java.util.List;
import org.sagebionetworks.agora.gene.api.model.document.OverallScoresDocument;
import org.sagebionetworks.agora.gene.api.model.dto.OverallScoresDto;
import org.sagebionetworks.agora.gene.api.model.mapper.OverallScoresMapper;
import org.sagebionetworks.agora.gene.api.model.repository.OverallScoresRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OverallScoresService {

  private static final Logger logger = LoggerFactory.getLogger(OverallScoresService.class);

  private OverallScoresMapper teamMapper = new OverallScoresMapper();

  private final OverallScoresRepository teamRepository;

  public OverallScoresService(OverallScoresRepository teamRepository) {
    this.teamRepository = teamRepository;
  }

  public List<OverallScoresDto> getOverallScores() {
    logger.info("getOverallScoress");
    List<OverallScoresDocument> documents = teamRepository.findAll();
    return teamMapper.convertToDtoList(documents);
  }
}
