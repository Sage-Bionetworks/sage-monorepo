package org.sagebionetworks.agora.gene.api.service;

import java.util.List;
import org.sagebionetworks.agora.gene.api.model.document.BioDomainsDocument;
import org.sagebionetworks.agora.gene.api.model.dto.BioDomainsDto;
import org.sagebionetworks.agora.gene.api.model.mapper.BioDomainsMapper;
import org.sagebionetworks.agora.gene.api.model.repository.BioDomainsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BioDomainsService {

  private static final Logger logger = LoggerFactory.getLogger(BioDomainsService.class);

  private BioDomainsMapper teamMapper = new BioDomainsMapper();

  private final BioDomainsRepository teamRepository;

  public BioDomainsService(BioDomainsRepository teamRepository) {
    this.teamRepository = teamRepository;
  }

  public List<BioDomainsDto> getBioDomains() {
    logger.info("getBioDomains");
    List<BioDomainsDocument> documents = teamRepository.findAll();
    return teamMapper.convertToDtoList(documents);
  }
}
