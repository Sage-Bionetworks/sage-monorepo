package org.sagebionetworks.qtl.api.service;

import lombok.RequiredArgsConstructor;
import org.sagebionetworks.qtl.api.model.document.DataVersionDocument;
import org.sagebionetworks.qtl.api.model.dto.DataVersionDto;
import org.sagebionetworks.qtl.api.model.mapper.DataVersionMapper;
import org.sagebionetworks.qtl.api.model.repository.DataVersionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class DataVersionService {

  private final DataVersionRepository repository;
  private final DataVersionMapper dataVersionMapper;

  public DataVersionDto loadDataVersion() {
    DataVersionDocument document = repository
      .findFirstBy()
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Data version not found")
      );

    return dataVersionMapper.toDto(document);
  }
}
