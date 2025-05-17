package org.sagebionetworks.agora.gene.api.service;

import java.util.List;
import org.sagebionetworks.agora.gene.api.model.document.TeamDocument;
import org.sagebionetworks.agora.gene.api.model.dto.TeamDto;
import org.sagebionetworks.agora.gene.api.model.mapper.TeamMapper;
import org.sagebionetworks.agora.gene.api.model.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TeamService {

  private static final Logger logger = LoggerFactory.getLogger(TeamService.class);

  private TeamMapper teamMapper = new TeamMapper();

  private final TeamRepository teamRepository;

  public TeamService(TeamRepository teamRepository) {
    this.teamRepository = teamRepository;
  }

  public List<TeamDto> getTeams() {
    logger.info("getTeams");
    List<TeamDocument> documents = teamRepository.findAll();
    return teamMapper.convertToDtoList(documents);
  }
}
